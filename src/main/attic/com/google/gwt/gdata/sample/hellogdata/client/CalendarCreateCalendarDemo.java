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
import com.google.gwt.gdata.client.Where;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.calendar.CalendarEntry;
import com.google.gwt.gdata.client.calendar.CalendarEntryCallback;
import com.google.gwt.gdata.client.calendar.CalendarService;
import com.google.gwt.gdata.client.calendar.ColorProperty;
import com.google.gwt.gdata.client.calendar.HiddenProperty;
import com.google.gwt.gdata.client.calendar.TimeZoneProperty;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a calendar.
 */
public class CalendarCreateCalendarDemo extends GDataDemo {

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
        return new CalendarCreateCalendarDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create and insert " +
            "a new calendar. The owncalendars feed is used to insert the " +
            "new calendar entry for the authenticated user.</p>";
      }

      @Override
      public String getName() {
        return "Calendar - Creating a calendar";
      }
    };
  }

  private CalendarService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/calendar/feeds/";

  /**
   * Setup the Calendar service and create the main content panel.
   * If the user is not logged on to Calendar display a message,
   * otherwise start the demo by creating a calendar.
   */
  public CalendarCreateCalendarDemo() {
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
   * Create a calendar by inserting a calendar entry into
   * a calendar feed.
   * Set the calendar's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Calendar-Client' so that
   * we can identify which calendars were created by this demo.
   * We also specify values for summary, time zone, location
   * and color.
   * On success and failure, display a status message.
   * 
   * @param calendarsFeedUri The uri of the calendars feed into 
   * which to insert the new calendar entry
   */
  private void createCalendar(String calendarsFeedUri) {
    CalendarEntry entry = CalendarEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-Calendar-Client: insert calendar");
    entry.setSummary(Text.newInstance());
    entry.getSummary().setText(
        "This is a test calendar created by GWT Client");
    entry.setTimeZone(TimeZoneProperty.newInstance());
    entry.getTimeZone().setValue("America/Los_Angeles");
    Where where = Where.newInstance();
    where.setLabel("Mountain View, CA");
    where.setValueString("Mountain View, CA");
    entry.addLocation(where);
    entry.setHidden(HiddenProperty.newInstance());
    entry.getHidden().setValue(false);
    entry.setColor(ColorProperty.newInstance());
    entry.getColor().setValue(ColorProperty.VALUE_RGB_2952A3);
    showStatus("Creating calendar...", false);
    service.insertEntry(calendarsFeedUri, entry,
        new CalendarEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Calendar feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(CalendarEntry result) {
        showStatus("Created a Calendar entry titled '" + 
            result.getTitle().getText() + "'", false);
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
        "HelloGData_Calendar_CreateCalendarDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a calendar");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createCalendar("http://www.google.com/calendar/feeds/default/" +
              "owncalendars/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Calendar.", true);
    }
  }
}