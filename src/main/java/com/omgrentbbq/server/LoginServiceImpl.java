package com.omgrentbbq.server;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.omgrentbbq.client.LoginService;
import com.omgrentbbq.shared.model.Member;
import com.omgrentbbq.shared.model.User;
import com.omgrentbbq.shared.model.UserSession;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {


    public UserSession login(String requestUri) {
        UserService userService = UserServiceFactory.getUserService();

        HttpSession session = getThreadLocalRequest().getSession();
        ObjectDatastore datastore = new AnnotationObjectDatastore(  );

        com.google.appengine.api.users.User user = userService.getCurrentUser();
        if (user != null) {
            User theUser = null;
            try {
                theUser = datastore.load(User.class, user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

            String s="";
            if(user.getEmail().endsWith("example.com"))s="?gwt.codesvr=127.0.0.1:9997";
            userSession.logoutUrl = userService.createLogoutURL(requestUri+s);
            userSession.admin = userService.isUserAdmin();
            userSession.loggedIn = userService.isUserLoggedIn();


            datastore.storeOrUpdate(theUser);
            datastore.storeOrUpdate(userSession, theUser);


            return userSession;
        } else {

            String s="";
//            if(user.getEmail().endsWith("example.com"))s="?gwt.codesvr=127.0.0.1:9997";
            UserSession userSession = new UserSession();
            userSession.loginUrl = userService.createLoginURL(requestUri+s);
            userSession.loggedIn = false;
            userSession.logoutUrl = userService.createLogoutURL(requestUri+s);

            datastore.storeOrUpdate(userSession);
            session.invalidate();


            return userSession;


        }
    }

    @Override
    public void commitParentEntity(Serializable o) {
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.storeOrUpdate(o);
    }

    @Override
    public Member getRenter(User user) {
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.associate(user);

        return ds.find()
                .type(Member.class)
                .addFilter("user", Query.FilterOperator.EQUAL, ds.associatedKey(user))
                .returnResultsNow()
                .next();

    }


}
