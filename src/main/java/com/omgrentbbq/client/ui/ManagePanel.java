package com.omgrentbbq.client.ui;

import com.google.gwt.accounts.client.AuthSubStatus;
import com.google.gwt.accounts.client.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.gdata.client.Email;
import com.google.gwt.gdata.client.contacts.*;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.App;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Share;

import java.util.ArrayList;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 8, 2010
 * Time: 11:11:05 PM
 */
public class ManagePanel extends VerticalPanel {
    public ContactEntry[] entries;

    /**
     * temporary list for email copmletions
     */
    public ArrayList<String> ems = new ArrayList<String>();

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
                                    final TextBox emailInput = new TextBox() {{
                                        addKeyDownHandler(new KeyDownHandler() {
                                            ListBox lb = new ListBox() {{
                                                setVisibleItemCount(6);
                                            }};
                                            PopupPanel hintPopup = new PopupPanel(true) {{
                                                setWidget(lb);
                                            }};

                                            @Override
                                            public void onKeyDown(KeyDownEvent keyDownEvent) {
                                                if (lb.isVisible()&&ems != null) {
                                                    if (keyDownEvent.isDownArrow()) {
                                                        final int index = lb.getSelectedIndex();
                                                        if (index < lb.getItemCount() && lb.getItemCount() > 1) {
                                                            lb.setSelectedIndex(index + 1);
                                                        }
                                                        return;
                                                    } else
                                                    if (keyDownEvent.isDownArrow()) {
                                                        final int index = lb.getSelectedIndex();
                                                        if (index < lb.getItemCount() && lb.getItemCount() > 1) {
                                                            lb.setSelectedIndex(index + 1);
                                                        }
                                                        return;
                                                    }
                                                }
                                                final String s = getText();
                                                hintPopup.setVisible(s.length() > 2);
                                                listEntry:
                                                for (int i = 0; i < entries.length; i++) {
                                                    ContactEntry entry = entries[i];
                                                    final Email[] addresses = entry.getEmailAddresses();
                                                    for (Email address : addresses) {
                                                        if (address.getAddress().contains(s) || address.getDisplayName().contains(s)) {
                                                            final String displayName = address.getDisplayName();
                                                            ems.add(displayName);
                                                            lb.addItem(displayName, address.getAddress());
                                                            continue listEntry;
                                                        }
                                                    }
                                                }
                                                if (!ems.isEmpty())
                                                    lb.setSelectedIndex(0);

                                            }
                                        });

                                    }};
                                    add(emailInput);
                                    add(new Button("Invite!") {
                                        {
                                            addClickHandler(new ClickHandler() {
                                                @Override
                                                public void onClick(ClickEvent clickEvent) {
                                                    final String emailAddress = emailInput.getText();
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

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess() {
                final String scope = "http://www.google.com/m8/feeds/";

                final ContactsService service = ContactsService.newInstance("" + System.currentTimeMillis());

                if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
                    Window.setStatus("Querying contacts...");
                    ContactQuery query = ContactQuery.newInstance("http://www.google.com/m8/feeds/contacts/default/full");
//                    Date today = new Date();
//                    DateTime updatedMin = DateTime.newInstance(today, true);
//                    query.setUpdatedMin(updatedMin);
                    query.setSortOrder(ContactQuery.SORTORDER_DESCENDING);
                    query.setShowDeleted(false);
                    service.getContactFeed(query, new ContactFeedCallback() {
                        public void onFailure(CallErrorException caught) {
                            Window.setStatus("An error occurred while retrieving the Contacts feed: " +
                                    caught.getMessage());
                        }

                        public void onSuccess(ContactFeed result) {
                            entries = result.getEntries();
                            if (entries.length == 0) {
                                Window.setStatus("No contacts were found.");
                            }

                        }
                    });

                }
            }
        });
        {
        }
    }

    private Group getCurrentGroup(App app) {
        final int selectedIndex = app.groupList.getSelectedIndex();
        return app.groups[selectedIndex];
    }
}

