package com.omgrentbbq.shared.model;
//
//import com.google.appengine.api.datastore.PostalAddress;

import java.io.Serializable;

public class Member implements Serializable {

    User user;

    PayGroup payGroup;

    Contact profile;

    Member() {
    }

    public Member(User user, PayGroup payGroup, Contact profile) {
        this.user = user;
        this.payGroup = payGroup;
        this.profile = profile;
    }

}
