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
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.gbase.ItemsEntry;
import com.google.gwt.gdata.client.gbase.ItemsEntryCallback;
import com.google.gwt.gdata.client.gbase.ItemsFeed;
import com.google.gwt.gdata.client.gbase.ItemsFeedCallback;
import com.google.gwt.gdata.client.gbase.MapAttribute;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to update an item.
 */
public class GoogleBaseUpdateItemDemo extends GDataDemo {

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
        return new GoogleBaseUpdateItemDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample code demonstrates how to update an existing " +
            "item of the authenticated user. It retrieves a list of the " +
            "user's items, and updates the first item with a title that " +
            "starts with 'GWT-GoogleBase-Client' with a new title and " +
            "target country.</p>";
      }

      @Override
      public String getName() {
        return "Base - Updating an item";
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
  public GoogleBaseUpdateItemDemo() {
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
   * starting with "GWT-GoogleBase-Client" and calls updateItem
   * to update the item.
   * If no item is found a message is displayed.
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
        ItemsEntry targetEntry = null;
        for (ItemsEntry entry : entries) {
          if (entry.getTitle().getText().startsWith("GWT-GoogleBase-Client")) {
            targetEntry = entry;
            break;
          }
        }
        if (targetEntry == null) {
          showStatus("No item found that contains 'GWT-GoogleBase-Client' " +
              "in the title.", false);
        } else {
          updateItem(targetEntry);
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
    service = GoogleBaseService.newInstance(
        "HelloGData_GoogleBase_UpdateItemDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Update an item");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          getItems("http://www.google.com/base/feeds/items/");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Base.", true);
    }
  }
  
  /**
   * Update an item by making use of the updateEntry
   * method of the Entry class.
   * Set the item's title to an arbitrary string. Here
   * we prefix the title with 'GWT-GoogleBase-Client' so that
   * we can identify which items were updated by this demo.
   * We also update the target_country property for this item.
   * On success and failure, display a status message.
   * 
   * @param itemsEntry The item entry which to update
   */
  private void updateItem(ItemsEntry itemsEntry) {
    showStatus("Updating item...", false);
    itemsEntry.setTitle(Text.newInstance());
    itemsEntry.getTitle().setText("GWT-GoogleBase-Client - updated item");
    MapAttribute attributes = itemsEntry.getAttributes();
    if (attributes.contains("target_country")) {
      attributes.get("target_country")[0].setValue("UK");
    }
    itemsEntry.updateEntry(new ItemsEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while updating an item: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ItemsEntry result) {
        showStatus("Updated an item.", false);
      }
    });
  }
}