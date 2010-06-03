package com.omgrentbbq.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.*;

import java.util.ArrayList;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 30, 2010
 * Time: 12:01:32 AM
 */
public interface TransactionManagerAsync {

    void addIncome(UserSession session, Income income, AsyncCallback<Long> async);

 
 
    void getGroups(UserSession session, AsyncCallback<ArrayList<Group>> async);

 

    void addPayee(UserSession u, Payee payee, Group parent, AsyncCallback<Void> async);


    void createMembership(UserSession u, User user, Group group, AsyncCallback<Void> async);

    void addGroup(UserSession session, Group group, AsyncCallback<Void> async);
}
