package com.omgrentbbq.server;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.omgrentbbq.client.TransactionManager;
import com.omgrentbbq.shared.model.Income;
import com.omgrentbbq.shared.model.Payee;
import com.omgrentbbq.shared.model.UserSession;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 30, 2010
 * Time: 12:01:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionManagerImpl extends RemoteServiceServlet implements TransactionManager {

    @Override
    public Long addIncome(UserSession session, Income income) {
        if (sessionStinks(session)) return null;
        ObjectDatastore ds = new AnnotationObjectDatastore();
        income.session = session;
        ds.associate(session);
        com.google.appengine.api.datastore.Key k = ds.store(income, session);
        return k.getId();
    }



    private boolean sessionStinks(UserSession session) {
        final UserService service = UserServiceFactory.getUserService();
        final User user = service.getCurrentUser();
        final HttpSession session1 = getThreadLocalRequest().getSession(false);
        if (user == null
                || session.user == null
                || !session1.getId().equals(session.id)
                || !session.loggedIn
                || !user.getUserId().equals(session.user.userId)) {
            return true;
        }
        return false;
    }
@Override
    public Payee addPayee(UserSession session, Payee payee) { 
        if (sessionStinks(session)) return null;
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.store(payee);
        return payee;
    }
}