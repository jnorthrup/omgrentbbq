package com.omgrentbbq.shared.model;
//
//import com.google.appengine.api.datastore.PostalAddress;

import com.vercer.engine.persist.annotation.Index;

import java.io.Serializable;

public class Membership implements Serializable {

    @Index
    public
    User user;
    @Index
    public Group group;


    Membership() {
    }

    public Membership(User user, Group group) {
        this.user = user;
        this.group = group;

    }

}
