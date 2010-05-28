package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;


public class Contact implements Serializable {
    public String name;
    public String address1;
    public String address2;
    public String city;
    public String state;
    public String zip;
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.PhoneNumber.class)
    public String phone;
}

