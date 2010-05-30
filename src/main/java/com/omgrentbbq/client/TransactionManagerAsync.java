package com.omgrentbbq.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.omgrentbbq.shared.model.Income;
import com.omgrentbbq.shared.model.Payee;
import com.omgrentbbq.shared.model.UserSession;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 30, 2010
 * Time: 12:01:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionManagerAsync {

    void addIncome(UserSession session, Income income, AsyncCallback<Long> async);

    void addPayee(UserSession session, Payee  entry, AsyncCallback<Payee> async);
}
