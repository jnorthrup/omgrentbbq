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
import com.google.gwt.gdata.client.gbase.Attribute;
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.gbase.ItemsEntry;
import com.google.gwt.gdata.client.gbase.ItemsFeed;
import com.google.gwt.gdata.client.gbase.ItemsFeedCallback;
import com.google.gwt.gdata.client.gbase.MapAttribute;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of an
 * item's attributes.
 */
public class GoogleBaseRetrieveItemAttributesDemo extends GDataDemo {

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
        return new GoogleBaseRetrieveItemAttributesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the items feed to find an item " +
            "with a title starting with 'GWT-GoogleBase-Client' and " +
            "displays its attributes.</p>";
      }

      @Override
      public String getName() {
        return "Base - Retrieving item attributes";
      }
    };
  }

  private GoogleBaseService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/base/feeds/";

  /**
   * Setup the Google Base service and create the main content panel.
   * If the user is not logged on to Google Base display a message,
   * otherwise start the demo by retrieving the user's items.
   */
  public GoogleBaseRetrieveItemAttributesDemo() {
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
   * Retrieve the items feed using the Google Base service and
   * the items feed uri. In GData all get, insert, update
   * and delete methods always receive a callback defining success
   * and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first Item entry with a title
   * starting with "GWT-GoogleBase-Client" and calls showData
   * to display the item's attributes.
   * 
   * @param itemsFeedUri The uri of the items feed
   */
  private void getItems(String itemsFeedUri) {
    showStatus("Loading items feed...", false);
    service.getItemsFeed(itemsFeedUri, new ItemsFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the items feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ItemsFeed result) {
        ItemsEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no items.", false);
        } else {
          ItemsEntry targetItem = null;
          for (ItemsEntry entry : entries) {
            String title = entry.getTitle().getText();
            if (title.startsWith("GWT-GoogleBase-Client")) {
              targetItem = entry;
              break;
            }
          }
          if (targetItem == null) {
            showStatus("No item found that contains 'GWT-GoogleBase-Client' " +
                "in the title.", false);
          } else {
            showData(targetItem.getAttributes());
          }
        }
      }
    });
  }

  /**
  * Displays a set of Google Base item attributes in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Name, Type and Value are displayed.
  * 
  * @param entries The Google Base item attributes to display.
  */
  private void showData(MapAttribute attributes) {
    mainPanel.clear();
    String[] labels = new String[] { "Name", "Type", "Value" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    String[] attributeNames = attributes.keys();
    for (int i = 0; i < attributeNames.length; i++) {
      String attributeName = attributeNames[i];
      Attribute attribute = attributes.get(attributeName)[0];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(attributeName));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label(attribute.getType()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2, new Label(attribute.getValue()));
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
        "HelloGData_GoogleBase_RetrieveItemAttributesDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getItems("http://www.google.com/base/feeds/items");
    } else {
      showStatus("You are not logged on to Google Base.", true);
    }
  }
}