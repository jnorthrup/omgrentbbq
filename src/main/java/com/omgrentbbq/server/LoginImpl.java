package com.omgrentbbq.server;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.HybridServiceServlet;
import com.omgrentbbq.client.Login;
import com.omgrentbbq.shared.model.*;

import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import static com.omgrentbbq.server.MementoFactory.*;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public class LoginImpl extends HybridServiceServlet implements Login {
    private static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService();


    @Override
    public Pair<UserSession, String> getUserSession(String browserUrl) {


        HttpSession session = getThreadLocalRequest().getSession(true);
        UserService service = UserServiceFactory.getUserService();

        com.google.appengine.api.users.User sysuser = service.getCurrentUser();
        String url = null;
        User user = null;
        if (null != sysuser) {
            try {

                Entity entity = DS.get($k(User.class, sysuser.getUserId()));

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
        userSession.$.put("user", user);


        try {
            userSession.$.put("userLoggedIn", service.isUserLoggedIn());
            userSession.$.put("userAdmin", service.isUserAdmin());
        } catch (Exception e) {

        }
//        userSession.$.putAll(map);
        update(userSession);


        return new Pair<UserSession, String>(userSession, url);
    }

    @Override
    public Group[] getGroups(User user) {
        final Key key = $k(user);
        new Query(Membership.class.getName())
                . addFilter("user", Query.FilterOperator.EQUAL, key);
        final Iterator<Entity> entityIterator = DS.prepare(
                new Query(Membership.class.getName())
                        .addFilter("user", Query.FilterOperator.EQUAL, key)
        ).asIterator();
        final ArrayList<Group> a = new ArrayList<Group>();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            final Membership membership = $(entity, Membership.class);
            final Serializable group = membership.getGroup();

            try {
                if (group instanceof Key) {
                    Key key1 = (Key) group;
                  Group g=$(DS.get( key1));
                    a.add (g)              ;
                }
            } catch (EntityNotFoundException ignored) {
            }

        }

        return new Group[0];

    }
}