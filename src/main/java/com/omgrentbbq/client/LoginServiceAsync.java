package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.Renter;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;

import java.io.Serializable;

public interface LoginServiceAsync {
    public void login(String requestUri, AsyncCallback<UserSession> async);


    void commitParentEntity(Serializable o, AsyncCallback<Void> async);

    void getRenter(User user, AsyncCallback<Renter> async);
}
