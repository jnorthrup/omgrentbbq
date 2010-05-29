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
import com.google.gwt.gdata.client.blogger.BlogPostFeed;
import com.google.gwt.gdata.client.blogger.BlogPostFeedCallback;
import com.google.gwt.gdata.client.blogger.BloggerService;
import com.google.gwt.gdata.client.blogger.PostEntry;
import com.google.gwt.gdata.client.blogger.PostEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a specific blog
 * post entry.
 */
public class BloggerRetrieveSpecificBlogPostDemo extends GDataDemo {

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
        return new BloggerRetrieveSpecificBlogPostDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample returns a specific blog post by querying the " +
            "entry's URI with getBlogPostFeed().</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Retrieving a specific blog post";
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
  public BloggerRetrieveSpecificBlogPostDemo() {
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
   * success handler obtains the first Blog entry and
   * calls getPosts to retrieve the posts feed for that blog.
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
          BlogEntry targetBlog = entries[0];
          String postsFeedUri = targetBlog.getEntryPostLink().getHref();
          getPosts(postsFeedUri);
        }
      }
    });
  }
  
  /**
   * Retrieves a post entry by uri. On success, call
   * showData to display the post entry details.
   * 
   * @param postEntryUri The uri of the post entry to retrieve
   */
  private void getPost(String postEntryUri) {
    showStatus("Loading post entry...", false);
    service.getPostEntry(postEntryUri, new PostEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Blogger Post " +
            "entry: " + caught.getMessage(), true);
      }
      public void onSuccess(PostEntry result) {
        showData(result);
      }
    });
  }

  /**
   * Retrieve the Blogger posts feed using the Blogger service and
   * the posts feed uri for a given blog.
   * On success, call getPost on the first post entry. This will 
   * retrieve the same entry that we already have, the purpose
   * is to exemplify retrieving a specific post entry directly.
   * If no posts are available, display a message.
   * 
   * @param postsFeedUri The posts feed uri for a given blog
   */
  private void getPosts(String postsFeedUri) {
    showStatus("Loading posts feed...", false);
    service.getBlogPostFeed(postsFeedUri, new BlogPostFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Blogger Posts " +
            "feed: " + caught.getMessage(), true);
      }
      public void onSuccess(BlogPostFeed result) {
        PostEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No post entries found for the current blog.", false);
        } else {
          PostEntry targetPost = entries[0];
          String postEntryUri = targetPost.getSelfLink().getHref();
          getPost(postEntryUri);
        }
      }
    });
  }

  /**
  * Displays a Blogger post entry in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Title, 
  * Author, Published and Contents are displayed.
  * 
  * @param entry The Blogger post entry to display.
  */
  private void showData(PostEntry entry) {
    mainPanel.clear();
    mainPanel.insertRow(0);
    mainPanel.addCell(0);
    mainPanel.setWidget(0, 0, new HTML("<h2>" + entry.getTitle().getText() + 
        "</h2>"));
    mainPanel.insertRow(1);
    mainPanel.addCell(1);
    mainPanel.setWidget(1, 0, new HTML("<i>By " + 
        entry.getAuthors()[0].getName().getValue() + " on " + 
        entry.getPublished().getValue().getDate().toString() + "</i>"));
    mainPanel.insertRow(2);
    mainPanel.addCell(2);
    mainPanel.setWidget(2, 0, new Label(entry.getContent().getText()));
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
        "HelloGData_Blogger_RetrieveSpecificBlogPostDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getBlogs("http://www.blogger.com/feeds/default/blogs");
    } else {
      showStatus("You are not logged on to Blogger.", true);
    }
  }
}