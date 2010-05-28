package com.omgrentbbq.shared.model;
//
//import com.google.appengine.api.datastore.PostalAddress;

import java.io.Serializable;

public class Renter implements Serializable {

    User user;

    PayGroup payGroup;

    Contact profile;

    Renter() {
    }

    public Renter(User user, PayGroup payGroup, Contact profile) {
        this.user = user;
        this.payGroup = payGroup;
        this.profile = profile;
    }

}
