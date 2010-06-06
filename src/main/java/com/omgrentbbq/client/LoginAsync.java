package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.Pair;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;

import com.google.gwt.http.client.URL;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public interface LoginAsync {

 
    void getUserSession(String browserUrl, AsyncCallback<Pair<UserSession, String>> async);
}
