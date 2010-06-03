package com.omgrentbbq.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.client.rpc.LoginService;
import com.omgrentbbq.client.rpc.LoginServiceAsync;
import com.omgrentbbq.client.ui.ContactCreationForm;
import com.omgrentbbq.client.ui.ManageTab;
import com.omgrentbbq.client.ui.WelcomeTab;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Membership;
import com.omgrentbbq.shared.model.UserSession;

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

    private DockPanel mainPanel = new DockPanel();

    UserSession session = null;
    private Panel authPanel = new VerticalPanel();
    private Label authLabel = new Label("Please sign in to your Google Account to access the application.");

    {
        authLabel.setSize("20EM", "");
    }

    private Anchor authLink = new Anchor("Sign In");
    private static final LoginServiceAsync LOGIN_SERVICE = GWT.create(LoginService.class);
    private WelcomeTab welcomeTab = new WelcomeTab();
    static String GDATA_API_KEY;

    public void onModuleLoad() {
        final String host = Window.Location.getHost();
        final String proto = Window.Location.getProtocol();
        GDATA_API_KEY = host.startsWith("127.0.0.1") ? "ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA" :
                proto.startsWith("https") ?
                        /*https://omgrentbbq.com*/"ABQIAAAAWpB08GH6KmKITXI7rtGRpBQP9W7Y7I5qr-k1KpACLx2-LL8VZRSAmDzEx8058dg-LbfPzLfgD1bPqQ" :
                        "ABQIAAAAWpB08GH6KmKITXI7rtGRpBSZ0_RId71_G7aCA6qntwd15T_WaBRjfmPbE7W4RF2InR8N8OZxXPGNTQ";
        mainPanel.add(new Image(MainBundle.INSTANCE.logo()), DockPanel.WEST);
        mainPanel.add(authPanel, DockPanel.EAST);
        RootPanel.get().clear();
        RootPanel.get().add(mainPanel);
        // Check login status using login service.
        LOGIN_SERVICE.login(Window.Location.getHref(), new AsyncCallback<UserSession>() {
            public void onFailure(Throwable error) {
            }

            public void onSuccess(UserSession result) {
                session = result;
                if (session.user == null) {
                    doWelcome();
                } else {
                    doMain();
                }
            }
        });
    }

    private void doWelcome() {
        authLink.setHref(session.loginUrl);
        authPanel.add(authLabel);
        authPanel.add(authLink);
        mainPanel.add(welcomeTab, DockPanel.CENTER);

    }

    void doMain() {

        initSession();


    }

    private void initSession() {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert("we're sorry our code failed to load, please check your network connectivity and hit refresh");
            }

            @Override
            public void onSuccess() {
                String logoutUrl = session.logoutUrl;
                authLink.setText("Sign out");
                authLink.setHref(logoutUrl);


                Label label = new Label("not " + session.user.nickname + " ?");
                if (session.admin) {
                    label.setTitle(session.toString());
                }
                authPanel.add(label);
                authPanel.add(authLink);
                TabPanel tabPanel = new TabPanel();

                mainPanel.add(tabPanel, DockPanel.CENTER);
                mainPanel.remove(welcomeTab);

                tabPanel.add(welcomeTab, "Welcome!");
                tabPanel.setAnimationEnabled(true);
                tabPanel.selectTab(0);
                checkUserInfoCompletion(tabPanel);    //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }

    void checkUserInfoCompletion(final TabPanel tabPanel) {


        LOGIN_SERVICE.getMember(session.user, new AsyncCallback<Membership>() {
            @Override
            public void onFailure(Throwable throwable) {
                final ContactCreationForm contactCreationForm = new ContactCreationForm(new AsyncCallback<Contact>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("Contact Creation Failure");
                    }

                    @Override
                    public void onSuccess(final Contact contact) {

                        final Group group = new Group(contact.address1 + "|" +
                                contact.address2 + "|" +
                                contact.city + "|" +
                                contact.state + "|" +
                                contact.zip, contact.name + " Private Stuff");

                        session.user.profile = contact;
                        final Membership membership = new Membership(session.user, group);

                        LOGIN_SERVICE.addUser(session, session.user, contact, membership, group, new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Window.alert("addUser failure for Membership");


                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                                tabPanel.remove(1);
                                checkUserInfoCompletion(tabPanel);
                            }
                        });
                    }
                });
                tabPanel.add(contactCreationForm
                        , "Start now!");
                tabPanel.selectTab(0);
            }

            @Override
            public void onSuccess(final Membership membership) {


                final AgendaHelper helper = new AgendaHelper(tabPanel, session);

                tabPanel.add(new ManageTab(helper, session), "Manage");
                tabPanel.add(new CreateEventUi(helper), "(d)");
                tabPanel.add(new Label("PlaceHolder"), "Groups");
                tabPanel.add(new Label("PlaceHolder"), "Support");
                tabPanel.selectTab(0);
            }
        });


    }


}

