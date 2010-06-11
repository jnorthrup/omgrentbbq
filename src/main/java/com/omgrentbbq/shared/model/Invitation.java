package com.omgrentbbq.shared.model;

import java.io.Serializable;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:20:17 PM
 */
public class Invitation extends Memento{

    public Serializable getFrom() {
        return $("from");
    }

    public Serializable getGroup() {
        return $("group");
    }

}
