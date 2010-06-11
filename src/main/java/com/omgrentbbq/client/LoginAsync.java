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

    void getUserSession(String browserUrl, AsyncCallback<Pair<UserSession, String>> async);

    void getGroups(User user, AsyncCallback<Group[]> async);

    void createNewMember(User user, Contact profile,/*, Group[] groups*/AsyncCallback<Void> async);

    void getPayeesForGroup(Serializable serializable, AsyncCallback<List<Payee>> async);

    void addPayeeForGroup(Payee payee, Group group, AsyncCallback<Payee> async);

    void addGroup(User user, Group group, AsyncCallback<Void> async);

    void deleteGroup(User user, Group group, AsyncCallback<Void> async);

    void inviteUserToGroup(User user, Group group, String emailAddress, AsyncCallback<Void> async);

    void createShare(Membership membership, Share.ShareType shareType, Float amount, AsyncCallback<Void> async);

    void getShares(Group group, AsyncCallback<Share[]> async);

    void createSharesFromInvite(User user, String invKey, AsyncCallback<Boolean> async);

    void assignMembership(User to, User from, Group group, AsyncCallback<Membership> async);
}