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
import com.google.gwt.gdata.client.blogger.BlogCommentFeed;
import com.google.gwt.gdata.client.blogger.BlogCommentFeedCallback;
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
 * The following example demonstrates how to delete a blog post comment.
 */
public class BloggerDeleteBlogPostCommentDemo extends GDataDemo {

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
        return new BloggerDeleteBlogPostCommentDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample deletes a comment from a blog post.</p>";
      }

      @Override
      public String getName() {
        return "Blogger - Deleting a comment";
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
  public BloggerDeleteBlogPostCommentDemo() {
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
   * Delete a comment entry using the Blogger service and
   * the comment entry uri.
   * On success and failure, display a status message.
   * 
   * @param commentEntryUri The uri of the comment entry to delete
   */
  private void deleteComment(String commentEntryUri) {
    showStatus("Deleting comment entry...", false);
    service.deleteEntry(commentEntryUri, new CommentEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while deleting a Blogger blog " +
            "comment: " + caught.getMessage(), true);
      }
      public void onSuccess(CommentEntry result) {
        showStatus("Deleted a comment.", false);
      }
    });
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
   * Retrieve the Blogger comments feed using the Blogger service and
   * the comments feed uri for a given post.
   * On success, identify the first comment entry with a title starting
   * with "GWT-Blogger-Client", this will be the comment that will be deleted.
   * If no comment is found, display a message.
   * Otherwise call deleteComment to delete the comment. Alternatively
   * we could also have used targetComment.deleteEntry to
   * delete the comment, but the effect is the same.
   * 
   * @param commentsFeedUri The comments feed uri for a given post
   */
  private void getComments(String commentsFeedUri) {
    showStatus("Loading Blogger post comments feed...", false);
    service.getBlogCommentFeed(commentsFeedUri, new BlogCommentFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the Blogger Comments " +
            "feed: " + caught.getMessage(), true);
      }
      public void onSuccess(BlogCommentFeed result) {
        if (result.getEntries().length == 0) {
          showStatus("The target blog post has no comments.", false);
        } else {
          // get the first comment that matches
          CommentEntry targetComment = null;
          for (CommentEntry comment : result.getEntries()) {
            String title = comment.getTitle().getText();
            if (title.startsWith("GWT-Blogger-Client")) {
              targetComment = comment;
              break;
            }
          }
          if (targetComment == null) {
            showStatus("Did not find a comment entry whose title starts " +
                "with the prefix 'GWT-Blogger-Client'.", false);
          } else {
            deleteComment(targetComment.getSelfLink().getHref());
          }
        }
      }
    });
  }

  /**
   * Retrieve the Blogger posts feed using the Blogger service and
   * the posts feed uri for a given blog.
   * On success, identify the first post entry that is available
   * for commenting, this will be the post entry for which a comment
   * will be deleted.
   * If no posts are available for commenting, display a message.
   * Otherwise call getComments to retrieve the comments feed
   * for the target post.
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
          String commentsFeedUri = targetPost.getRepliesLink().getHref();
          getComments(commentsFeedUri);
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
    service = BloggerService.newInstance(
        "HelloGData_Blogger_DeleteBlogPostCommentDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Delete a blog comment");
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