package com.omgrentbbq.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.HybridServiceServlet;
import com.omgrentbbq.client.Login;
import com.omgrentbbq.shared.model.Pair;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;

import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Map;

import static com.omgrentbbq.server.MementoFactory.*;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public class LoginImpl extends HybridServiceServlet implements Login {


    @Override
    public Pair<UserSession, String> getUserSession(String browserUrl) {


        HttpSession session = getThreadLocalRequest().getSession(true);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        UserService service = UserServiceFactory.getUserService();

        com.google.appengine.api.users.User sysuser = service.getCurrentUser();
        String url = null;
        User user = null;
        if (null != sysuser) {
            try {

                Entity entity = ds.get($k(User.class, sysuser.getUserId()));

                user = $(entity);
                update(user);
                if (!service.isUserLoggedIn()) {
                    url = service.createLoginURL(browserUrl);
                } else {
                    url = service.createLogoutURL(browserUrl);
                }


            } catch (EntityNotFoundException ignored) {

                user = MementoFactory.writeMemento(sysuser, User.class);
            }


        } else {
            url = service.createLoginURL(browserUrl);
        }
        final UserSession userSession = writeMemento(session, UserSession.class);
        userSession.properties.put("user", user);


        try {
            final boolean loggedIn = service.isUserLoggedIn();
           userSession.properties.put("userLoggedIn",loggedIn);
        } catch (Exception e) {

        }
//        userSession.properties.putAll(map);
        update(userSession);


        return new Pair<UserSession, String>(userSession, url);
    }
}