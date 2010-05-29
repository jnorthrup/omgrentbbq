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
import com.google.gwt.gdata.client.contacts.ContactGroupEntry;
import com.google.gwt.gdata.client.contacts.ContactGroupEntryCallback;
import com.google.gwt.gdata.client.contacts.ContactsService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a contact group.
 */
public class ContactsCreateContactGroupDemo extends GDataDemo {

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
        return new ContactsCreateContactGroupDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create a new " +
            "contact group.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Creating contact groups";
      }
    };
  }

  private ContactsService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/m8/feeds/";

  /**
   * Setup the Contacts service and create the main content panel.
   * If the user is not logged on to Contacts display a message,
   * otherwise start the demo by creating a contact group.
   */
  private ContactsCreateContactGroupDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.CONTACTS)) {
      showStatus("Loading the GData Contacts package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.CONTACTS);
    } else {
      startDemo();
    }
  }
  
  /**
   * Create a contact group by inserting a contact group entry into
   * a contact groups feed.
   * Set the contact group's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Contacts-Client' so that
   * we can identify which groups were created by this demo.
   * On success and failure, display a status message.
   * 
   * @param contactGroupFeedUri The uri of the groups feed into which 
   * to insert the new group entry
   */
  private void createContactGroup(String contactGroupFeedUri) {
    showStatus("Creating contact group...", false);
    ContactGroupEntry entry = ContactGroupEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-Contacts-Client - Create Group");
    service.insertEntry(contactGroupFeedUri, entry,
        new ContactGroupEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a contact group: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactGroupEntry result) {
        showStatus("Created a contact group.", false);
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
    service = ContactsService.newInstance(
        "HelloGData_Contacts_CreateContactGroupDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a contact group");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createContactGroup(
              "http://www.google.com/m8/feeds/groups/default/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Contacts.", true);
    }
  }
}