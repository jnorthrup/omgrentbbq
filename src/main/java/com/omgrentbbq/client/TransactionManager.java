package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.omgrentbbq.shared.model.Income;
import com.omgrentbbq.shared.model.Payee;
import com.omgrentbbq.shared.model.UserSession;
@RemoteServiceRelativePath("tm") 
public interface TransactionManager extends RemoteService {
    Long addIncome(UserSession session, Income income);

    Payee addPayee(UserSession session, Payee entry);
}
