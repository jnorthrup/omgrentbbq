package com.omgrentbbq.server;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.omgrentbbq.client.rpc.LoginService;
import com.omgrentbbq.shared.model.*;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

import javax.servlet.http.HttpSession;
import java.util.Date;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {


    public UserSession login(String requestUri) {
        UserService userService = UserServiceFactory.getUserService();

        HttpSession session = getThreadLocalRequest().getSession();
        ObjectDatastore datastore = new AnnotationObjectDatastore();

        com.google.appengine.api.users.User user = userService.getCurrentUser();
        if (user != null) {
            User theUser = null;
            try {
                theUser = datastore.load(User.class, user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            if (!(theUser != null)) {
                theUser = new User();
            }
            theUser.emailAddress = user.getEmail();
            theUser.nickname = user.getNickname();
            theUser.authdomain = user.getAuthDomain();
            theUser.userId = user.getUserId();

            Date date = new Date(session.getCreationTime());
            UserSession userSession = new UserSession(session.getId(), date, theUser);

            userSession.logoutUrl = userService.createLogoutURL(requestUri );
            userSession.admin = userService.isUserAdmin();
            userSession.loggedIn = userService.isUserLoggedIn();


            datastore.storeOrUpdate(theUser);
            datastore.storeOrUpdate(userSession, theUser);


            return userSession;
        } else {

            String s = "";
            UserSession userSession = new UserSession();
            userSession.loginUrl = userService.createLoginURL(requestUri + s);

            userSession.loggedIn = false;
            userSession.logoutUrl = userService.createLogoutURL(requestUri + s);
            datastore.storeOrUpdate(userSession);
            session.invalidate();
            return userSession;

        }
    }


    @Override
    public Membership getMember(User user) {
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.associate(user);

        return ds.find()
                .type(Membership.class)
                .addFilter("user", Query.FilterOperator.EQUAL, ds.associatedKey(user))
                .returnResultsNow()
                .next();

    }

    @Override
    public void addUser(UserSession session, User user, Contact contact, Membership member, Group group) {
        ObjectDatastore ds = new AnnotationObjectDatastore();
        user.profile = contact;
        ds.store(user);
        ds.store(group);
        member.user = user;
        member.group = group;

        ds.associate(session);
        session.user = user;
        ds.storeOrUpdate(session);
        ds.store(member);
    }


}
