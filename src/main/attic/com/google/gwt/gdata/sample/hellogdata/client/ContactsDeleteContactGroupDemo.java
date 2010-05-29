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
import com.google.gwt.gdata.client.GDataRequestParameters;
import com.google.gwt.gdata.client.GDataSystemPackage;
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
 * The following example demonstrates how to delete a contact group.
 */
public class ContactsDeleteContactGroupDemo extends GDataDemo {

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
        return new ContactsDeleteContactGroupDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to delete a contact " +
            "group entry. It obtains the contact group that has a title " +
            "that starts with 'GWT-Contacts-Client' and deletes the " +
            "group.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Deleting contact groups";
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
  public ContactsDeleteContactGroupDemo() {
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
   * Delete a contact group entry using the Contacts service and
   * the contact group entry uri.
   * On success and failure, display a status message.
   * 
   * @param contactGroupEntryUri The uri of the contact group entry to delete
   * @param etag The etag of the entry to delete
   */
  private void deleteContactGroup(String contactGroupEntryUri, String etag) {
    showStatus("Deleting a contact group...", false);
    GDataRequestParameters pars = GDataRequestParameters.newInstance();
    pars.setEtag(etag);
    service.deleteEntry(contactGroupEntryUri,
        new ContactGroupEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while deleting a contact group: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactGroupEntry result) {
        showStatus("Deleted a contact group.", false);
      }
    }, pars);
  }

  /**
   * Retrieve the contact groups feed using the Contacts service and
   * the contact groupd feed uri.
   * On success, identify the first group entry with a title starting
   * with "GWT-Contacts-Client", this is the group that will be deleted.
   * If no contact group is found, display a message.
   * Otherwise call deleteContactGroup to delete the group entry.
   * Alternatively we could also have used deleteContactGroup.deleteEntry to
   * delete the contact group, but the effect is the same.
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
        for (ContactGroupEntry group : entries) {
          String title = group.getTitle().getText();
          if (title.startsWith("GWT-Contacts-Client")) {
            targetGroup = group;
            break;
          }
        }
        if (targetGroup == null) {
          showStatus("No contacts were found with a title starting with " +
              "'GWT-Contacts-Client'.", false);
        } else {
          String contactGroupEntryUri = targetGroup.getEditLink().getHref();
          deleteContactGroup(contactGroupEntryUri, targetGroup.getEtag());
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
    service = ContactsService.newInstance(
        "HelloGData_Contacts_DeleteContactGroupDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Delete a contact group");
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