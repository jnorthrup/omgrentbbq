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
import com.google.gwt.gdata.client.sidewiki.SidewikiEntry;
import com.google.gwt.gdata.client.sidewiki.SidewikiEntryFeed;
import com.google.gwt.gdata.client.sidewiki.SidewikiEntryFeedCallback;
import com.google.gwt.gdata.client.sidewiki.SidewikiEntryQuery;
import com.google.gwt.gdata.client.sidewiki.SidewikiService;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to query entries to find
 * Sidewiki entries by a given author.
 */
public class SidewikiRetrieveEntriesDemo extends GDataDemo {

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
        return new SidewikiRetrieveEntriesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample displays Sidewiki entries created by the " +
          "current user.</p>";
      }

      @Override
      public String getName() {
        return "Sidewiki - Retrieve entries";
      }
    };
  }

  private SidewikiService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/sidewiki/feeds/";

  /**
   * Setup the Google Sidewiki service and create the main content panel.
   * Start the demo by querying Sidewiki entries.
   */
  public SidewikiRetrieveEntriesDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.SIDEWIKI)) {
      showStatus("Loading the GData Sidewiki package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.SIDEWIKI);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieves a Sidewiki entry feed using a Query object.
   * In GData, feed URIs can contain query string parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * Upon successfully receiving the entries feed, the entries 
   * are displayed to the user via the showData method.
   * 
   * @param entriesFeedUri The items feed uri.
   */
  private void queryEntries(String entriesFeedUri) {
    showStatus("Loading entries feed...", false);
    SidewikiEntryQuery query = SidewikiEntryQuery.newInstance(entriesFeedUri);
    query.setMaxResults(25);
    service.getSidewikiEntryFeed(query, new SidewikiEntryFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the entries feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(SidewikiEntryFeed result) {
        SidewikiEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no Sidewiki entries.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Sidewiki entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Name and Url are displayed.
  * 
  * @param entries The Sidewiki entries to display.
  */
  private void showData(SidewikiEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Name", "Url" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      SidewikiEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      if (entry.getHtmlLink() == null) {
        mainPanel.setWidget(row, 1, new Label("Not available"));
      } else {
        String link = entry.getHtmlLink().getHref();
        mainPanel.setWidget(row, 1,
            new HTML("<a href=\"" + link + "\" target=\"_blank\">" + 
                link +  "</a>"));
      }
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
    service = SidewikiService.newInstance(
        "HelloGData_Sidewiki_QueryEntriesByAuthorDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      queryEntries("http://www.google.com/sidewiki/feeds/entries/author/me/full");
    } else {
      showStatus("You are not logged on to Google Sidewiki.", true);
    }
  }
}