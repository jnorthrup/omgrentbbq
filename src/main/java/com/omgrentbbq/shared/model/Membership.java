package com.omgrentbbq.shared.model;

import java.io.Serializable;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:20:14 PM
 */
public class Membership extends Memento {
    public Membership() {
    }

    public Membership(User user, Group group) {
        $("user", user);
        $("group", group);
    }

    public Serializable getGroup() {
        return $("group");
    }

    public Serializable getUser() {
        return $("user");
    }
    public Share getShare() {
        final Share share = new Share();
        embed("share", share);
        return share;
    }
}
