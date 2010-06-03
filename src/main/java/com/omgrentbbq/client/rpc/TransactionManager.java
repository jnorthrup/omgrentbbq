package com.omgrentbbq.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.omgrentbbq.shared.model.*;

import java.util.ArrayList;

@RemoteServiceRelativePath("tm")
public interface TransactionManager extends RemoteService {
    Long addIncome(UserSession session, Income income);

    //    Payee addPayee(UserSession session, Payee entry);
    ArrayList<Group> getGroups(UserSession session);

    void addPayee(UserSession session, Payee payee, Group parent);

    void addGroup(UserSession session, Group group);

    void createMembership(UserSession u, User user, Group group);


}
