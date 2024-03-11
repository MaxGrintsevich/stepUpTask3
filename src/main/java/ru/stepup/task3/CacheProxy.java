package ru.stepup.task3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class CacheObject {
    Object value;
    long expirationTime;
    CacheObject(Object value, long validTime){
        this.value = value;
        this.expirationTime = System.currentTimeMillis() + validTime;
    }

    public Object getValue() {
        return value;
    }

    public void renewExpiration(long validTime) {
        this.expirationTime = System.currentTimeMillis() + validTime;
    }

    public boolean isExpired(){
        return System.currentTimeMillis() > this.expirationTime;
    }
}

class CacheProxy implements InvocationHandler, Cleanable {

    private Object obj;
    private Map<Capture, Map<Method, CacheObject>> cache = new ConcurrentHashMap<>();
    private Capture capture;

    CacheProxy(Object obj){
        this.obj = obj;
        this.capture = new Capture(obj);
        this.cache.put(capture, new HashMap<>());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m =obj.getClass().getMethod(method.getName(), method.getParameterTypes());

        if (m.isAnnotationPresent(Cache.class)){
            Cache cacheAnnotation = m.getAnnotation(Cache.class);
            if (! cache.containsKey(capture)){
                cache.put(capture, new HashMap<>());
            }
            Map<Method, CacheObject> localCache = new HashMap<>(cache.get(capture));
            if (!localCache.containsKey(m)){
                localCache.put(m, new CacheObject(method.invoke(obj, args), cacheAnnotation.value()));
            }
            localCache.get(m).renewExpiration(cacheAnnotation.value());
            cache.put(capture, localCache);
            return localCache.get(m).value;

        }

        Object result = method.invoke(obj, args);

        if (m.isAnnotationPresent(Mutator.class)){
            capture = new Capture(obj);
            if (! cache.containsKey(capture)){
                cache.put(capture, new HashMap<>());
            }
        }
        return result;
    }

    public boolean clean(){
        boolean updated = false;
        for (Capture capture: cache.keySet()){
            Map<Method, CacheObject> localCache = new HashMap<>(cache.get(capture));
            for (Method method : localCache.keySet()){
                if (localCache.get(method).isExpired()){
                    localCache.remove(method);
                    updated = true;
                }
            }
            if (updated) {
                if (localCache.isEmpty()) {
                    cache.remove(capture);
                } else {
                    cache.replace(capture, localCache);
                }
            }
        }
        return cache.isEmpty();
    }
    public static <T> T getCachedInstance(T f ){
        ClassLoader ldr = f.getClass().getClassLoader();
        Class[] i = f.getClass().getInterfaces();
        CacheProxy c = new CacheProxy(f);
        CacheCleaner.submitCleaning(c);
        T ff = (T) Proxy.newProxyInstance(ldr, i, c);
        return ff;
    }

    @Override
    public String toString() {
        return capture.toString();
    }
}

