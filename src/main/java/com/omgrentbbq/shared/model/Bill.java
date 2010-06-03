package com.omgrentbbq.shared.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 26, 2010
 * Time: 1:19:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bill implements Serializable {
    Payee payee;
    Date recieved, due;
    float amount;

}
