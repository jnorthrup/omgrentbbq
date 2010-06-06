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
    private Serializable key;



    public String getType() {
        return getClass().getName();
    }

    public Map<String, Serializable> properties =
            new LinkedHashMap<String, Serializable>();

    @Override
    public String toString() {
        final String[] strings = getClass().getName().split("\\.");
        return strings[strings.length-1] +"{" +
                "key=" + getKey() +
                ", properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Memento)) return false;

        Memento memento = (Memento) o;

        if (getKey() != null ? !getKey().equals(memento.getKey()) : memento.getKey() != null) return false;
        if (properties != null ? !properties.equals(memento.properties) : memento.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    public Serializable getKey() {
        return key;
    }

    public void setKey(Serializable key) {
        this.key = key;
    }
}

