package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.omgrentbbq.shared.model.Renter;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;

import java.io.Serializable;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

    UserSession login(String requestUri);

    void commitParentEntity(Serializable o);
        Renter getRenter(User user);
}
