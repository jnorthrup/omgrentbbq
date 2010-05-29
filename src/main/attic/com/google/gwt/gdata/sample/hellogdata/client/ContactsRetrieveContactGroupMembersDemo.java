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
import com.google.gwt.gdata.client.contacts.ContactEntry;
import com.google.gwt.gdata.client.contacts.ContactFeed;
import com.google.gwt.gdata.client.contacts.ContactFeedCallback;
import com.google.gwt.gdata.client.contacts.ContactGroupEntry;
import com.google.gwt.gdata.client.contacts.ContactGroupFeed;
import com.google.gwt.gdata.client.contacts.ContactGroupFeedCallback;
import com.google.gwt.gdata.client.contacts.ContactQuery;
import com.google.gwt.gdata.client.contacts.ContactsService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of 
 * contact group members.
 */
public class ContactsRetrieveContactGroupMembersDemo extends GDataDemo {

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
        return new ContactsRetrieveContactGroupMembersDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to retrieve all members of " +
            "a particular group. It first obtains the ID for a given " +
            "contact group and uses it as the query parameter to retrieve " +
            "all members of that group.</p>";
      }

      @Override
      public String getName() {
        return "Contacts - Retrieving all group members";
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
  public ContactsRetrieveContactGroupMembersDemo() {
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
   * Retrieves a contacts feed for a group using a Query object.
   * In GData, feed URIs can contain query string parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * Upon successfully receiving the contacts feed, the contact
   * entries are displayed to the user via the showData method.
   * A string parameter is used to indicate the ID of the group
   * to query.
   * 
   * @param contactGroupId The id of the group for which to retrieve
   * the contacts
   */
  private void queryContacts(String contactGroupId) {
    showStatus("Loading contacts feed...", false);
    ContactQuery query = ContactQuery.newInstance(
        "http://www.google.com/m8/feeds/contacts/default/full");
    query.setStringParam("group", contactGroupId);
    service.getContactFeed(query, new ContactFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Contacts feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ContactFeed result) {
        ContactEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("The contact group has no member contacts.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
   * Retrieve the contact groups feed using the Contacts service and
   * the contacts feed uri. In GData all get, insert, update and delete methods
   * always receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first group entry and
   * calls queryContacts to retrieve the contacts feed for that group.
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
        if (entries.length == 0) {
          showStatus("You have no contact groups.", false);
        } else {
          ContactGroupEntry targetGroup = entries[0];
          String groupId = targetGroup.getId().getValue();
          queryContacts(groupId);
        }
      }
    });
  }

  /**
  * Displays a set of Google Contacts contact entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Title, Email and Updated are displayed.
  * 
  * @param entries The Google Contacts contact entries to display.
  */
  private void showData(ContactEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "Email", "Updated" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      ContactEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      String email = "";
      if (entry.getEmailAddresses().length > 0) {
        email = entry.getEmailAddresses()[0].getAddress();
      }
      mainPanel.setWidget(row, 1, new Label(email));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2,
          new Label(entry.getUpdated().getValue().getDate().toString()));
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
    service = ContactsService.newInstance(
        "HelloGData_Contacts_RetrieveContactGroupMembersDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getContactGroups("http://www.google.com/m8/feeds/groups/default/full");
    } else {
      showStatus("You are not logged on to Google Contacts.", true);
    }
  }
}