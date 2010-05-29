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
import com.google.gwt.gdata.client.calendar.CalendarEventEntry;
import com.google.gwt.gdata.client.calendar.CalendarEventFeed;
import com.google.gwt.gdata.client.calendar.CalendarEventFeedCallback;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of a 
 * user's calendar events.
 */
public class CalendarRetrieveEventsDemo extends GDataDemo {

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
        return new CalendarRetrieveEventsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to retrieve all " +
            "primary calendar events for the authenticated user. The " +
            "private/full feed is used to obtain the events from the " +
            "private calendar with full projection. The sample iterates " +
            "through the list of events and prints out each event's " +
            "title.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Retrieving events";
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
  public CalendarRetrieveEventsDemo() {
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
   * Retrieve the Calendar events feed using the Calendar service and
   * the events feed uri. In GData all get, insert, update and delete methods
   * always receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the event entries.
   * 
   * @param eventsFeedUri The uri of the events feed
   */
  private void getEvents(String eventsFeedUri) {
    showStatus("Loading events feed...", false);
    service.getEventsFeed(eventsFeedUri, new CalendarEventFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Events feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEventFeed result) {
        CalendarEventEntry[] entries =
          (CalendarEventEntry[]) result.getEntries();
        if (entries.length == 0) {
          showStatus("There are no Calendar events.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Calendar event entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Title, URL 
  * and Updated are displayed.
  * 
  * @param entries The Calendar event entries to display.
  */
  private void showData(CalendarEventEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "URL", "Updated" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      CalendarEventEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      String link = entry.getHtmlLink().getHref();
      mainPanel.setWidget(row, 1,
          new HTML("<a href=\"" + link + "\">" + link + "</a>"));
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
        "HelloGData_Calendar_RetrieveEventsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getEvents("http://www.google.com/calendar/feeds/default/private/full");
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
  }
}