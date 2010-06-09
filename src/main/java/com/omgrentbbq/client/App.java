package com.omgrentbbq.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.client.ui.*;
import com.omgrentbbq.shared.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {
    /**
     * The message displayed to the session when the server cannot be reached or
     * returns an error.
     */
    @SuppressWarnings("unused")
    public static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    DockPanel panel = new DockPanel();


    public final ListBox groupList = new ListBox() {{
        addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                if (getSelectedIndex() >= 0) {
                    populatePayeeList(groups[getSelectedIndex()]);
                }
            }
        });
    }};
    static String GDATA_API_KEY;

    TabPanel tabPanel = new TabPanel() {{
        setAnimationEnabled(true);
    }};
    public Group[] groups;

    public LoginAsync lm = GWT.create(Login.class);
    public User user;
    private VerticalPanel managePanel;

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

                String url = userSessionURLPair.getSecond();
                user = (User) userSession.$("user");
                panel.add(tabPanel, DockPanel.CENTER);
                tabPanel.add(new WelcomeTab(), "Welcome!");
                tabPanel.selectTab(0);

                if (null != user && Boolean.valueOf(String.valueOf(userSession.$("userLoggedIn")))) {


                    doEntry(userSession);
                    if (userSession.isUserAdmin())
                        panel.add(new Label(userSession.toString()), DockPanel.SOUTH);

                }
            }
        });
    }

    private void doEntry(final UserSession userSession) {
        lm.getGroups(user, new AsyncCallback<Group[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(final Group[] groups) {
              App.this.groups=groups;
                if (userSession.isUserAdmin())
                    panel.add(new Label(Arrays.toString(groups)), DockPanel.SOUTH);


                if (groups.length == 0) {
                    tabPanel.add(new ContactCreationForm(new AsyncCallback<Contact>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                        }

                        @Override
                        public void onSuccess(Contact contact) {
                            lm.createNewMember(user, contact, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                }

                                @Override
                                public void onSuccess(Void aVoid) {
                                    tabPanel.remove(1);
                                    doEntry(userSession);
                                }
                            });

                        }
                    }), "Sign Up Free!");
                } else {


                    managePanel = new ManagePanel(App.this );
                    tabPanel.insert(managePanel, "Manage Groups", 1);

                }
            }
        });
    }

    public ListBox payeeBox = new ListBox() {{
        setVisibleItemCount(4);
    }};

    public void populatePayeeList(Group group) {
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

    public void groupReload(PopupPanel box) {
        new Timer() {
            @Override
            public void run() {
                lm.getGroups(user, new AsyncCallback<Group[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Group[] groups) {
                        App.this.groups = groups;
                        groupList.clear();
                        for (Group group1 : groups) {
                            groupList.addItem(group1.getName());

                        }
                        if (groupList.getItemCount() > 0) {
                            groupList.setSelectedIndex(0);
                            populatePayeeList(groups[0]);
                        }
                    }
                });
            }
        }.schedule(1000);
        box.hide(true);
    }

}

