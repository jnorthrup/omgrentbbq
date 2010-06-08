package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.omgrentbbq.shared.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
@RemoteServiceRelativePath("user")
public interface Login extends RemoteService {
    public Pair<UserSession, String> getUserSession(String browserUrl);

    public Group[] getGroups(User user);


    void createNewMember(User user,Contact profile, Group[] groups);

    List<Payee> getPayeesForGroup(Serializable serializable);

    Payee addPayeeForGroup(Payee payee, Group group);

}
