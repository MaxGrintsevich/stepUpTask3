package ru.stepup.task3;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Capture{
    Map<String, Object> fields = new HashMap<>();

    public Capture(Object obj)  {
        //fields.put("reference", obj);
        for(Field f:obj.getClass().getDeclaredFields()){
            f.setAccessible(true);
            try {
                this.fields.put(f.getName(), f.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Capture{" +
                "fields=" + fields +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capture)) return false;

        return this.fields.entrySet().stream()
                .allMatch(e -> e.getValue().equals(((Capture) o).fields.get(e.getKey())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}

