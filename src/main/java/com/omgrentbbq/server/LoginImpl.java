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
import java.util.List;

import static com.omgrentbbq.server.MementoFactory.*;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public class LoginImpl extends HybridServiceServlet implements Login {
    private static final DatastoreServiceConfig config = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(ReadPolicy.Consistency.STRONG));
    private static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService(config);


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

                user = $(entity, User.class);
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
        userSession.$("user", user);


        try {
            userSession.$("userLoggedIn", service.isUserLoggedIn());
            userSession.$("userAdmin", service.isUserAdmin());
        } catch (Exception e) {
        }
        update(userSession);


        return new Pair<UserSession, String>(userSession, url);
    }

    @Override
    public Group[] getGroups(User user) {
        final Key key = $k(user);
        final ArrayList<Group> a = new ArrayList<Group>();
        new Query(Membership.class.getName())
                .addFilter("user", Query.FilterOperator.EQUAL, key);
        final Iterator<Entity> entityIterator = DS.prepare(
                new Query(Membership.class.getName())
                        .addFilter("user", Query.FilterOperator.EQUAL, key)
        ).asIterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            final Membership membership = $(entity, Membership.class);
            final Serializable group = membership.getGroup();
            if (group instanceof Group) {
                a.add((Group) group);
            } else
                try {
                    if (group instanceof Key) {
                        Key key1 = (Key) group;
                        Group g = $(DS.get(key1), Group.class);
                        a.add(g);
                    }
                } catch (EntityNotFoundException ignored) {
                }
        }

        return a.toArray(new Group[a.size()]);

    }

    @Override
    public void createNewMember(final User user, final Contact profile, Group[] groups) {
        embed(new Pair<String, Memento>("profile", profile), user);
        update(user);
        if (groups.length == 0) {
            final Group group = new Group();
            group.$("name", profile.$("name") + "'s free private membership");
            group.$("immutable", true);

            groups = new Group[]{group};
        }

        MementoFactory.update(user);
        for (Group group : groups) {
            update(group);
            final Membership membership = new Membership(user, group);
            update(membership);
        }
    }

    @Override
    public List<Payee> getPayeesForGroup(Serializable serializable) {
        Key key;
        if (serializable instanceof Group) {
            Group group = (Group) serializable;
            key = $k(group);
        } else {
            key = $k(Group.class, serializable);

        }
        final Iterator<Entity> entityIterator = DS.prepare(
                new Query(Payee.class.getName()).addFilter("group", Query.FilterOperator.EQUAL, key)
        ).asIterator();
        final ArrayList<Payee> payeeArrayList = new ArrayList<Payee>();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            payeeArrayList.add($(entity, Payee.class));
        }
        return payeeArrayList;
    }

    @Override
    public Payee addPayeeForGroup(Payee payee, Group group) {

        payee.$("group", group);
        update(payee);
        return payee;
    }
}