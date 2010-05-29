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
import com.google.gwt.gdata.client.DateTime;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.PhoneNumber;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.contacts.ContactEntry;
import com.google.gwt.gdata.client.contacts.ContactFeed;
import com.google.gwt.gdata.client.contacts.ContactFeedCallback;
import com.google.gwt.gdata.client.contacts.ContactQuery;
import com.google.gwt.gdata.client.contacts.ContactsService;
import com.google.gwt.gdata.client.contacts.PersonEntry;
import com.google.gwt.gdata.client.contacts.PersonEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

import java.util.Date;

/**
 * The following example demonstrates how to update a contact.
 */
public class ContactsUpdateContactDemo extends GDataDemo {

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
        return new ContactsUpdateContactDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to update a contact entry. " +
            "It obtains the first contact entry with a title starting with " +
            "'GWT-Contacts-Client' and updates its title.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Updating contacts";
      }
    };
  }

  private ContactsService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/m8/feeds/";

  /**
   * Setup the Contacts service and create the main content panel.
   * If the user is not logged on to Contacts display a message,
   * otherwise start the demo by querying the user's contacts.
   */
  public ContactsUpdateContactDemo() {
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
   * Retrieves a contacts feed using a Query object.
   * In GData, feed URIs can contain querystring parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * On success, obtain the first contact entry with a title starting
   * with "GWT-Contacts-Client", this is the contact that will be updated.
   * If no contact is found, display a message.
   * Otherwise call updateContact to update the contact.
   * 
   * @param contactsFeedUri The contacts feed uri
   */
  private void queryContacts(String contactsFeedUri) {
    showStatus("Querying contacts...", false);
    ContactQuery query = ContactQuery.newInstance(contactsFeedUri);
    Date today = new Date();
    DateTime updatedMin = DateTime.newInstance(today, true);
    query.setUpdatedMin(updatedMin);
    query.setSortOrder(ContactQuery.SORTORDER_DESCENDING);
    service.getContactFeed(query, new ContactFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Contacts feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactFeed result) {
        ContactEntry[] entries = result.getEntries();
        ContactEntry targetContact = null;
        for (ContactEntry contact : entries) {
          String title = contact.getTitle().getText();
          if (title.startsWith("GWT-Contacts-Client")) {
            targetContact = contact;
            break;
          }
        }
        if (targetContact == null) {
          showStatus("No contacts were found that were modified today and " +
              "contained 'GWT-Contacts-Client' in the title.", false);
        } else {
          updateContact(targetContact);
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
   * Update a contact by making use of the updateEntry
   * method of the Entry class.
   * Set the contact's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Contacts-Client' so that
   * we can identify which contacts were updated by this demo.
   * We also update the contact's phone number.
   * On success and failure, display a status message.
   * 
   * @param targetContact The contact entry which to update
   */
  private void updateContact(ContactEntry targetContact) {
    targetContact.setTitle(Text.newInstance());
    targetContact.getTitle().setText("GWT-Contacts-Client - updated contact");
    PhoneNumber phoneNumber = PhoneNumber.newInstance();
    phoneNumber.setValue("123-456-7890");
    phoneNumber.setRel(PhoneNumber.REL_WORK);
    targetContact.setPhoneNumbers(new PhoneNumber[] { phoneNumber });
    showStatus("Updating a contact event...", false);
    targetContact.updateEntry(new PersonEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while updating a contact: " +
            caught.getMessage(), true);
      }
      public void onSuccess(PersonEntry result) {
        showStatus("Updated a contact.", false);
      }
    });
  }
  
  /**
   * Starts this demo.
   */
  private void startDemo() {
    service = ContactsService.newInstance(
        "HelloGData_Contacts_UpdateContactDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Update a contact");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          queryContacts(
              "http://www.google.com/m8/feeds/contacts/default/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Contacts.", true);
    }
  }
}