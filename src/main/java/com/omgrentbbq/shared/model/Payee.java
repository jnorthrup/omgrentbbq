package com.omgrentbbq.shared.model;

import java.io.Serializable;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:20:08 PM
 */
public class Payee extends Memento{

    Serializable getGroup(){

        return $("group");
    }
    Contact getContact(){
        final Contact contact = new Contact();
        contact.$$(new Pair<String, Memento>("contact",this  ));
        return contact;
    }

    public String getName() {
            return getContact().getName();
    }

    public String getNickname() {
        return $("nickname");
    }
}
