/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.gdata.sample.hellogdata.client;

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
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

import java.util.Date;

/**
 * The following example demonstrates how to create a single Calendar event.
 */
public class CalendarCreateSingleEventDemo extends GDataDemo {

  /**
   * This method is used by the main sample app to obtain
   * information on this sample and a sample instance.
   * 
   * @return An instance of this demo.
   */
  public static GDataDemoInfo init() {
    return new GDataDemoInfo() {

      @Override
      public GDataDemo createInstance() {
        return new CalendarCreateSingleEventDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create and insert a " +
            "single event into the authenticated user's primary calendar. " +
            "The private/full feed is used for event insertion.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Creating a single event";
      }
    };
  }

  private CalendarService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/calendar/feeds/";

  /**
   * Setup the Calendar service and create the main content panel.
   * If the user is not logged on to Calendar display a message,
   * otherwise start the demo by creating an event.
   */
  public CalendarCreateSingleEventDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
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
   * We also specify values for start and end times.
   * On success and failure, display a status message.
   * 
   * @param eventsFeedUri The uri of the events feed into which to 
   * insert the new event
   */
  @SuppressWarnings("deprecation")
  private void createEvent(String eventsFeedUri) {
    showStatus("Creating Calendar event...", false);
    CalendarEventEntry eventEntry = CalendarEventEntry.newInstance();
    eventEntry.setTitle(Text.newInstance());
    eventEntry.getTitle().setText("GWT-Calendar-Client: insert event");
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setHours(endDate.getHours() + 1);
    When when = When.newInstance();
    when.setStartTime(DateTime.newInstance(startDate));
    when.setEndTime(DateTime.newInstance(endDate));
    eventEntry.addTime(when);
    service.insertEntry(eventsFeedUri, eventEntry,
        new CalendarEventEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a Calendar event: " + 
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEventEntry result) {
        showStatus("Created a Calendar event taking place in the next hour.",
            false);
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
      msg.setStylePrimaryName("hm-error");
    }
    mainPanel.setWidget(0, 0, msg);
  }
  
  /**
   * Starts this demo.
   */
  private void startDemo() {
    service = CalendarService.newInstance(
        "HelloGData_Calendar_CreateSingleEventDemo_v1.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create an event");
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