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
import com.google.gwt.gdata.client.contacts.ContactGroupFeed;
import com.google.gwt.gdata.client.contacts.ContactGroupFeedCallback;
import com.google.gwt.gdata.client.contacts.ContactsService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to update a contact group.
 */
public class ContactsUpdateContactGroupDemo extends GDataDemo {

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
        return new ContactsUpdateContactGroupDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to update a contact group " +
            "entry. It obtains the first contact group entry that has a " +
            "title that starts with 'GWT-Contacts-Client' and updates its " +
            "title.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Updating contact groups";
      }
    };
  }

  private ContactsService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/m8/feeds/";

  /**
   * Setup the Contacts service and create the main content panel.
   * If the user is not logged on to Contacts display a message,
   * otherwise start the demo by retrieving the user's contact groups.
   */
  public ContactsUpdateContactGroupDemo() {
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
   * Retrieve the contact groups feed using the Contacts service and
   * the contact groups feed uri.
   * On success, obtain the first group entry with a title starting
   * with "GWT-Contacts-Client", this is the group that will be updated.
   * If no contact group is found, display a message.
   * Otherwise call updateContactGroup to update the group.
   * 
   * @param contactGroupsFeedUri The contact groups feed uri
   */
  private void getContactGroups(String contactGroupsFeedUri) {
    showStatus("Loading contact groups feed...", false);
    service.getContactGroupFeed(contactGroupsFeedUri,
        new ContactGroupFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the contact groups " +
            "feed: " + caught.getMessage(), true);
      }
      public void onSuccess(ContactGroupFeed result) {
        ContactGroupEntry[] entries = result.getEntries();
        ContactGroupEntry targetGroup = null;
        for (ContactGroupEntry contact : entries) {
          String title = contact.getTitle().getText();
          if (title.startsWith("GWT-Contacts-Client")) {
            targetGroup = contact;
            break;
          }
        }
        if (targetGroup == null) {
          showStatus("No contacts groups were found with a title starting " +
              "with 'GWT-Contacts-Client'.", false);
        } else {
          updateContactGroup(targetGroup);
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
   * Update a contact group by making use of the updateEntry
   * method of the Entry class.
   * Set the group's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Contacts-Client' so that
   * we can identify which groups were updated by this demo.
   * On success and failure, display a status message.
   * 
   * @param targetGroup The contact group entry which to update
   */
  private void updateContactGroup(ContactGroupEntry targetGroup) {
    showStatus("Updating a contact group...", false);
    targetGroup.setTitle(Text.newInstance());
    targetGroup.getTitle().setText("GWT-Contacts-Client - updated group");
    targetGroup.updateEntry(new ContactGroupEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while updating a contact group: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactGroupEntry result) {
        showStatus("Updated a contact group.", false);
      }
    });
  }
  
  /**
   * Starts this demo.
   */
  private void startDemo() {
    service = ContactsService.newInstance(
        "HelloGData_Contacts_UpdateContactGroupDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Update a contact group");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          getContactGroups(
              "http://www.google.com/m8/feeds/groups/default/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Contacts.", true);
    }
  }
}