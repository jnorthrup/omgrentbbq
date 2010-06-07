package com.omgrentbbq.shared.model;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 5:20:20 PM
 */
public class Contact  extends Memento{
 
    public String getAddress1() {
        return $("address1");
    }

    public void setAddress1(String address1) {
        $("address1",address1);
    }

    public String getAddress2() {
        return $("address2");
    }

    public void setAddress2(String address2) {
        $("address2",address2);
    }

    public String getName() {
        return $("name");
    }

    public void setName(String name) {
        $("name",name);
    }

    public String getCity() {
        return $("city");
    }

    public void setCity(String city) {
        $("city",city);
    }

    public String getState() {
        return $("state");
    }

    public void setState(String state) {
        $("state",state);
    }

    public String getZip() {
        return $("zip");
    }

    public void setZip(String zip) {
        $("zip",zip);
    }

    public String getPhone() {
        return $("phone");
    }

    public void setPhone(String phone) {
        $("phone",phone);
    }
}
