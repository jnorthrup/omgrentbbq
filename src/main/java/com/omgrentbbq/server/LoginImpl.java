package com.omgrentbbq.server;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.HybridServiceServlet;
import com.omgrentbbq.client.Login;
import com.omgrentbbq.shared.model.*;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
    public void createNewMember(User user, Contact profile) {
        embed(new Pair<String, Memento>("profile", profile), user);
        update(user);
        final Group group = new Group();
        group.$("name", profile.$("name") + "'s free private membership");
        group.$("privacy", true);
        group.$("immutable", true);

        MementoFactory.update(user);
        update(group);
        final Membership membership = new Membership(user, group);
        update(membership);
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

    @Override
    public void addGroup(User user, Group group) {
        update(group);
        final Membership membership = new Membership(user, group);
        update(membership);
    }

    @Override
    public void deleteGroup(User user, Group group) {

        final Query queryMembersForGroup = new Query(Membership.class.getName()).addFilter("group", Query.FilterOperator.EQUAL, $k(group));
        final int count = DS.prepare(queryMembersForGroup).countEntities();
        final Iterable<Entity> entityIterable = DS.prepare(queryMembersForGroup.addFilter("user", Query.FilterOperator.EQUAL, $k(user))).asIterable();
        for (Entity entity : entityIterable) {
            DS.delete(entity.getKey());
        }

        if (count < 2) {
            DS.delete($k(group));
        }

    }


    @Override
    public void inviteUserToGroup(User user, Group group, String emailAddress) {
        if (emailAddress.isEmpty())
            emailAddress = "jimn235@hotmail.com";
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        final HttpServletRequest servletRequest = getThreadLocalRequest();
        final Properties p = new Properties();
        try {
            p.load(new StringReader( servletRequest.toString().replace(": ","=")));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        final String s = servletRequest.getRequestURI();

        String out = null;
        try {
            final URI uri = new URI((String) p.get("Origin")) ;

            char c;
            c = uri.getQuery()==null ? '?' : '&';
            final Invitation invitation = new Invitation();
            invitation.$("from", user);
            invitation.$("to", emailAddress);
            invitation.$("group", group);

            update(invitation);
            final Key key = $k(invitation);
            out = uri.toASCIIString() + c + "invitation=" + KeyFactory.keyToString(key);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String msgBody = "Hello!\nOmgRentBBq.com user " + user.getNickname() + " has invited you to participate in the group called " + group.getName() + "\n" +
                "you can create an Id in seconds and log in to accpet this invitation using the following URL (click or paste into browser):\n" +
                "\n\n" +
                out;
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user.<String>$("email"), user.getNickname()));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(emailAddress));
            msg.setSubject("OmgRentBBq invitation to join group " + group.getName());
            msg.setText(msgBody);
            Transport.send(msg); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createShare(Membership membership, Share.ShareType shareType, Float amount) {
        try {
            Entity entity = DS.get($k(membership));
            final Membership membership1 = $(entity, Membership.class);

            entity = DS.get($k((Group) membership.getGroup()));

            Group group = $(entity, Group.class);
            entity = DS.get($k((User) membership.getUser()));
            User user = $(entity, User.class);


            final Share share = new Share(shareType, membership, amount);
            embed(new Pair<String, Memento>("user", user), share);
            embed(new Pair<String, Memento>("group", group), share);

            update(
                    share
            );
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Share[] getShares(Group group) {
        final Query query = new Query(Membership.class.getName()).addFilter("group", Query.FilterOperator.EQUAL, $k(group)).setKeysOnly();
        final Iterable<Entity> entityIterable = DS.prepare(query).asIterable();
        final ArrayList<Key> arrayList1 = new ArrayList<Key>();
        for (Entity entity : entityIterable) {
            arrayList1.add(entity.getKey());
        }

        final Query query1 = new Query(Share.class.getName()).addFilter("member", Query.FilterOperator.IN, arrayList1);
        final Iterator<Entity> entityIterator = DS.prepare(query1).asIterator();
        final ArrayList<Share> arrayList = new ArrayList<Share>();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            final Share share = $(entity, Share.class);
            arrayList.add(share);
        }
        return arrayList.toArray(new Share[arrayList.size()]);


    }
}