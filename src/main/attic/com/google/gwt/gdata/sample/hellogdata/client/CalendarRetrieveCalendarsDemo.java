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
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.calendar.CalendarEntry;
import com.google.gwt.gdata.client.calendar.CalendarFeed;
import com.google.gwt.gdata.client.calendar.CalendarFeedCallback;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of a
 * user's calendars.
 */
public class CalendarRetrieveCalendarsDemo extends GDataDemo {

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
        return new CalendarRetrieveCalendarsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the allcalendars feed to retrieve " +
            "the authenticated user's list of calendars (primary," +
            " secondary and subscribed). The feed result is iterated " +
            "through to print out each calendar's title.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Retrieving all calendars";
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
  public CalendarRetrieveCalendarsDemo() {
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
   * the calendars feed uri. In GData all get, insert, update and 
   * delete methods always receive a callback defining success and 
   * failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the calendar entries.
   * 
   * @param calendarsFeedUri The uri of the calendars feed
   */
  private void getCalendars(String calendarsFeedUri) {
    showStatus("Loading calendars feed...", false);
    service.getAllCalendarsFeed(calendarsFeedUri, new CalendarFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Calendar feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarFeed result) {
        CalendarEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no Calendars.", false);
        } else {
          showData(result.getEntries());
        }
      }
    });
  }

  /**
  * Displays a set of Calendar entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Title, Color 
  * and Updated are displayed.
  * 
  * @param entries The Calendar entries to display.
  */
  private void showData(CalendarEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "Color", "Updated" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      CalendarEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label(entry.getColor().getValue()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2,
          new Label(entry.getUpdated().getValue().getDate().toString()));
    }
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
        "HelloGData_Calendar_RetrieveCalendarsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getCalendars(
          "http://www.google.com/calendar/feeds/default/allcalendars/full");
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
  }
}