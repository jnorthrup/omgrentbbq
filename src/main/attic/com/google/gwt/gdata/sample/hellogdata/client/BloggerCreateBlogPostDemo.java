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
import com.google.gwt.gdata.client.app.Control;
import com.google.gwt.gdata.client.app.Draft;
import com.google.gwt.gdata.client.atom.Category;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.blogger.BlogEntry;
import com.google.gwt.gdata.client.blogger.BlogFeed;
import com.google.gwt.gdata.client.blogger.BlogFeedCallback;
import com.google.gwt.gdata.client.blogger.BlogPostFeed;
import com.google.gwt.gdata.client.blogger.BlogPostFeedCallback;
import com.google.gwt.gdata.client.blogger.BloggerService;
import com.google.gwt.gdata.client.blogger.PostEntry;
import com.google.gwt.gdata.client.blogger.PostEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a blog post.
 */
public class BloggerCreateBlogPostDemo extends GDataDemo {

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
        return new BloggerCreateBlogPostDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample creates a new blog post in the user's most " +
            "recently updated blog. The post's title will be " +
            "'GWT-Blogger-Client: inserted post'.</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Creating a new blog post";
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
  public BloggerCreateBlogPostDemo() {
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
   * Retrieve the Blogger posts feed using the Blogger service and
   * the posts feed uri for a given blog.
   * On success, call insertPost to insert a new post entry
   * into the retrieved posts feed.
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
        insertPost(result);
      }
    });
  }
  
  /**
   * Create a blog post by inserting a post entry into
   * a blog posts feed.
   * Set the post's title and contents to an arbitrary string. Here
   * we prefix the title with 'GWT-Blogger-Client' so that
   * we can identify which posts were created by this demo.
   * To avoid publishing the new post we set the Atom control status
   * to "draft".
   * Finally, we associate the new post with two categories.
   * On success and failure, display a status message.
   * 
   * @param postFeed The post feed into which to insert the new post
   */
  private void insertPost(BlogPostFeed postFeed) {
    showStatus("Creating blog post entry...", false);
    PostEntry newPost = PostEntry.newInstance();
    newPost.setTitle(Text.newInstance());
    newPost.getTitle().setText("GWT-Blogger-Client - inserted post");
    newPost.setContent(Text.newInstance());
    newPost.getContent().setText("This is the body of the blog post. We " +
        "can include <b>HTML</b> tags.");
    newPost.setControl(Control.newInstance());
    newPost.getControl().setDraft(Draft.newInstance());
    newPost.getControl().getDraft().setValue(Draft.VALUE_YES);
    Category cat1 = Category.newInstance();
    cat1.setScheme("http://www.blogger.com/atom/ns#");
    cat1.setTerm("Label1");
    Category cat2 = Category.newInstance();
    cat2.setLabel("http://www.blogger.com/atom/ns#");
    cat2.setTerm("Label2");
    newPost.setCategories(new Category[] { cat1, cat2 });
    postFeed.insertEntry(newPost, new PostEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a blog post: " + 
            caught.getMessage(), true);
      }
      public void onSuccess(PostEntry result) {
        showStatus("Created a blog entry titled '" + 
            result.getTitle().getText() + "'.", false);
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
    service = BloggerService.newInstance(
        "HelloGData_Blogger_CreateBlogPostDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a blog post");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          getBlogs("http://www.blogger.com/feeds/default/blogs");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Blogger.", true);
    }
  }
}