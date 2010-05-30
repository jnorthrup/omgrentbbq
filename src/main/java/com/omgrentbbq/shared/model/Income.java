package com.omgrentbbq.shared.model;

// import com.google.appengine.api.datastore.PostalAddress;

import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 30, 2010
 * Time: 12:03:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Income implements Serializable {
    Date creation=new Date();
    float amount;
    @SuppressWarnings({"NonJREEmulationClassesInClientCode"})
    @Type(com.google.appengine.api.datastore.Text.class)
    String source;
    public UserSession session;

    public Income() {
    }

    public Income(float amount, String source) {
        this.amount = amount;
        this.source = source;
    }
}
