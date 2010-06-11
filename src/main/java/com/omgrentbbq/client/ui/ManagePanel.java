package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
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
    public ManagePanel(final App app) {
        FlexTable flexTable = new ManageGroupPanel(app);
        add(flexTable);
        add(new ManagePayeePanel(this, app));
        add(new VerticalPanel() {
            final FlexTable flexTable1 = new FlexTable();

            {
                setVisible(!getCurrentGroup(app).isPrivacy());

                add(flexTable1);
                add(new Button("Invite Other Users") {{
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {

                            new DialogBox(true, true) {{
                                setPopupPosition(20, 20);
                                setText("please enter the email address you would like to invite into this group");
                                setWidget(new HorizontalPanel() {{
                                    final TextBox box = new TextBox();
                                    add(box);
                                    add(new Button("Invite!") {
                                        {
                                            addClickHandler(new ClickHandler() {
                                                @Override
                                                public void onClick(ClickEvent clickEvent) {
                                                    final String emailAddress = box.getText();
                                                    app.lm.inviteUserToGroup(app.user, getCurrentGroup(app), emailAddress, new AsyncCallback<Void>() {
                                                        @Override
                                                        public void onFailure(Throwable throwable) {
                                                            Window.setStatus("invitation was cancelled ");
                                                            hide(true);
                                                        }

                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Window.setStatus("invitation sent to " + emailAddress);
                                                            hide(true);
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });
                                    add(new Button("cancel") {{
                                        addClickHandler(new ClickHandler() {
                                            @Override
                                            public void onClick(ClickEvent clickEvent) {
                                                Window.setStatus("no invitation was chosen");
                                                hide(true);
                                            }
                                        });
                                    }});
                                }});


                                show();

                            }};

                        }
                    });
                }});
                app.groupList.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent changeEvent) {
                        final Group group = getCurrentGroup(app);
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

    private Group getCurrentGroup(App app) {
        final int selectedIndex = app.groupList.getSelectedIndex();
        return app.groups[selectedIndex];
    }
}

