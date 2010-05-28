package com.omgrentbbq.shared.model;

import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

import java.io.Serializable;
import java.util.Date;

public class UserSession implements Serializable {

    @Key
    public String id;
    public Date created;
    public String loginUrl;
    public String logoutUrl;
    public boolean admin;
    public boolean loggedIn;
    @Embed(true)
    public User user;

    public UserSession() {
    }
    public UserSession(String id, Date created, User user) {
        this.id = id;
        this.created = created;
        this.user = user;
    }

}