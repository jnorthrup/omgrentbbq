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
import com.google.gwt.gdata.client.Email;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.contacts.ContactEntry;
import com.google.gwt.gdata.client.contacts.ContactEntryCallback;
import com.google.gwt.gdata.client.contacts.ContactsService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a contact.
 */
public class ContactsCreateContactDemo extends GDataDemo {

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
        return new ContactsCreateContactDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create a new " +
            "contact entry.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Creating contacts";
      }
    };
  }

  private ContactsService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/m8/feeds/";

  /**
   * Setup the Contacts service and create the main content panel.
   * If the user is not logged on to Contacts display a message,
   * otherwise start the demo by creating a contact.
   */
  public ContactsCreateContactDemo() {
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
   * Create a contact by inserting a contact entry into
   * a contacts feed.
   * Set the contact's title and contents to an arbitrary string. Here
   * we prefix the title with 'GWT-Contacts-Client' so that
   * we can identify which contacts were created by this demo.
   * On success and failure, display a status message.
   * 
   * @param contactFeedUri The uri of the contact feed into which to
   * insert the new contact entry
   */
  private void createContact(String contactFeedUri) {
    showStatus("Creating contact...", false);
    ContactEntry entry = ContactEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-Contacts-Client - Create Contact");
    entry.setContent(Text.newInstance());
    entry.getContent().setText("content info here");
    Email email = Email.newInstance();
    email.setAddress("GWT-Contacts-Client@domain.com");
    email.setPrimary(true);
    email.setRel(Email.REL_HOME);
    entry.setEmailAddresses(new Email[] { email });
    service.insertEntry(contactFeedUri, entry,
        new ContactEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a contact: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactEntry result) {
        showStatus("Created a contact.", false);
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
        "HelloGData_Contacts_CreateContactDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a contact");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createContact(
              "http://www.google.com/m8/feeds/contacts/default/full");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Contacts.", true);
    }
  }
}