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
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.maps.FeatureEntry;
import com.google.gwt.gdata.client.maps.FeatureFeed;
import com.google.gwt.gdata.client.maps.FeatureFeedCallback;
import com.google.gwt.gdata.client.maps.MapEntry;
import com.google.gwt.gdata.client.maps.MapFeed;
import com.google.gwt.gdata.client.maps.MapFeedCallback;
import com.google.gwt.gdata.client.maps.MapsService;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of features
 * for a given map.
 */
public class MapsRetrieveMapFeaturesDemo extends GDataDemo {

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
        return new MapsRetrieveMapFeaturesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the features feed to retrieve a " +
            "list of features for the first map that has a title starting " +
            "with 'GWT-Maps-Client'.</p>";
      }

      @Override
      public String getName() {
        return "Maps - Retrieving map features";
      }
    };
  }

  private MapsService service;
  private FlexTable mainPanel;
  private final String scope = "http://maps.google.com/maps/feeds/maps/";

  /**
   * Setup the Google Maps service and create the main content panel.
   * If the user is not logged on to Google Maps display a message,
   * otherwise start the demo by retrieving the user's maps.
   */
  public MapsRetrieveMapFeaturesDemo() {
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
   * Retrieve the features feed for a given map using the
   * Maps service and the features feed uri.
   * In GData all get, insert, update and delete methods always
   * receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the feature entries.
   * 
   * @param mapEntryUri The uri of the map entry for which to
   * retrieve the features feed
   */
  private void getFeatures(String mapEntryUri) {
    String featuresFeedUri =
      mapEntryUri.replace("/feeds/maps/", "/feeds/features/") + "/full";
    showStatus("Loading features feed...", false);
    service.getFeatureFeed(featuresFeedUri, new FeatureFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the features feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(FeatureFeed result) {
        FeatureEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("The map contains no features.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
   * Retrieve the maps feed using the Maps service and
   * the maps feed uri.
   * The failure handler displays an error message while the
   * success handler obtains the first map entry with a title
   * starting with "GWT-Maps-Client" and calls getFeatures to
   * retrieve the map's features.
   * If no map is found, a message is displayed.
   * 
   * @param mapsFeedUri The uri of the map feed
   */
  private void getMaps(String mapsFeedUri) {
    showStatus("Loading maps feed...", false);
    service.getMapFeed(mapsFeedUri, new MapFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the maps feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(MapFeed result) {
        MapEntry[] entries = result.getEntries();
        MapEntry targetMap = null;
        for (MapEntry entry : entries) {
          if (entry.getTitle().getText().startsWith("GWT-Maps-Client")) {
            targetMap = entry;
            break;
          }
        }
        if (targetMap == null) {
          showStatus(
              "No map found that contains 'GWT-Maps-Client' in the title.",
              false);
        } else {
          String mapEntryUri = targetMap.getId().getValue();
          getFeatures(mapEntryUri);
        }
      }
    });
  }

  /**
  * Displays a set of Google Maps feature entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Title and Address are displayed.
  * 
  * @param entries The Google Maps feature entries to display.
  */
  private void showData(FeatureEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "Address" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      FeatureEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      String address = "";
      if (entry.getPostalAddress() != null) {
        address = entry.getPostalAddress().getValue();
      }
      mainPanel.setWidget(row, 1, new Label(address));
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
    service = MapsService.newInstance(
        "HelloGData_Maps_RetrieveMapFeaturesDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getMaps("http://maps.google.com/maps/feeds/maps/default/full");
    } else {
      showStatus("You are not logged on to Google Maps.", true);
    }
  }
}