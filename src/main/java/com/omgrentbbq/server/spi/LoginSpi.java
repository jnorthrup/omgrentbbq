package com.omgrentbbq.server.spi;

import com.google.appengine.api.datastore.*;
import com.omgrentbbq.client.Login;
import com.omgrentbbq.server.MementoFactory;
import com.omgrentbbq.shared.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static com.omgrentbbq.server.MementoFactory.*;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 4, 2010
 * Time: 2:44:03 PM
 */
public class LoginSpi implements Login {
    private static final DatastoreServiceConfig config = DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(ReadPolicy.Consistency.STRONG));
    public static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService(config);


    @Override
    public Pair<UserSession, String> getUserSession(String browserUrl) {
        throw new RuntimeException("SPI cannot provide servlet methods directly");
    }

    @Override
    public Group[] getGroups(User user) {
        final Key key = $$(user);
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
    public void inviteUserToGroup(User user, Group group, String emailAddress) {
        throw new RuntimeException("SPI cannot provide servlet methods directly");
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
            key = $$(group);
        } else {
            key = $$(Group.class, serializable);

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

        final Query queryMembersForGroup = new Query(Membership.class.getName()).addFilter("group", Query.FilterOperator.EQUAL, $$(group));
        final int count = DS.prepare(queryMembersForGroup).countEntities();
        final Iterable<Entity> entityIterable = DS.prepare(queryMembersForGroup.addFilter("user", Query.FilterOperator.EQUAL, $$(user))).asIterable();
        for (Entity entity : entityIterable) {
            DS.delete(entity.getKey());
        }

        if (count < 2) {
            DS.delete($$(group));
        }

    }

    @Override
    public void createShare(Membership membership, Share.ShareType shareType, Float amount) {
        try {
            Entity entity = DS.get($$(membership));
            final Membership membership1 = $(entity, Membership.class);

            entity = DS.get($$((Group) membership.getGroup()));

            Group group = $(entity, Group.class);
            entity = DS.get($$((User) membership.getUser()));
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
        final Query query = new Query(Membership.class.getName()).addFilter("group", Query.FilterOperator.EQUAL, $$(group)).setKeysOnly();
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

    @Override
    public Boolean createSharesFromInvite(User user, String invKey) {
        final Key key = KeyFactory.stringToKey(invKey);
        try {
            Invitation inv = $(DS.get(key), Invitation.class);
            final User from = (User) inv.getFrom();
            final Group group = (Group) inv.getGroup();
            final Membership membership = ((Login) this).assignMembership(user, from, group);
            if (membership != null) {
                return true;
            }


        } catch (EntityNotFoundException e) {

            return true;
        }
        return false;
    }

    @Override
    public Membership assignMembership(User to, User from, Group group) {
        Membership membership = loadMembership(group, from);
        Share s = membership.getShare();

        if (s.getAmount() == null) {
            s.setAmount(1f);
            s.setShareType(Share.ShareType.pieShare);
        }
        update(membership);

        membership = loadMembership(group, to);
        if (membership == null) membership = new Membership(to, group);
        s = membership.getShare();

        if (s.getAmount() == null) {
            s.setAmount(1f);
            s.setShareType(Share.ShareType.pieShare);
        }
        update(membership);
        return membership;
    }

    private static Membership loadMembership(Group group, User from) {
        final Key key = $$(from);
        final Key key1 = $$(group);
        Query grpQuery = new Query(Membership.class.getName())
                .addFilter("group", Query.FilterOperator.EQUAL, key1).setKeysOnly();

        final Iterable<Entity> entityIterable = DS.prepare(grpQuery).asIterable();
        HashSet<Key> ar = new HashSet<Key>();
        for (Entity entity : entityIterable) {
            ar.add(entity.getKey());
        }
        Query userQuery = new Query(Membership.class.getName())
                .addFilter("user", Query.FilterOperator.EQUAL, key);


        final Iterable<Entity> iterable = DS.prepare(userQuery).asIterable();

        Entity correct = null;
        for (Entity entity : iterable) {
            if (ar.contains((entity).getKey())) {
                correct = entity;
                break;
            }
        }

        if (null == correct) {
            grpQuery = new Query(Membership.class.getName())
                    ;
            final List<Entity> entities = DS.prepare(grpQuery).asList(FetchOptions.Builder.withDefaults());
            return null;

        } else


            return $(correct, Membership.class);
    }
}