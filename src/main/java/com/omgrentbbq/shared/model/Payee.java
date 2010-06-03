package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ALL"})
public class Payee implements Serializable {

    public
    String account, nickname, fax;
    public Periodicity periodicity;

    /**
     * for most Periodicity
     */
    public int[] schedule = {};
            @Key
            Long id;
    @Embed
    public Contact contact;
    @Parent
    Group group;

    @Child
    public List<Bill> paid = new ArrayList<Bill>();
    @Child
    public List<Share> shares = new ArrayList<Share>();

    @Type(com.google.appengine.api.datastore.Text.class)
    public
    String notes;

    @Child
    public
    List<String> comments = new ArrayList<String>();

    public Payee() {
    }
}
