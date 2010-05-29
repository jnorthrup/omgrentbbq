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
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.maps.MapEntry;
import com.google.gwt.gdata.client.maps.MapEntryCallback;
import com.google.gwt.gdata.client.maps.MapsService;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a map.
 */
public class MapsCreateMapDemo extends GDataDemo {

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
        return new MapsCreateMapDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create and insert " +
            "a new map. The map feed post URL " +
            "(http://maps.google.com/maps/feeds/maps/default/full) is used " +
            "to insert a new map entry for the authenticated user.</p>";
      }

      @Override
      public String getName() {
        return "Maps - Creating a map";
      }
    };
  }

  private MapsService service;
  private FlexTable mainPanel;
  private final String scope = "http://maps.google.com/maps/feeds/maps/";

  /**
   * Setup the Google Maps service and create the main content panel.
   * If the user is not logged on to Google Maps display a message,
   * otherwise start the demo by creating a map.
   */
  public MapsCreateMapDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.MAPS)) {
      showStatus("Loading the GData Maps package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.MAPS);
    } else {
      startDemo();
    }
  }

  /**
   * Create a map by inserting a map entry into
   * a maps feed.
   * Set the map's title and contents to an arbitrary string. Here
   * we prefix the title with 'GWT-Maps-Client' so that
   * we can identify which maps were created by this demo.
   * On success and failure, display a status message.
   * 
   * @param mapsFeedUri The uri of the maps feed into which to
   * insert the new map entry
   */
  private void createMap(String mapsFeedUri) {
    showStatus("Creating map...", false);
    MapEntry entry = MapEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-Maps-Client - inserted map");
    service.insertEntry(mapsFeedUri, entry, new MapEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a map: " +
            caught.getMessage(), true);
      }
      public void onSuccess(MapEntry result) {
        showStatus("Created a map.", false);
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
    service = MapsService.newInstance("HelloGData_Maps_CreateMapDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a map");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createMap("http://maps.google.com/maps/feeds/maps/default/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Maps.", true);
    }
  }
}