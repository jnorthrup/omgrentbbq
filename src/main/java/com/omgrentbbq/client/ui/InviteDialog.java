package com.omgrentbbq.client.ui;

import com.google.gwt.accounts.client.AuthSubStatus;
import com.google.gwt.accounts.client.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.gdata.client.Email;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.contacts.*;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 16, 2010
 * Time: 10:36:35 PM
 */
class InviteDialog extends DialogBox {
    public TextBox emailAddressTextBox;
    public Button inviteButton;
    public Button cancelButton;

    public ContactsService service;
    public List<ContactEntry> entries = new ArrayList<ContactEntry>();
    ListBox hintListBox;
    private ManagePanel managePanel;

    /**
     * temporary list for email copmletions
     */
    public ArrayList<ContactEntry> ems = new ArrayList<ContactEntry>();
    public Timer hintTimer;

    public InviteDialog(final ManagePanel managePanel, final App app) {
        super(true, true);

        this.managePanel = managePanel;

        ems.clear();
        setPopupPosition(20, 20);
        setText("please enter the email address you would like to invite into this group");
        setWidget(new HorizontalPanel() {{

            inviteButton = new Button("Invite!") {
                {
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            final String emailAddress = emailAddressTextBox.getText();
                            app.lm.inviteUserToGroup(app.user, managePanel.getCurrentGroup(app), emailAddress, new AsyncCallback<Void>() {
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
            };
            emailAddressTextBox = new TextBox() {
                {


                    addKeyPressHandler(new KeyPressHandler() {
                        @Override
                        public void onKeyPress(KeyPressEvent keyPressEvent) {
                            if (keyPressEvent.getCharCode() == KeyCodes.KEY_ENTER) {
                                inviteButton.click();
                            }
                            if (keyPressEvent.getCharCode() == KeyCodes.KEY_ENTER) {

                                cancelButton.click();
                            }

                        }
                    });


                    hintListBox = new ListBox() {{
                        setVisibleItemCount(6);
                        addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                emailAddressTextBox.setText(getValue(getSelectedIndex()));
                            }
                        });
                        addKeyPressHandler(new KeyPressHandler() {

                            @Override
                            public void onKeyPress(KeyPressEvent keyPressEvent) {

                                final char c = keyPressEvent.getCharCode();
                                final int keyEnter = KeyCodes.KEY_ENTER;
                                if (c == keyEnter) {
                                    emailAddressTextBox.setText(getValue(getSelectedIndex()));
                                    inviteButton.click();
                                }
                                if (c == KeyCodes.KEY_ESCAPE) {
                                    emailAddressTextBox.setFocus(true);
                                    hintListBox.clear();
                                    hintListBox.setVisible(false);
                                }
                            }
                        });

                    }};

                    ;
                    final PopupPanel hintPopup = new PopupPanel(true) {{
                        setWidget(new HorizontalPanel() {{
                            add(hintListBox);

                        }});
                    }};
                    addCloseHandler(new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                            hintPopup.hide(true);

                        }
                    });

                    addKeyDownHandler(new KeyDownHandler() {


                        @Override
                        public void onKeyDown(final KeyDownEvent keyDownEvent) {
                            if (hintListBox.isVisible() && !ems.isEmpty()) {
                                if (keyDownEvent.isDownArrow()) {
                                    final int index = hintListBox.getSelectedIndex();
                                    if (index < hintListBox.getItemCount() && hintListBox.getItemCount() > 1) {
                                        hintListBox.setSelectedIndex(index + 1);
                                    }
                                    pass = true;
                                    hintListBox.setFocus(true);
                                } else if (keyDownEvent.isUpArrow()) {
                                    final int index = hintListBox.getSelectedIndex();
                                    if (index > 0) {
                                        hintListBox.setSelectedIndex(index - 1);
                                    }
                                    pass = true;
                                    hintListBox.setFocus(true);
                                }

                            }


                        }
                    });
                    addKeyUpHandler(new KeyUpHandler() {
                        @Override
                        public void onKeyUp(KeyUpEvent keyUpEvent) {
                            if (pass) {
                                pass = false;
                                return;
                            }
                            final String s = getText().trim();
                            if (s.isEmpty()) {
                                hintPopup.hide(true);
                                return;
                            } else {
                                hintPopup.showRelativeTo(emailAddressTextBox);
                                hintPopup.show();
                            }

                            if (null != hintTimer)
                                hintTimer.cancel();
                            else
                                hintTimer = new Timer() {
                                    @Override
                                    public void run() {
                                        final String s = emailAddressTextBox.getText().trim();
                                        if (s.isEmpty()) {
                                            return;
                                        }
                                        hintListBox.clear();
                                        ems.clear();
                                        for (ContactEntry entry : entries) {
                                            final Email[] addresses = entry.getEmailAddresses();
                                            for (Email address : addresses) {
                                                if (address.getAddress().contains(s)/* || address.getDisplayName().contains(s)*/) {
                                                    String displayName = address.getDisplayName();
                                                    final String address1 = address.getAddress();
                                                    final String label = address.getLabel();
                                                    final boolean b = address.getPrimary();
                                                    final String rel = address.getRel();
                                                    if (null == displayName || displayName.isEmpty())
                                                        displayName = address1;
                                                    ems.add(entry);
                                                    hintListBox.addItem(displayName, address1);
                                                    break;
                                                }
                                            }
                                        }
                                        if (!ems.isEmpty())
                                            hintListBox.setSelectedIndex(0);
                                    }
                                };
                            hintTimer.schedule(250);

                        }
                    });
                }

                public boolean pass = false;
            };
            final TextBox emailInput = emailAddressTextBox;
            add(emailInput);
            add(inviteButton);
            cancelButton = new Button("cancel") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        Window.setStatus("no invitation was chosen");
                        hintTimer = null;
                        hide(true);
                    }
                });
            }};
            add(cancelButton);
        }});
        show();
        emailAddressTextBox.setFocus(true);

        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess() {
                if (!GData.isLoaded(GDataSystemPackage.CONTACTS)) {
                    GData.loadGDataApi(App.GDATA_API_KEY, new Runnable() {
                        public void run() {
                            startDemo();
                        }
                    }, GDataSystemPackage.CONTACTS);
                } else {
                    startDemo();
                }
            }

            /**
             * Retrieves a contacts feed using a Query object.
             * In GData, feed URIs can contain query string parameters. The
             * GData query objects aid in building parameterized feed URIs.
             * Upon successfully receiving the contacts feed, the contact entries
             * are displayed to the user via the showData method.
             * The MaxResults parameter is used to limit the number of entries
             * returned.
             *
             * @param contactsFeedUri The contacts feed uri.
             */
            private void queryContacts(String contactsFeedUri) {
                final ContactQuery query = ContactQuery.newInstance(contactsFeedUri);
                final int[] si = {1};
                query.setStartIndex(si[0]);
                query.setMaxResults(222);

                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess() {


                        service.getContactFeed(query, new ContactFeedCallback() {
                            public void onFailure(CallErrorException caught) {

                            }

                            public void onSuccess(ContactFeed result) {
                                final ContactEntry[] e = result.getEntries();

                                final int length = e.length;
                                if (length != 0) {

                                    entries.addAll(Arrays.asList(e));
//                                    final double v = query.getStartIndex();
                                    si[0] += length;
                                    query.setStartIndex(si[0]);
                                    service.getContactFeed(query, this);
                                }
                            }
                        });
                    }
                });
            }

            /**
             * Starts this demo.
             */
            private void startDemo() {
                service = ContactsService.newInstance("" + System.currentTimeMillis());
                if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
                    queryContacts("http://www.google.com/m8/feeds/contacts/default/full");
                }
            }
        });
    }

    private final String scope = "http://www.google.com/m8/feeds/";
}
