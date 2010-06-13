package com.omgrentbbq.shared.model;

public class Group extends Memento {
    public Group(String name) {
        $("name", name);
    }

    public Group() {
    }

    public String getName() {
        return $("name");
    }

    public boolean isImmutable() {
        return Boolean.TRUE.toString().equals(String.valueOf($("immutable")));
    }

    public boolean isPrivacy() {
        return Boolean.TRUE.toString().equals(String.valueOf($("privacy")));
    }

    public void setName(String name) {
        $("name", name);
    }
}
