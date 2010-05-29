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
import com.google.gwt.gdata.client.blogger.BlogEntry;
import com.google.gwt.gdata.client.blogger.BlogFeed;
import com.google.gwt.gdata.client.blogger.BlogFeedCallback;
import com.google.gwt.gdata.client.blogger.BloggerService;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of a user's blogs.
 */
public class BloggerRetrieveBlogsDemo extends GDataDemo {

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
        return new BloggerRetrieveBlogsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the \"metafeed\" feed to retrieve the " +
            "authenticated user's list of blogs. Each blog is listed as a " +
            "link to the actual blog.</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Retrieving a list of blogs";
      }
    };
  }

  private BloggerService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.blogger.com/feeds/";

  /**
   * Setup the Blogger service and create the main content panel.
   * If the user is not logged on to Blogger display a message,
   * otherwise start the demo by retrieving the user's blogs.
   */
  public BloggerRetrieveBlogsDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.BLOGGER)) {
      showStatus("Loading the GData Blogger package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.BLOGGER);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieve the Blogger blogs feed using the Blogger service and
   * the blogs feed uri. In GData all get, insert, update and delete methods
   * always receive a callback defining success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the blog entries.
   * 
   * @param blogsFeedUri The uri of the blogs feed
   */
  private void getBlogs(String blogsFeedUri) {
    showStatus("Loading blog feed...", false);
    service.getBlogFeed(blogsFeedUri, new BlogFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Blogger Blog " +
            "feed: " + caught.getMessage(), true);
      }
      public void onSuccess(BlogFeed result) {
        BlogEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no Blogger blogs.", false);
        } else {
          showData(result.getEntries());
        }
      }
    });
  }

  /**
  * Displays a set of Blogger blog entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Title, URL 
  * and Updated are displayed.
  * 
  * @param entries The Blogger blog entries to display.
  */
  private void showData(BlogEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "URL", "Updated" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      BlogEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      String link = entry.getHtmlLink().getHref();
      mainPanel.setWidget(row, 1, new HTML("<a href=\"" + link + 
          "\" target=\"_blank\">" + link +  "</a>"));
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
    service = BloggerService.newInstance(
        "HelloGData_Blogger_RetrieveBlogsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getBlogs("http://www.blogger.com/feeds/default/blogs");
    } else {
      showStatus("You are not logged on to Blogger.", true);
    }
  }
  
}