package com.omgrentbbq.shared.model;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copyright 2 010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:49:46 PM
 */
public class Memento implements Serializable{
    private Serializable $$;


    public <T extends Serializable> T $(String $$){
        return (T) $.get($$);
    }

    public String getType() {
        return getClass().getName();
    }

    public Map<String, Serializable> $ =
            new LinkedHashMap<String, Serializable>();

    @Override
    public String toString() {
        final String[] strings = getClass().getName().split("\\.");
        return strings[strings.length-1] +"{" +
                "$$=" + $$() +
                ", $=" + $ +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Memento)) return false;

        Memento memento = (Memento) o;

        return !($$() != null ? !$$().equals(memento.$$()) : memento.$$() != null) && !($ != null ? !$.equals(memento.$) : memento.$ != null);

    }

    @Override
    public int hashCode() {
        int result = $$() != null ? $$().hashCode() : 0;
        result = 31 * result + ($ != null ? $.hashCode() : 0);
        return result;
    }

    public Serializable $$() {
        return $$;
    }

    public void $$(Serializable $$) {
        this.$$ = $$;
    }
}

