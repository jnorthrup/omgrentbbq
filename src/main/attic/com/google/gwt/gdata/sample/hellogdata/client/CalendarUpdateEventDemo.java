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
import com.google.gwt.gdata.client.EventEntry;
import com.google.gwt.gdata.client.EventEntryCallback;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.calendar.CalendarEventEntry;
import com.google.gwt.gdata.client.calendar.CalendarEventFeed;
import com.google.gwt.gdata.client.calendar.CalendarEventFeedCallback;
import com.google.gwt.gdata.client.calendar.CalendarEventQuery;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to update a Calendar event.
 */
public class CalendarUpdateEventDemo extends GDataDemo {

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
        return new CalendarUpdateEventDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to update the title " +
            "of an existing event. A full text query is used to locate " +
            "an event with a title/description containing " +
            "'GWT-Calendar-Client', where the first match " +
            "is updated with the new title. The private/full feed is " +
            "used for the event update.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Updating an event";
      }
    };
  }

  private CalendarService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/calendar/feeds/";

  /**
   * Setup the Calendar service and create the main content panel.
   * If the user is not logged on to Calendar display a message,
   * otherwise start the demo by querying the user's calendar events.
   */
  public CalendarUpdateEventDemo() {
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
   * Retrieves a Calendar events feed using a Query object.
   * In GData, feed URIs can contain querystring parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * On success, identify the first event entry with a title starting
   * with "GWT-Calendar-Client", this will be the event that will be updated.
   * If no event is found, display a message.
   * Otherwise call updateEvent to update the event.
   * 
   * @param eventsFeedUri The uri of the events feed
   */
  private void queryEvents(String eventsFeedUri) {
    showStatus("Querying for events...", false);
    CalendarEventQuery query = CalendarEventQuery.newInstance(eventsFeedUri);
    query.setFullTextQuery("GWT-Calendar-Client");
    service.getEventsFeed(query, new CalendarEventFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Event feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEventFeed result) {
        CalendarEventEntry[] entries =
          (CalendarEventEntry[]) result.getEntries();
        if (entries.length == 0) {
          showStatus(
              "No events found containing the text 'GWT-Calendar-Client'.",
              false);
        } else {
          CalendarEventEntry targetEvent = null;
          for (CalendarEventEntry entry : entries) {
            String title = entry.getTitle().getText();
            if (title.startsWith("GWT-Calendar-Client")) {
              targetEvent = entry;
              break;
            }
          }
          if (targetEvent == null) {
            showStatus("Did not find a event entry whose title starts with " +
                "the prefix 'GWT-Calendar-Client'.", false);
          } else {
            updateEvent(targetEvent);
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
   * Starts this demo.
   */
  private void startDemo() {
    service = CalendarService.newInstance(
        "HelloGData_Calendar_UpdateEventDemo_v12.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Update an event");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          queryEvents(
              "http://www.google.com/calendar/feeds/default/private/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
      }
  
  /**
   * Update an event by making use of the updateEntry
   * method of the Entry class.
   * Set the event's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Calendar-Client' so that
   * we can identify which events were updated by this demo.
   * On success and failure, display a status message.
   * 
   * @param targetEvent The event entry which to update
   */
  private void updateEvent(CalendarEventEntry targetEvent) {
    targetEvent.setTitle(Text.newInstance());
    targetEvent.getTitle().setText("GWT-Calendar-Client - updated event");
    showStatus("Updating a Calendar event...", false);
    targetEvent.updateEntry(new EventEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while updating a Calendar event: " +
            caught.getMessage(), true);
      }
      public void onSuccess(EventEntry result) {
        showStatus("Updated a Calendar event.", false);
      }
    });
  }
}