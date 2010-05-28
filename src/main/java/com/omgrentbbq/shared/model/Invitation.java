package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;

public class Invitation implements Serializable {


    Renter sentBy;
    //for GWT this suppression and long classname are important.
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.Email.class)
    public String sentTo;  
}
