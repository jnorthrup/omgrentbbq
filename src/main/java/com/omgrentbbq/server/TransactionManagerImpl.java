package com.omgrentbbq.server;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.omgrentbbq.client.rpc.TransactionManager;
import com.omgrentbbq.shared.model.*;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 30, 2010
 * Time: 12:01:32 AM
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


    boolean sessionStinks(UserSession session) {
        final UserService service = UserServiceFactory.getUserService();
        final User user = service.getCurrentUser();
        final HttpSession session1 = getThreadLocalRequest().getSession(false);
        return user == null
                || session.user == null
                || !session1.getId().equals(session.id)
                || !session.loggedIn
                || !user.getUserId().equals(session.user.userId);
    }

/*    @Override
    public Payee addPayee(UserSession session, Payee payee) {
        if (sessionStinks(session)) return null;
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.store(payee);
        return payee;
    }*/

    @Override
    public ArrayList<Group> getGroups(UserSession session) {

        if (sessionStinks(session)) return null;
        ObjectDatastore ds = new AnnotationObjectDatastore();
        return innerGetGroups(session, ds);

    }

    @Override
    public ArrayList<Payee> getPayees(UserSession session, Group group) {

        if (sessionStinks(session)) return null;
        ObjectDatastore ds = new AnnotationObjectDatastore();

        ds.associate(group);
        final QueryResultIterator<Payee> payeeQueryResultIterator = ds.find()
                .type(Payee.class)
                .withAncestor(group)
                .returnResultsNow();
        final ArrayList<Payee> arrayList = new ArrayList<Payee>();
        while (payeeQueryResultIterator.hasNext()) {
            Payee payee = null;
            try {
                payee = payeeQueryResultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            arrayList.add(payee);

        }
        return arrayList;
    }

    private static ArrayList<Group> innerGetGroups(UserSession session, ObjectDatastore ds) {
        final com.omgrentbbq.shared.model.User user = session.user;
        ds.associate(user);
        ds.associate(session);
        final ArrayList<Group> arrayList = new ArrayList<Group>();
        final String s = user.userId;
        final Key key = ds.associatedKey(user);
        final QueryResultIterator<Membership> memberQueryResultIterator = ds.find().type(Membership.class)
                .addFilter("user", Query.FilterOperator.EQUAL, key)
                .returnResultsNow();

        while (memberQueryResultIterator.hasNext()) {
            Membership membership = memberQueryResultIterator.next();

            final Key key1 = ds.associatedKey(membership.group);
            final Group group = ds.load(key1);
            arrayList.add(group);
        }
        return arrayList;
    }

    @Override
    public void addPayee(UserSession session, Payee payee, Group group) {
        if (sessionStinks(session))
            throw new Error("bad session");
        ObjectDatastore ds =
                new AnnotationObjectDatastore();


        ds.associate(group);
        ds.store(payee, group);
    }

    @Override
    public void addGroup(UserSession session, Group group) {
        if (sessionStinks(session)) {
            throw new Error("Session Disconnected -- please reload");
        }
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.store(group);
    }

    @Override
    public void createMembership(UserSession session, com.omgrentbbq.shared.model.User user, Group group) {
        if (sessionStinks(session)) throw new Error("bad session");
        ObjectDatastore ds = new AnnotationObjectDatastore();
        ds.associate(group);
        ds.associate(user);
        ds.store(new Membership(user, group));

    }


}
 