package ru.stepup.task3;

public class Starter {
    public static void main(String[] args) throws InterruptedException {
        CacheCleaner.createJob(200);
        CacheCleaner.startJob();

        Fractionable f = new Fraction(1, 2);
        Fractionable cacheF = CacheProxy.getCachedInstance(f);

        System.out.println(cacheF.doubleValue());
        System.out.println(cacheF.doubleValue());
        cacheF.setDenum(5);
        System.out.println(cacheF.doubleValue());
        cacheF.setDenum(2);
        System.out.println(cacheF.doubleValue());
        Thread.sleep(1000);
        System.out.println(cacheF.doubleValue());

        CacheCleaner.stopJob();
    }
}
