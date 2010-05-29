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
import com.google.gwt.gdata.client.GDataRequestParameters;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.calendar.CalendarEventEntry;
import com.google.gwt.gdata.client.calendar.CalendarEventEntryCallback;
import com.google.gwt.gdata.client.calendar.CalendarEventFeed;
import com.google.gwt.gdata.client.calendar.CalendarEventFeedCallback;
import com.google.gwt.gdata.client.calendar.CalendarEventQuery;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to delete a Calendar event.
 */
public class CalendarDeleteEventDemo extends GDataDemo {

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
        return new CalendarDeleteEventDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to delete an existing " +
            "event. A full text query is used to locate those events with " +
            "the specified text, and the first match will be deleted from " +
            "the authenticated user's primary calendar. The private/full " +
            "feed is used for event deletion.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Deleting an event";
      }
    };
  }

  private CalendarService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/calendar/feeds/";

  /**
   * Setup the Calendar service and create the main content panel.
   * If the user is not logged on to Calendar display a message,
   * otherwise start the demo by querying the user's calendars.
   */
  public CalendarDeleteEventDemo() {
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
   * Delete an event entry using the Calendar service and
   * the event entry uri.
   * On success and failure, display a status message.
   * 
   * @param eventEntryUri The uri of the event entry to delete
   * @param etag The etag of the entry to delete
   */
  private void deleteEvent(String eventEntryUri, String etag) {
    showStatus("Deleting event...", false);
    GDataRequestParameters pars = GDataRequestParameters.newInstance();
    pars.setEtag(etag);
    service.deleteEntry(eventEntryUri,
        new CalendarEventEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while deleting a Calendar event: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEventEntry result) {
        showStatus("Deleted a Calendar event.", false);
      }
    }, pars);
  }

  /**
   * Retrieves a calendar feed using a Query object.
   * In GData, feed URIs can contain querystring parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * We query for events with a title starting with 
   * "GWT-Calendar-Client", this is the event that will be deleted.
   * If no event is found, display a message.
   * Otherwise call deleteEvent to delete the event. Alternatively
   * we could also have used targetEvent.deleteEntry to
   * delete the event, but the effect is the same.
   * 
   * @param calendarsFeedUri The uri of the calendars feed
   */
  private void queryCalendars(String calendarsFeedUri) {
    showStatus("Querying for events...", false);
    CalendarEventQuery query =
        CalendarEventQuery.newInstance(calendarsFeedUri);
    query.setFullTextQuery("GWT-Calendar-Client");
    service.getEventsFeed(query, new CalendarEventFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Event feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEventFeed result) {
        CalendarEventEntry[] entries = (CalendarEventEntry[]) result.getEntries();
        if (entries.length == 0) {
          showStatus(
              "No events found containing the text 'GWT-Calendar-Client'.",
              false);
        } else {
          CalendarEventEntry targetEvent = entries[0];
          String eventEntryUri = targetEvent.getEditLink().getHref();
          deleteEvent(eventEntryUri, targetEvent.getEtag());
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
   * Starts this demo.
   */
  private void startDemo() {
    service = CalendarService.newInstance(
        "HelloGData_Calendar_DeleteEventDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Delete an event");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          queryCalendars(
              "http://www.google.com/calendar/feeds/default/private/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
  }
}