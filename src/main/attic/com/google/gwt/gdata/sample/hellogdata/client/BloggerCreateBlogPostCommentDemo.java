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
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.blogger.BlogEntry;
import com.google.gwt.gdata.client.blogger.BlogFeed;
import com.google.gwt.gdata.client.blogger.BlogFeedCallback;
import com.google.gwt.gdata.client.blogger.BlogPostFeed;
import com.google.gwt.gdata.client.blogger.BlogPostFeedCallback;
import com.google.gwt.gdata.client.blogger.BloggerService;
import com.google.gwt.gdata.client.blogger.CommentEntry;
import com.google.gwt.gdata.client.blogger.CommentEntryCallback;
import com.google.gwt.gdata.client.blogger.PostEntry;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a blog post comment.
 */
public class BloggerCreateBlogPostCommentDemo extends GDataDemo {

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
        return new BloggerCreateBlogPostCommentDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample adds a new comment to a blog post. The " +
            "comment's contents contain the text 'GWT-Blogger-Client'.</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Creating a comment";
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
  public BloggerCreateBlogPostCommentDemo() {
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
   * On success, identify the first post entry that is available
   * for commenting, this will be the post entry that we'll
   * reply to.
   * If no posts are available for commenting, display a message.
   * Otherwise extract the blog and post ID and call insertComment
   * to add a comment.
   * If the regular expression parsing of the blog and post IDs
   * fails, display a message.
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
      PostEntry targetPost = null;
      // get the first public post
      for (PostEntry post : result.getEntries()) {
        if (post.getRepliesLink() != null) {
          targetPost = post;
          break;
        }
      }
      if (targetPost == null) {
        showStatus("The target blog contains no public posts.", false);
      } else {
        String postEntryId = targetPost.getId().getValue();
        JsArrayString match = regExpMatch("blog-(\\d+)\\.post-(\\d+)", 
            postEntryId);
        if (match.length() > 1) {
          insertComment(match.get(1), match.get(2));
        } else {
          showStatus("Error parsing the blog post id.", true);
        }
      }
    }
    });
  }
  
  /**
   * Create a blog comment by inserting a comment entry into
   * a blog comments feed.
   * Set the comment's contents to an arbitrary string. Here
   * we prefix the contents with 'GWT-Blogger-Client' so that
   * we can identify which comments were created by this demo.
   * On success and failure, display a status message.
   * 
   * @param blogId The ID of the blog containing the target post
   * @param postId The ID of the post to which to reply
   */
  private void insertComment(String blogId, String postId) {
    showStatus("Creating blog comment entry...", false);
    CommentEntry comment = CommentEntry.newInstance();
    comment.setContent(Text.newInstance());
    comment.getContent().setText("GWT-Blogger-Client - Great post!");
    String commentsFeedUri = "http://www.blogger.com/feeds/" + blogId + "/" +
        postId + "/comments/default";
    service.insertEntry(commentsFeedUri, comment,
        new CommentEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating the Blogger post " +
            "comment: " + caught.getMessage(), true);
      }
      public void onSuccess(CommentEntry result) {
        showStatus("Created a comment.", false);
      }
    });
  }
  
  /**
   * Expose the JavaScript regular expression parsing to GWT.
   * 
   * @param regEx The regular expression to use
   * @param target The text string to parse
   * @return A JavaScript string array containing any matches
   */
  private native JsArrayString regExpMatch(String regEx, String target) /*-{
    var re = new RegExp();
    return re.compile(regEx).exec(target);
  }-*/;

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
        "HelloGData_Blogger_CreateBlogPostCommentDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a blog comment");
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