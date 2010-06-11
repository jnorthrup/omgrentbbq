package com.omgrentbbq.server;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.HybridServiceServlet;
import com.omgrentbbq.client.Login;
import com.omgrentbbq.server.spi.LoginSpi;
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
import java.util.List;
import java.util.Properties;

import static com.omgrentbbq.server.spi.LoginSpi.DS;
import static com.omgrentbbq.server.MementoFactory.*;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 11, 2010
 * Time: 12:07:41 AM
 */
public class LoginImpl extends HybridServiceServlet implements Login {

  static LoginSpi spi=new LoginSpi();

    @Override
    public Pair<UserSession, String> getUserSession(String browserUrl) {


        HttpSession session = getThreadLocalRequest().getSession(true);
        UserService service = UserServiceFactory.getUserService();

        com.google.appengine.api.users.User sysuser = service.getCurrentUser();
        String url = null;
        User user = null;
        if (null != sysuser) {
            try {

                Entity entity =
                        DS.get($k(User.class, sysuser.getUserId()));

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
        return spi.getGroups(user);
    }

    @Override
    public void createNewMember(User user, Contact profile) {
        spi.createNewMember(user, profile);
    }

    @Override
    public List<Payee> getPayeesForGroup(Serializable serializable) {
        return spi.getPayeesForGroup(serializable);
    }

    @Override
    public Payee addPayeeForGroup(Payee payee, Group group) {
        return spi.addPayeeForGroup(payee, group);
    }

    @Override
    public void addGroup(User user, Group group) {
        spi.addGroup(user, group);
    }

    @Override
    public void deleteGroup(User user, Group group) {
        spi.deleteGroup(user, group);
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
            p.load(new StringReader(servletRequest.toString().replace(": ", "=")));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        final String s = servletRequest.getRequestURI();

        String out = null;
        try {
            final URI uri = new URI((String) p.get("Origin"));

            char c;
            c = uri.getQuery() == null ? '?' : '&';
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
        spi.createShare(membership, shareType, amount);
    }

    @Override
    public Share[] getShares(Group group) {
        return spi.getShares(group);
    }

    @Override
    public Boolean createSharesFromInvite(User user, String invKey) {
        return spi.createSharesFromInvite(user, invKey);
    }

    @Override
    public Membership assignMembership(User to, User from, Group group) {
        return spi.assignMembership(to, from, group);
    }
}
