package com.omgrentbbq.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.PayGroup;
import com.omgrentbbq.shared.model.Renter;
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

    private VerticalPanel mainPanel = new VerticalPanel();

    UserSession session = null;
    private HorizontalPanel authPanel = new HorizontalPanel();
    private Label authLabel = new Label("Please sign in to your Google Account to access the application.");
    private Anchor authLink = new Anchor("Sign In");
    private static final LoginServiceAsync LOGIN_SERVICE = GWT.create(LoginService.class);

    public void onModuleLoad() {
        // Check login status using login service.
        LOGIN_SERVICE.login(GWT.getHostPageBaseURL(), new AsyncCallback<UserSession>() {
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
        authPanel.add(new Image( MainBundle.INSTANCE.logo()));
        // Assemble login imageStoryRow.
        authLink.setHref(session.loginUrl);
        authPanel.add(authLabel);
        authPanel.add(authLink);
        mainPanel.add(authPanel);
        RootPanel.get("main").add(mainPanel);
        WelcomeTab welcomeTabPanel = new WelcomeTab();
        mainPanel.add(welcomeTabPanel);
    }

    void doMain() {
        initSession();

        // Associate the Main panel with the HTML host page.
        RootPanel.get("main").add(mainPanel);

    }

    private void initSession() {
        String logoutUrl = session.logoutUrl;
        authLink.setText("Sign out");
        authLink.setHref(logoutUrl);

 

        Label label = new Label("not " + session.user.nickname + " ?");
        if (session.admin) {
            label.setTitle(session.toString());
        }
        authPanel.add(new Image(MainBundle.INSTANCE.logo()));
        authPanel.add(label);
        authPanel.add(authLink);
        mainPanel.add(authPanel);
        TabPanel tabPanel = new TabPanel();
        mainPanel.add(tabPanel);
        tabPanel.add(new WelcomeTab(), "Welcome!");
        tabPanel.setAnimationEnabled(true);
        tabPanel.selectTab(0);
        checkUserInfoCompletion(tabPanel);
    }

    void checkUserInfoCompletion(final TabPanel tabPanel) {


        LOGIN_SERVICE.getRenter(session.user, new AsyncCallback<Renter>() {
            @Override
            public void onFailure(Throwable throwable) {
                tabPanel.add(new ContactCreationForm(new AsyncCallback<Contact>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("Contact Creation Failure");
                    }

                    @Override
                    public void onSuccess(final Contact contact) {

                        final PayGroup payGroup = new PayGroup(contact.address1 + "|" +
                                contact.address2 + "|" +
                                contact.city + "|" +
                                contact.state + "|" +
                                contact.zip);

                        /*    LOGIN_SERVICE.commitParentEntity(payGroup, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("commitParentEntity failure for PayGroup");
                    }

                    @Override
                    public void onSuccess(Void aVoid) {*/
                        LOGIN_SERVICE.commitParentEntity(new Renter(session.user, payGroup, contact), new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Window.alert("commitParentEntity failure for Renter");
                            }

                            @Override
                            public void onSuccess(Void aVoid) {

                                tabPanel.remove(1);
                                checkUserInfoCompletion(tabPanel);
                            }
                        });

                        //    }
                        //  });
                    }
                })
                        , "Start now!");
                tabPanel.selectTab(0);
            }

            @Override
            public void onSuccess(final Renter renter) {
                {

                    userMain(tabPanel);
                }
            }
        });


    }

    private void userMain(TabPanel tabPanel) {
        tabPanel.add(new Label("PlaceHolder"), "Money Agenda");
        tabPanel.add(new Label("PlaceHolder"), "Add/manage Your Payees");
        tabPanel.add(new Label("PlaceHolder"), "Add/manage new Group Payments");
        tabPanel.add(new Label("PlaceHolder"), "Add/manage Your Bills");
        tabPanel.add(new Label("PlaceHolder"), "Add/manage Payment Methods");
        
    }

}


