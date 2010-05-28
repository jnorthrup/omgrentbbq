package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ALL"})
public class Payee implements Serializable {


    PayCycle payCycle;

    /**
     * for most PayCycle
     */
    int[] schedule = {};

    @Embed
    Contact contact;
    String account;

    @Child
    List<Bill> paid = new ArrayList<Bill>();
    @Child
    List<Share> shares = new ArrayList<Share>();

    @Type(com.google.appengine.api.datastore.Text.class)
    String notes;

    @Child
    List<String> comments = new ArrayList<String>();
}
