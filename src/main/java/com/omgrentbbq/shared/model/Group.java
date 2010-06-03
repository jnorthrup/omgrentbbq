package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Key;
import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
                     @Key Long id;
    //for GWT this suppression and long classname are important.
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.PostalAddress.class)
    String address;
    public String nickname;
 

    public Group() {
    }

    public Group(String address, String nickname) {
        this.address = address;
        this.nickname = nickname;
    }
}

