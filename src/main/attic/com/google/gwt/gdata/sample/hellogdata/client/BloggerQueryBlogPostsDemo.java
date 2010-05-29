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
import com.google.gwt.gdata.client.DateTime;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.blogger.BlogEntry;
import com.google.gwt.gdata.client.blogger.BlogFeed;
import com.google.gwt.gdata.client.blogger.BlogFeedCallback;
import com.google.gwt.gdata.client.blogger.BlogPostFeed;
import com.google.gwt.gdata.client.blogger.BlogPostFeedCallback;
import com.google.gwt.gdata.client.blogger.BlogPostQuery;
import com.google.gwt.gdata.client.blogger.BloggerService;
import com.google.gwt.gdata.client.blogger.PostEntry;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import java.util.Date;

/**
 * The following example demonstrates how to query for blog posts.
 */
public class BloggerQueryBlogPostsDemo extends GDataDemo {

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
        return new BloggerQueryBlogPostsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample queries for blog posts that were " +
            "published within the last month.</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Querying for blog posts";
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
  public BloggerQueryBlogPostsDemo() {
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
   * success handler picks up the first Blog entry and
   * calls queryPosts to retrieve a posts feed for that blog.
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
          queryPosts(postsFeedUri);
        }
      }
    });
  }

  /**
   * Retrieves a posts feed for a blog using a Query object.
   * In GData, feed URIs can contain querystring parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * Upon successfully receiving the posts feed, the post entries 
   * are displayed to the user via the showData method.
   * The PublishedMin and PublishedMax parameters are used to
   * limit the range of posts to a given publishing period.
   * 
   * @param postsFeedUri The posts feed uri for a given blog.
   */
  @SuppressWarnings("deprecation")
  private void queryPosts(String postsFeedUri) {
    final BlogPostQuery query = BlogPostQuery.newInstance(postsFeedUri);
    Date minDate = new Date();
    minDate.setMonth(minDate.getMonth() - 1);
    query.setPublishedMin(DateTime.newInstance(minDate));
    query.setPublishedMax(DateTime.newInstance(new Date()));
    showStatus("Querying Blogger for posts...", false);
    service.getBlogPostFeed(query, new BlogPostFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while querying Blogger for Posts: " + 
            caught.getMessage(), true);
      }
      public void onSuccess(BlogPostFeed result) {
        showData(result.getEntries());
      }
    });
  }

  /**
  * Displays a set of Blogger post entries in a tabular fashion with
  * the help of a GWT FlexTable widget. The data fields Title, URL 
  * and Published are displayed.
  * 
  * @param entries The Blogger post entries to display.
  */
  private void showData(PostEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "URL", "Published" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      PostEntry entry = entries[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      if (entry.getHtmlLink() == null) {
        mainPanel.setWidget(row, 1, new Label("Not available"));
      } else {
        String link = entry.getHtmlLink().getHref();
        mainPanel.setWidget(row, 1, new HTML("<a href=\"" + link + 
            "\" target=\"_blank\">" + link +  "</a>"));
      }
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2,
          new Label(entry.getPublished().getValue().getDate().toString()));
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
        "HelloGData_Blogger_QueryBlogPostsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getBlogs("http://www.blogger.com/feeds/default/blogs");
    } else {
      showStatus("You are not logged on to Blogger.", true);
    }
  }
}