package com.omgrentbbq.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.*;

import java.io.Serializable;

public interface LoginServiceAsync {
    public void login(String requestUri, AsyncCallback<UserSession> async);



    void getMember(User user, AsyncCallback<Membership> async);

    void addUser(UserSession session, User user, Contact contact, Membership member, Group group, AsyncCallback<Void> async);
}
