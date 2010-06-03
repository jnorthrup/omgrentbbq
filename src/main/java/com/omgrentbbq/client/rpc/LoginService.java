package com.omgrentbbq.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.omgrentbbq.shared.model.*;

import java.io.Serializable;

@RemoteServiceRelativePath("u")
public interface LoginService extends RemoteService {

    UserSession login(String requestUri);
    void addUser(UserSession session,User user, Contact contact,Membership member, Group group);
    Membership getMember(User user);
}
