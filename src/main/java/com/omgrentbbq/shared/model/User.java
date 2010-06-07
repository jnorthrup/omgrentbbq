package com.omgrentbbq.shared.model;


@KeyProperty("userId")
public class User extends Memento {
    public String getNickname() {
        return $("nickname");
    }
}
