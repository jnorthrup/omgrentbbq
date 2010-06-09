package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.omgrentbbq.client.App;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Share;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 8, 2010
 * Time: 11:11:05 PM
 */
public class ManagePanel extends VerticalPanel {
    public ManagePanel(final App app/*, final Group[] groups*/) {
        FlexTable flexTable = new ManageGroupPanel(app);
        add(flexTable);
        add(new ManagePayeePanel(this, app));
        add(new VerticalPanel() {
            final FlexTable flexTable1 = new FlexTable();

            {
                add(flexTable1);
                add(new Button("Invite Other Users"));
                app.groupList.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent changeEvent) {
                        final int selectedIndex = app.groupList.getSelectedIndex();
                        final Group group = app.groups[selectedIndex];
                        final boolean privacy = group.isPrivacy();

                        setVisible(!privacy);
                        if (!privacy) {
                            app.lm.getShares(group, new AsyncCallback<Share[]>() {
                                @Override
                                public void onFailure(Throwable throwable) {


                                }

                                @Override
                                public void onSuccess(final Share[] shares1) {

                                    flexTable1.clear();

                                    flexTable1.setWidget(0, 0, new HTML("<h3>User"));
                                    flexTable1.setWidget(0, 1, new HTML("<h3>Shares"));
                                    flexTable1.setWidget(0, 2, new HTML("<h3>Type"));
                                    for (int i1 = 0; i1 < shares1.length; i1++) {
                                        Share share1 = shares1[i1];
                                        final boolean isDollars = share1.getShareType() == Share.ShareType.fixedAmount;
                                        flexTable1.setText(i1, 0, (String) share1.$("user.name"));
                                        flexTable1.setText(i1, 1, (isDollars ? "$" : "") + share1.getAmount().toString() + (isDollars ? "" : "/" + String.valueOf(shares1.length)));
                                        flexTable1.setText(i1, 2, isDollars ? "Fixed Amount" : "Shares of a Pie");
                                    }
                                }
                            });
                        }
                    }
                });
            }});
    }
}

