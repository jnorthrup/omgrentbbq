package com.omgrentbbq.client;

import com.google.gwt.accounts.client.AuthSubStatus;
import com.google.gwt.accounts.client.User;
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
import com.google.gwt.user.client.ui.*;

import java.util.Date;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 29, 2010
 * Time: 1:19:31 AM
 */
class CreateEventUi extends HorizontalPanel {
    private CalendarService service;
    private FlexTable managePanel;

    private final String scope;
    private final AgendaHelper helper;

    public CreateEventUi(AgendaHelper helper) {
        this.helper = helper;
        managePanel = new FlexTable();
        add(managePanel);
        if (!GData.isLoaded(GDataSystemPackage.CALENDAR)) {
            showStatus("Loading the GData Calendar package...", false);
            GData.loadGDataApi(OmgRentBbq.GDATA_API_KEY, new Runnable() {
                public void run() {
                    manageCalendar();
                }
            }, GDataSystemPackage.CALENDAR);
        } else {
            manageCalendar();
        }
        scope = "http://www.google.com/m8/feeds/ http://www.google.com/calendar/feeds/ http://maps.google.com/maps/feeds ";
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
                        helper.freshCalendar();

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
        managePanel.clear();
        managePanel.insertRow(0);
        managePanel.addCell(0);
        Label msg = new Label(message);
        if (isError) {
            msg.setStylePrimaryName("Caption");
        }
        managePanel.setWidget(0, 0, msg);
    }

    /**
     * Starts this demo.
     */
    private void manageCalendar() {
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
            managePanel.setWidget(0, 0, startButton);
        } else {
            showStatus("You are not logged on to Google Calendar.", true);
            final Anchor logInCalButton = new Anchor();
            logInCalButton.setText("Log in to google calendar");
            logInCalButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    User.login(scope);
                }
            });
            managePanel.setWidget(0, 0, logInCalButton);
        }
    }
}
