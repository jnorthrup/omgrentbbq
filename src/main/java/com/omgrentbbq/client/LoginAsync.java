package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.*;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public interface LoginAsync {

    void addPayeeForGroup(Payee payee, Group group, AsyncCallback<Payee> async);

    void getPayeesForGroup(Serializable serializable, AsyncCallback<List<Payee>> async);


    void getGroups(User user, AsyncCallback<Group[]> async);

    void getUserSession(String browserUrl, AsyncCallback<Pair<UserSession, String>> async);

    void addGroup(User user, Group group, AsyncCallback<Void> async);

    void deleteGroup(User user, Group group, AsyncCallback<Void> async);

    void createNewMember(User user, Contact profile,/*, Group[] groups*/AsyncCallback<Void> async);
}
