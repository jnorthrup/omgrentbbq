package com.omgrentbbq.shared.model;

import java.io.Serializable;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:20:14 PM
 */
public class Membership extends Memento {
    public Serializable getGroup() {
        return $("group");
    }

    public Serializable getUser() {
        return $("user");
    }
}
