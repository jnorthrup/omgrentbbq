package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;
import java.util.List;

public class PayGroup implements Serializable {
    //for GWT this suppression and long classname are important.
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.PostalAddress.class)
    String address;
    List<Payee> payees;

    PayGroup() {
    }

    public PayGroup(String address) {
        this.address = address;
    }
}

