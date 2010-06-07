package com.omgrentbbq.shared.model;

import java.io.Serializable;

public class Group extends Memento{
    public Group(String name) {
        $("name",name);
    }

    public Group() {
    }

    public String getName() {
        return $("name");
    }

    public boolean isImmutable() {
        return Boolean.valueOf( String.valueOf( $("immutable")));
    }
}
