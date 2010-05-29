package com.omgrentbbq.client;


import com.google.gwt.accounts.client.AuthSubStatus;
import com.google.gwt.accounts.client.User;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gdata.client.DateTime;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.When;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.calendar.CalendarEventEntry;
import com.google.gwt.gdata.client.calendar.CalendarEventEntryCallback;
import com.google.gwt.gdata.client.calendar.CalendarExtendedProperty;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
 import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.resources.MainBundle;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.Member;
import com.omgrentbbq.shared.model.PayGroup;
import com.omgrentbbq.shared.model.UserSession;

import java.util.Date;
import java.util.Random;

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
    private static final String GDATA_API_KEY = "ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA";
    private static final Random RANDOM = new Random();
    private SimplePanel htmlHolder;
    //"ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA-OX2XYmEAa76BRl5-EVx5PbQ1VFzCJyQmfA43hlLA";

    public void onModuleLoad() {

        mainPanel.add(new Image(MainBundle.INSTANCE.logo()), DockPanel.WEST);
        mainPanel.add(authPanel, DockPanel.EAST);
        RootPanel.get().clear();
        RootPanel.get().add(mainPanel);
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


        LOGIN_SERVICE.getRenter(session.user, new AsyncCallback<Member>() {
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


                        LOGIN_SERVICE.commitParentEntity(new Member(session.user, payGroup, contact), new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                Window.alert("commitParentEntity failure for Member");
                            }

                            @Override
                            public void onSuccess(Void aVoid) {

                                tabPanel.remove(1);
                                checkUserInfoCompletion(tabPanel);
                            }
                        });

                    }
                })
                        , "Start now!");
                tabPanel.selectTab(0);
            }

            @Override
            public void onSuccess(final Member member) {


                userMain(tabPanel);

            }
        });


    }

    private void userMain(final TabPanel tabPanel) {

        htmlHolder = new SimplePanel();
        tabPanel.add(htmlHolder, "Summary Page");

        freshCalendar(htmlHolder);
        tabPanel.add(new MyHorizontalPanel(), "Manage");
        tabPanel.add(new Label("PlaceHolder"), "Groups");
        tabPanel.add(new Label("PlaceHolder"), "Support");

    }

    private   void freshCalendar(SimplePanel htmlHolder) {
        final HTML html = new HTML(
                "<iframe " +
                "src='http://www.google.com/calendar/embed?height=600&amp;wkst=1&amp;bgcolor=%23FFFFFF&amp;" +
                "src=" +
                session.user.emailAddress +
                "&amp;color=%231B887A&amp;ctz=Pacific' " +
                "style='border-width:0'  width=600 height=500 frameborder=0 scrolling='no'&amp;_r"+  new Random().nextGaussian()+" />"  );
        htmlHolder.clear();
        htmlHolder.setWidget(html);
    }


    class MyHorizontalPanel extends HorizontalPanel {

        private CalendarService service;
        private FlexTable mainPanel = new FlexTable();

        {
            add(mainPanel);
            init();
        }

        private final String scope = "http://www.google.com/calendar/feeds/";

        /**
         * Setup the Calendar service and create the main content panel.
         * If the user is not logged on to Calendar display a message,
         * otherwise start the demo by creating an event.
         */
        public void init() {
            if (!GData.isLoaded(GDataSystemPackage.CALENDAR)) {
                showStatus("Loading the GData Calendar package...", false);
                GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
                    public void run() {
                        startDemo();
                    }
                }, GDataSystemPackage.CALENDAR);
            } else {
                startDemo();
            }
        }

        /**
         * Create a calendar event by inserting an event entry into
         * a calendar events feed.
         * Set the event's title to an arbitrary string. Here
         * we prefix the title with 'GWT-Calendar-Client' so that
         * we can identify which events were created by this demo.
         * We also specify values for time span and an extended
         * property.
         * On success and failure, display a status message.
         *
         * @param eventsFeedUri The uri of the events feed into which to
         *                      insert the new event
         */
        @SuppressWarnings("deprecation")
        private void createEvent(String eventsFeedUri) {
            showStatus("Creating event reminder...", false);
            CalendarEventEntry entry = CalendarEventEntry.newInstance();
            entry.setTitle(Text.newInstance());
            entry.getTitle().setText("GWT-Calendar-Client: add extended property");
            When when = When.newInstance();
            Date startTime = new Date();
            Date endTime = new Date();
            endTime.setHours(endTime.getHours() + 1);
            when.setStartTime(DateTime.newInstance(startTime));
            when.setEndTime(DateTime.newInstance(endTime));
            entry.addTime(when);
            CalendarExtendedProperty extendedProp =
                    CalendarExtendedProperty.newInstance();
            extendedProp.setName("mydata");
            extendedProp.setValue("xyz");
            entry.addExtendedProperty(extendedProp);
            service.insertEntry(eventsFeedUri, entry,
                    new CalendarEventEntryCallback() {
                        public void onFailure(CallErrorException caught) {
                            showStatus("An error occurred while creating a Calendar event " +
                                    "reminder: " + caught.getMessage(), true);
                        }

                        public void onSuccess(CalendarEventEntry result) {
                            showStatus("Created an event with an extended property.", false);
                                                                                            freshCalendar(htmlHolder);

                        }
                    });
        }

        /**
         * Displays a status message to the user.
         *
         * @param message The message to display.
         * @param isError Indicates whether the status is an error status.
         */
        private void showStatus(String message, boolean isError) {
            mainPanel.clear();
            mainPanel.insertRow(0);
            mainPanel.addCell(0);
            Label msg = new Label(message);
            if (isError) {
                msg.setStylePrimaryName("Caption");
            }
            mainPanel.setWidget(0, 0, msg);
        }

        /**
         * Starts this demo.
         */
        private void startDemo() {
            service = CalendarService.newInstance(
                    "HelloGData_Calendar_CreateEventWithExtendedPropertyDemo_v2.0");
            if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
                Button startButton = new Button(
                        "Create an event with an extended property");
                startButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        createEvent(
                                "http://www.google.com/calendar/feeds/default/private/full");
                    }
                });
                mainPanel.setWidget(0, 0, startButton);
            } else {
                showStatus("You are not logged on to Google Calendar.", true);
            }
        }
    }
}