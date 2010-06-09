package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.omgrentbbq.client.App;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Payee;

/**
* Copyright 2010 Glamdring Incorporated Enterprises.
* User: jim
* Date: Jun 9, 2010
* Time: 12:36:09 AM
*/
public   class addPayeeClickHandler implements ClickHandler {
    private final App app;

    public addPayeeClickHandler(final App app) {
        this.app = app;
    }
    /*   private final App app;
    private final Group[] groups;

    public addPayeeClickHandler(App app, Group[] groups) {
        this.app = app;
        this.groups = groups;
    }*/

    @Override
    public void onClick(ClickEvent clickEvent) {
        final int index = app.groupList.getSelectedIndex();
        if (index < 0) return;
        final Group group = app.groups[index];
        final PopupPanel box = new DecoratedPopupPanel();
        box.setAnimationEnabled(true);

        new PayeePanel(box,
                new AsyncCallback<Payee>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Payee payee) {
                        Window.setStatus("payee " + payee.getNickname() + " was successfully added to " + group.getName());
                        box.hide(true);
                    }
                },
                new AsyncCallback<Payee>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        report(throwable);
                    }

                    @Override
                    public void onSuccess(final Payee payee) {
                        new Timer() {
                            @Override
                            public void run() {
                                app.lm.addPayeeForGroup(payee, group, new AsyncCallback<Payee>() {
                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        report(throwable);
                                    }

                                    @Override
                                    public void onSuccess(final Payee payee) {
                                        app.populatePayeeList(group);
                                    }
                                });
                            }
                        }.schedule(1000);
                    }
                });
    }

    void report(Throwable t) {
        Window.setStatus("payee was not added: " + t.getMessage());
    }
}
