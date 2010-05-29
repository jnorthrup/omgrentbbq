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
import com.google.gwt.gdata.client.gbase.AttributesEntry;
import com.google.gwt.gdata.client.gbase.AttributesFeed;
import com.google.gwt.gdata.client.gbase.AttributesFeedCallback;
import com.google.gwt.gdata.client.gbase.GmAttribute;
import com.google.gwt.gdata.client.gbase.GmValue;
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of attributes
 * for an item type.
 */
public class GoogleBaseRetrieveItemTypeAttributesDemo extends GDataDemo {

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
        return new GoogleBaseRetrieveItemTypeAttributesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the attributes feed to query for " +
            "metadata about products listed in Google Base. The attributes " +
            "feed is useful for determining what kinds of attributes other " +
            "users are using with certain item types. This example, looks " +
            "at attributes related to the products vertical.</p>";
      }

      @Override
      public String getName() {
        return "Base - Retrieving item type attributes";
      }
    };
  }

  private GoogleBaseService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/base/feeds/";

  /**
   * Setup the Google Base service and create the main content panel.
   * If the user is not logged on to Google Base display a message,
   * otherwise start the demo by retrieving item type attributes.
   */
  public GoogleBaseRetrieveItemTypeAttributesDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.GBASE)) {
      showStatus("Loading the GData Google-Base package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.GBASE);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieve the attributes feed for the product type using the
   * Google Base service and the attributes feed uri.
   * In GData all get, insert, update and delete methods always
   * receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the attribute entries.
   * 
   * @param attributesFeedUri The uri of the items feed
   */
  private void getAttributes(String attributesFeedUri) {
    showStatus("Loading attributes feed...", false);
    service.getAttributesFeed(attributesFeedUri, new AttributesFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the attributes feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(AttributesFeed result) {
        AttributesEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No attributes found.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Google Base attribute entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Name, Type and Common Values are displayed.
  * 
  * @param entries The Google Base attribute entries to display.
  */
  private void showData(AttributesEntry[] attributes) {
    mainPanel.clear();
    String[] labels = new String[] { "Name", "Type", "Common Values" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < attributes.length; i++) {
      AttributesEntry attribute = attributes[i];
      GmAttribute attributeInfo = attribute.getAttribute();
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(attributeInfo.getName()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label(attributeInfo.getType()));
      mainPanel.addCell(row);
      String commonValues = "";
      for (GmValue value : attributeInfo.getValues()) {
        commonValues += value.getValue() + "\n";
      }
      mainPanel.setWidget(row, 2, new Label(commonValues));
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
    service = GoogleBaseService.newInstance(
        "HelloGData_GoogleBase_RetrieveAttributesDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getAttributes("http://www.google.com/base/feeds/attributes/-/products");
    } else {
      showStatus("You are not logged on to Google Base.", true);
    }
  }
}