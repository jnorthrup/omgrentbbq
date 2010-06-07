package com.omgrentbbq.shared.model;

import java.io.Serializable;

@KeyProperty("id")
public class UserSession extends Memento {

    public boolean isUserAdmin() {
        return Boolean.TRUE == (Boolean.valueOf(String.valueOf($("userAdmin"))));
    }
    public Serializable getUser(){return $("user");}
}
