package com.omgrentbbq.shared.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:09:46 PM
 */
public class Pair<T1,T2> implements Serializable {
   public  Pair() {
    }

    public Pair(Serializable... a) {

        this.a = a;
    }

    Serializable a[ ]= new Serializable[0];
    public T1 getFirst(){
           return (T1) a[0];
       }
    public T2 getSecond(){
           return (T2) a[1];
       }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair pair = (Pair) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(a, pair.a)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return a != null ? Arrays.hashCode(a) : 0;
    }
}
