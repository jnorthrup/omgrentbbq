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
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.calendar.CalendarEntry;
import com.google.gwt.gdata.client.calendar.CalendarEntryCallback;
import com.google.gwt.gdata.client.calendar.CalendarFeed;
import com.google.gwt.gdata.client.calendar.CalendarFeedCallback;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to update a calendar.
 */
public class CalendarUpdateCalendarDemo extends GDataDemo {

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
        return new CalendarUpdateCalendarDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to update an existing " +
            "calendar of the authenticated user. It retrieves a list of " +
            "the user's own calendars, and updates the first calendar with " +
            "a title that starts with 'GWT-Calendar-Client'. The " +
            "owncalendars feed is used to update an existing calendar.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Updating a calendar";
      }
    };
  }

  private CalendarService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/calendar/feeds/";

  /**
   * Setup the Calendar service and create the main content panel.
   * If the user is not logged on to Calendar display a message,
   * otherwise start the demo by retrieving the user's calendars.
   */
  public CalendarUpdateCalendarDemo() {
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
   * Retrieve the calendars feed using the Calendar service and
   * the calendars feed uri.
   * On success, identify the first calendar entry with a title 
   * starting with "GWT-Calendar-Client", this will be the calendar
   * that will be updated.
   * If no calendar is found, display a message.
   * Otherwise call updateCalendar to update the calendar.
   * 
   * @param calendarsFeedUri The uri of the calendars feed
   */
  private void getCalendars(String calendarsFeedUri) {
    showStatus("Loading calendars...", false);
    service.getOwnCalendarsFeed(calendarsFeedUri, new CalendarFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Calendar feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarFeed result) {
        CalendarEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no calendars.", false);
        } else {
          CalendarEntry targetCalendar = null;
          for (CalendarEntry entry : entries) {
            String title = entry.getTitle().getText();
            if (title.startsWith("GWT-Calendar-Client")) {
              targetCalendar = entry;
              break;
            }
          }
          if (targetCalendar == null) {
            showStatus("Did not find a calendar entry whose title starts " +
                "with the prefix 'GWT-Calendar-Client'.", false);
          } else {
            updateCalendar(targetCalendar);
          }
        }
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
   * Update a calendar by making use of the updateEntry
   * method of the Entry class.
   * Set the calendar's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Calendar-Client' so that
   * we can identify which calendars were updated by this demo.
   * On success and failure, display a status message.
   * 
   * @param targetCalendar The calendar entry which to update
   */
  private void updateCalendar(CalendarEntry targetCalendar) {
    targetCalendar.setTitle(Text.newInstance());
    targetCalendar.getTitle().setText(
        "GWT-Calendar-Client - updated calendar");
    showStatus("Updating calendar...", false);
    targetCalendar.updateEntry(new CalendarEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while updating a calendar: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEntry result) {
        showStatus("Updated a calendar.", false);
      }
    });
  }
  
  /**
   * Starts this demo.
   */
  private void startDemo() {
    service = CalendarService.newInstance(
        "HelloGData_Calendar_UpdateCalendarDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Update a calendar");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          getCalendars(
              "http://www.google.com/calendar/feeds/default/" +
                  "owncalendars/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
  }
}