package com.omgrentbbq.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.client.ui.ContactCreationForm;
import com.omgrentbbq.client.ui.PayeePanel;
import com.omgrentbbq.client.ui.WelcomeTab;
import com.omgrentbbq.shared.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OmgRentBbq implements EntryPoint {
    /**
     * The message displayed to the session when the server cannot be reached or
     * returns an error.
     */
    @SuppressWarnings("unused")
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    DockPanel panel = new DockPanel();
    private VerticalPanel authPanel;


    final ListBox groupList = new ListBox();
    static String GDATA_API_KEY;
    Anchor authAnchor;
    TabPanel tabPanel = new TabPanel() {{
        setAnimationEnabled(true);
    }};
    private Group[] groups;

    private LoginAsync lm = GWT.create(Login.class);

    public void onModuleLoad() {
        String host = Window.Location.getHost();
        String proto = Window.Location.getProtocol();
        boolean localHost = host.startsWith("127.0.0.1");
        boolean secure = proto.startsWith("https");
        GDATA_API_KEY = secure ?
                localHost ?
                        "ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA" :
                        "ABQIAAAAWpB08GH6KmKITXI7rtGRpBQP9W7Y7I5qr-k1KpACLx2-LL8VZRSAmDzEx8058dg-LbfPzLfgD1bPqQ" :
                "ABQIAAAAWpB08GH6KmKITXI7rtGRpBSZ0_RId71_G7aCA6qntwd15T_WaBRjfmPbE7W4RF2InR8N8OZxXPGNTQ";
        panel.add(new Image(MainBundle.INSTANCE.logo()), DockPanel.WEST);
        RootPanel rootPanel = RootPanel.get("main");
        rootPanel.add(panel);

        lm.getUserSession(Window.Location.getHref(), new AsyncCallback<Pair<UserSession, String>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Pair<UserSession, String> userSessionURLPair) {

                final UserSession userSession = userSessionURLPair.getFirst();
                authAnchor = new Anchor();
                panel.add(authAnchor, DockPanel.EAST);
                String url = userSessionURLPair.getSecond();
                authAnchor.setHref(url);
                final User user = (User) userSession.$("user");
                panel.add(tabPanel, DockPanel.CENTER);
                tabPanel.add(new WelcomeTab(), "Welcome!");
                tabPanel.selectTab(0);

                if (null == user || !Boolean.valueOf(String.valueOf(userSession.$("userLoggedIn")))) {
                    authAnchor.setText("Sign in using your google account now");
                } else {
                    authAnchor.setText("not " + user.$("nickname") + "? Sign Out");

                    GWT.runAsync(new RunAsyncCallback() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        @Override
                        public void onSuccess() {

                            lm.getGroups(user, new AsyncCallback<Group[]>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    //To change body of implemented methods use File | Settings | File Templates.
                                }

                                @Override
                                public void onSuccess(final Group[] groups) {
                                    OmgRentBbq.this.groups = groups;
                                    if (userSession.isUserAdmin())
                                        panel.add(new Label(Arrays.toString(groups)), DockPanel.SOUTH);
                                    if (groups.length == 0) {
                                        tabPanel.add(new ContactCreationForm(new AsyncCallback<Contact>() {
                                            @Override
                                            public void onFailure(Throwable throwable) {
                                            }

                                            @Override
                                            public void onSuccess(Contact contact) {
                                                lm.createNewMember(user, contact, new Group[0], new AsyncCallback<Void>() {
                                                    @Override
                                                    public void onFailure(Throwable throwable) {
                                                    }

                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        tabPanel.remove(1);
                                                        GWT.runAsync(new MyRunAsyncCallback());
                                                    }
                                                });

                                            }
                                        }), "Sign Up Free!");
                                    } else {
                                        GWT.runAsync(new MyRunAsyncCallback());

                                    }
                                }
                            });
                            if (userSession.isUserAdmin())
                                panel.add(new Label(userSession.toString()), DockPanel.SOUTH);
                        }
                    });
                }
            }
        });
    }

    private ListBox payeeBox = new ListBox() {{
        setVisibleItemCount(4);
    }};

    private class MyRunAsyncCallback implements RunAsyncCallback {

        private Timer timer;
        private VerticalPanel verticalPanel;

        @Override
        public void onFailure(Throwable throwable) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSuccess() {

            final FlexTable flexTable = new FlexTable();

            for (Group group : groups) {

                groupList.addItem(group.getName(), String.valueOf(group.$$()));
            }
            flexTable.setWidget(0, 0, new HTML("<h2>Manage your groups of payees and payments"));
            flexTable.setWidget(1, 0, new Label("Groups"));
            flexTable.setWidget(1, 1, new Anchor("(+)") {{
                setTitle("add a new Group");
            }});
            flexTable.setWidget(1, 2, new Anchor("(-)") {
                {
                    groupList.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent changeEvent) {
                            title();
                        }
                    });
                    title();
                }

                private void title() {
                    if (groups.length > 0 && !groups[groupList.getSelectedIndex()].isImmutable()) {
                        setVisible(false);
                    } else {
                        setVisible(true);
                        setTitle("remove " + groupList.getItemText(groupList.getSelectedIndex()));
                    }
                }
            });

            flexTable.setWidget(1, 4, groupList);
            flexTable.getFlexCellFormatter().setColSpan(0, 0, 5);
            flexTable.getFlexCellFormatter().setColSpan(1, 4, 2);
            populatePayeeList(groups[groupList.getSelectedIndex()]);
            verticalPanel = new VerticalPanel() {{
                add(flexTable);
                add(new HorizontalPanel() {{
                    add(new Label("Payees"));
                    add(new Anchor("(+)") {{
                        GWT.runAsync(new RunAsyncCallback() {
                            @Override
                            public void onFailure(Throwable throwable) {
                            }

                            @Override
                            public void onSuccess() {
                                final Group group = groups[groupList.getSelectedIndex()];
                                setTitle("add a new Payee to " + group.getName());
                                addClickHandler(new addPayeeClickHandler(group));
                            }
                        });
                    }});
                    add(payeeBox);
                }});

            }};
            tabPanel.insert(verticalPanel, "Manage Groups", 1);

        }

        private void mutate(final Label nickname, final ContactCreationForm contactForm, final TextBox account) {
            if (timer != null) {
                timer.cancel();
            } else {
                timer = new Timer() {
                    @Override
                    public void run() {
                        nickname.setText(contactForm.name.getText() + "/" + account.getText());
                    }
                };
            }

            timer.schedule(250);

        }

        private void populatePayeeList(Group group) {
            lm.getPayeesForGroup(group.$$(), new AsyncCallback<List<Payee>>() {
                @Override
                public void onFailure(Throwable throwable) {
                }

                @Override
                public void onSuccess(List<Payee> payees) {
                    payeeBox.clear();
                    for (Payee payee : payees) {
                        payeeBox.addItem(payee.getNickname());
                    }
                }
            });
        }

        private class addPayeeClickHandler implements ClickHandler {
            private final Group group;

            public addPayeeClickHandler(Group group) {


                this.group = group;
            }


            @Override
            public void onClick(ClickEvent clickEvent) {

                final DecoratedPopupPanel decoratedPopupPanel = new DecoratedPopupPanel();
                final PayeePanel payeePanel = new PayeePanel(decoratedPopupPanel,
                        new AsyncCallback<Payee>() {
                            @Override
                            public void onFailure(Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(Payee payee) {
                                Window.setStatus("payee " + payee.getNickname() + " was successfully added to " + group.getName());
                                decoratedPopupPanel.hide(true);
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
                                        lm.addPayeeForGroup(payee, group, new AsyncCallback<Payee>() {
                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                report(throwable);
                                            }

                                            @Override
                                            public void onSuccess(final Payee payee) {
                                                populatePayeeList(group);
                                            }
                                        });
                                    }
                                }.schedule(1000);


                            }
                        });

            }

            void report(Throwable t) {
                Window.setStatus("payee was not added to " + group.getName() + ": " + t.getMessage());
            }
        }


    }
}

