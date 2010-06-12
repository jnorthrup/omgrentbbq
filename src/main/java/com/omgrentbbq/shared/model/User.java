package com.omgrentbbq.shared.model;


@KeyProperty("userId")
public class User extends Memento {
    public String getNickname() {
        return $("nickname");
    }

    public void setEmail(String s) {
        $("email", s);
    }

    public void setUserId(long l) {
        $("userId", l);
    }
}
