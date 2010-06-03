package com.omgrentbbq.shared.model;

//for GWT commenting this line is important.
//import com.google.appengine.api.datastore.Email.class ;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;
import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;

public class User implements Serializable {
    @Key
    public String userId;

    //for GWT this suppression and long classname are important.
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.Email.class)
    public String emailAddress;
    public String nickname;
    public String authdomain;
    public String federatedIdentity;

    @Embed
    public
    Contact profile;

}


