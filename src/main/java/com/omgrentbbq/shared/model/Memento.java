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
public class Memento implements Serializable {

    /**
     * all purpose parent/key reference
     * might be a key or might be a pair<String,Memento > used for translating embedded fields from another Memento
     */
    public Serializable $$;


    public <T extends Serializable>T $(String k) {
        if ($$ instanceof Pair) {
            Pair<String,   Memento> pair = (Pair<String,   Memento>) $$;
            return (T)pair.getSecond().$(pair.getFirst() + "." + k);
        } else
            return (T) $.get(k);
    }

    public <T extends Serializable> T $(String k,Serializable t) {
        if ($$ instanceof Pair) {
            Pair<String,  Memento> pair = (Pair<String,  Memento>) $$;
            return (T)pair.getSecond().$(pair.getFirst() + "." + k, t);
        } else
            return (T) $.put(k, t);
    }


    public String getType() {
/*        final String[] strings = getClass().getName().split(".");
        return strings[strings.length-1];*/
        return getClass().getName();
    }

    public Map<String, Serializable> $ =
            new LinkedHashMap<String, Serializable>();

    @Override
    public String toString() {
        final String[] strings = getClass().getName().split("\\.");
        return strings[strings.length - 1] + "{" +
                "$$=" + $$() +
                ", $=" + $ +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this != o) {
            if (o instanceof Memento) {

                Memento memento = (Memento) o;

                return !($$() != null ? !$$().equals(memento.$$()) : memento.$$() != null) && !($ != null ? !$.equals(memento.$) : memento.$ != null);
            }
            return false;
        }
        return true;

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

