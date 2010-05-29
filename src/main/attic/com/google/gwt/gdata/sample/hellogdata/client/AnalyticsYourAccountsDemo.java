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
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to access 50 of the account names,
 * profile names, profile ids and table ids to which your login has access.
 */
public class AnalyticsYourAccountsDemo extends GDataDemo {

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
        return new AnalyticsYourAccountsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to access 50 of the " +
            "account names, profile names, profile ids and table ids to " +
            "which your login has access.</p>";
      }

      @Override
      public String getName() {
        return "Analytics - Retrieving accounts";
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
  public AnalyticsYourAccountsDemo() {
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
   * success handler calls showData to display all the retrieved
   * Account entries.
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
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Analytics data entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Account Name,
  * Profile Name, Profile Id and Table Id are displayed.
  * 
  * @param entries The Analytics data entries to display.
  */
  private void showData(AccountEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Account Name", 
        "Profile Name", "Profile Id", "Table Id" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      AccountEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0,
          new Label(entry.getPropertyValue("ga:AccountName")));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1,
          new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2,
          new Label(entry.getPropertyValue("ga:ProfileId")));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 3,
          new Label(entry.getTableId().getValue()));
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
        "HelloGData_Analytics_YourAccountsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getAccounts("https://www.google.com/analytics/feeds/accounts/" +
          "default?max-results=50");
    } else {
      showStatus("You are not logged on to Google Analytics.", true);
    }
  }
}