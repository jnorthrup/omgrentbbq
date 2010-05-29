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
import com.google.gwt.gdata.client.gbase.Attribute;
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.gbase.ItemsEntry;
import com.google.gwt.gdata.client.gbase.ItemsEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create an item.
 */
public class GoogleBaseCreateItemDemo extends GDataDemo {

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
        return new GoogleBaseCreateItemDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create and insert " +
            "a new item. The items feed post URL " +
            "(http://www.google.com/base/feeds/items) is used to insert a " +
            "new item entry for the authenticated user.</p>";
      }

      @Override
      public String getName() {
        return "Base - Creating an item";
      }
    };
  }

  private GoogleBaseService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/base/feeds/";

  /**
   * Setup the Google Base service and create the main content panel.
   * If the user is not logged on to Google Base display a message,
   * otherwise start the demo by creating an item.
   */
  public GoogleBaseCreateItemDemo() {
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
   * Create an item by inserting an item entry into
   * an items feed.
   * Set the item's title to an arbitrary string. Here
   * we prefix the title with 'GWT-GoogleBase-Client' so that
   * we can identify which items were created by this demo.
   * The new item is created as a product review with values
   * for the default product review attributes.
   * On success and failure, display a status message.
   * 
   * @param itemsFeedUri The uri of the items feed into which
   * to insert the new item entry
   */
  private void createItem(String itemsFeedUri) {
    showStatus("Creating item...", false);
    ItemsEntry entry = ItemsEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-GoogleBase-Client - inserted item");
    entry.setContent(Text.newInstance());
    entry.getContent().setText("GData is great data!! :)");
    
    Attribute targetCountry = Attribute.newInstance();
    targetCountry.setValue("US");
    Attribute reviewType = Attribute.newInstance();
    reviewType.setValue("Product Review");
    Attribute nameOfItem = Attribute.newInstance();
    nameOfItem.setValue("gwt-gdata");
    Attribute expirationDate = Attribute.newInstance();
    expirationDate.setValue("2038-01-19T03:14:07Z");
    Attribute rating = Attribute.newInstance();
    rating.setValue("5-Excellent");
    Attribute customerId = Attribute.newInstance();
    customerId.setValue("5752122");
    Attribute itemType = Attribute.newInstance();
    itemType.setValue("Reviews");
    Attribute itemLanguage = Attribute.newInstance();
    itemLanguage.setValue("en");
    
    entry.setAttribute("target_country", targetCountry);
    entry.setAttribute("review_type", reviewType);
    entry.setAttribute("name_of_item_reviewed", nameOfItem);
    entry.setAttribute("expiration_date", expirationDate);
    entry.setAttribute("rating", rating);
    entry.setAttribute("customer_id", customerId);
    entry.setAttribute("item_type", itemType);
    entry.setAttribute("item_language", itemLanguage);
    
    service.insertEntry(itemsFeedUri, entry, new ItemsEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating an item: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ItemsEntry result) {
        showStatus("Created an item.", false);
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
        "HelloGData_GoogleBase_CreateItemDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create an item");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createItem("http://www.google.com/base/feeds/items");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Base.", true);
    }
  }
}