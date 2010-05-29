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
import com.google.gwt.gdata.client.analytics.AccountEntry;
import com.google.gwt.gdata.client.analytics.AccountFeed;
import com.google.gwt.gdata.client.analytics.AccountFeedCallback;
import com.google.gwt.gdata.client.analytics.AnalyticsService;
import com.google.gwt.gdata.client.analytics.DataEntry;
import com.google.gwt.gdata.client.analytics.DataFeed;
import com.google.gwt.gdata.client.analytics.DataFeedCallback;
import com.google.gwt.gdata.client.analytics.DataQuery;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to get 30 days worth of Pageviews
 * and Visits from Google Analytics.
 */
public class AnalyticsVisitsDemo extends GDataDemo {

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
        return new AnalyticsVisitsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to " +
            "get 30 days worth of Pageviews and Visits from Google " +
            "Analytics.</p>";
      }

      @Override
      public String getName() {
        return "Analytics - Querying visits and pageviews";
      }
    };
  }

  private AnalyticsService service;
  private FlexTable mainPanel;
  private final String scope = "https://www.google.com/analytics/feeds/";

  /**
   * Setup the Analytics service and create the main content panel.
   * If the user is not logged on to Analytics display a message,
   * otherwise start the demo by retrieving the Analytics accounts.
   */
  public AnalyticsVisitsDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.ANALYTICS)) {
      showStatus("Loading the GData Analytics package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.ANALYTICS);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieve the Analytics accounts feed using the Analytics service and
   * the accounts feed uri. In GData all get, insert, update and delete methods
   * always receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first Account entry and
   * calls queryData to retrieve the data feed for that account.
   * 
   * @param accountsFeedUri The uri of the accounts feed
   */
  private void getAccounts(String accountsFeedUri) {
    showStatus("Loading Analytics accounts feed...", false);
    service.getAccountFeed(accountsFeedUri, new AccountFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Analytics " +
            "Accounts feed: " + caught.getMessage(), true);
      }
      public void onSuccess(AccountFeed result) {
        AccountEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no Analytics accounts.", false);
        } else {
          AccountEntry targetEntry = entries[0];
          queryData(targetEntry.getTableId().getValue());
        }
      }
    });
  }

  /**
   * Retrieves a data feed for an Analytics account using a Query object.
   * In GData, feed URIs can contain querystring parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * Upon successfully receiving the data feed, the data entries are 
   * displayed to the user via the showData method.
   * Query parameters are specified for start and end dates, dimensions,
   * metrics, sort field and direction and the IDs of the
   * account tables which should be queried.
   * 
   * @param tableId The id of the account table for which to retrieve the 
   * Analytics data.
   */
  private void queryData(String tableId) {
    DataQuery query = DataQuery.newInstance(
        "https://www.google.com/analytics/feeds/data");
    query.setStartDate("2009-07-01");
    query.setEndDate("2009-07-31");
    query.setDimensions("ga:date");
    query.setMetrics("ga:visits,ga:pageviews");
    query.setSort("ga:date");
    query.setIds(tableId);
    showStatus("Loading data feed...", false);
    service.getDataFeed(query, new DataFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Analytics " +
            "Data feed: " + caught.getMessage(), true);
      }
      public void onSuccess(DataFeed result) {
        showData(result.getEntries());
      }
    });
  }

  /**
  * Displays a set of Analytics data entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Date, Visits 
  * and Pageviews are displayed.
  * 
  * @param entries The Analytics data entries to display.
  */
  private void showData(DataEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Date", "Visits", "Pageviews" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      DataEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0,
          new Label(entry.getStringValueOf("ga:date")));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, 
          new Label("" + entry.getNumericValueOf("ga:visits")));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2, 
          new Label("" + entry.getNumericValueOf("ga:pageviews")));
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
    service = AnalyticsService.newInstance(
        "HelloGData_Analytics_VisitsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getAccounts("https://www.google.com/analytics/feeds/accounts/" +
          "default?max-results=50");
    } else {
      showStatus("You are not logged on to Google Analytics.", true);
    }
  }
}