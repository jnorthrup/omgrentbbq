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
import com.google.gwt.gdata.client.FeedLink;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.gbase.ItemsEntry;
import com.google.gwt.gdata.client.gbase.ItemsFeed;
import com.google.gwt.gdata.client.gbase.ItemsFeedCallback;
import com.google.gwt.gdata.client.gbase.MediaEntry;
import com.google.gwt.gdata.client.gbase.MediaFeed;
import com.google.gwt.gdata.client.gbase.MediaFeedCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.mediarss.MediaContent;
import com.google.gwt.gdata.client.mediarss.MediaThumbnail;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of
 * media attachments.
 */
public class GoogleBaseRetrieveMediaDemo extends GDataDemo {

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
        return new GoogleBaseRetrieveMediaDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses the private, read/write media feed " +
            "to query an item's attached media. Each item has an associated " +
            "media feed which can contain up to 10 binary attachments. Use " +
            "this feed to manage binary attachments for your Google Base " +
            "items. Google Base creates a set of thumbnails for each " +
            "attachment, and stores the thumbnails on the Google Base " +
            "server.</p>";
      }

      @Override
      public String getName() {
        return "Base - Retrieving media";
      }
    };
  }

  private GoogleBaseService service;
  private FlexTable mainPanel;
  private final String scope = "http://www.google.com/base/feeds/";

  /**
   * Setup the Google Base service and create the main content panel.
   * If the user is not logged on to Google Base display a message,
   * otherwise start the demo by retrieving the user's items.
   */
  public GoogleBaseRetrieveMediaDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.GBASE)) {
      showStatus("Loading the GData Google-Base package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.GBASE);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieve the items feed using the Google Base service and
   * the items feed uri. In GData all get, insert, update
   * and delete methods always receive a callback defining success
   * and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first item entry that is associated
   * with on or more media attachments and calls getMedia to
   * retrieve its media entries.
   * 
   * @param itemsFeedUri The uri of the items feed
   */
  private void getItems(String itemsFeedUri) {
    showStatus("Loading items feed...", false);
    service.getItemsFeed(itemsFeedUri, new ItemsFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the items feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(ItemsFeed result) {
        ItemsEntry[] entries = result.getEntries();
        ItemsEntry targetItem = null;
        for (ItemsEntry entry : entries) {
          FeedLink<MediaFeed> link = entry.getFeedLink().cast();
          if (link.getCountHint() > 0) {
            targetItem = entry;
            break;
          }
        }
        if (targetItem == null) {
          showStatus("You have no items containing media.", false);
        } else {
          String itemsEntryUri = targetItem.getSelfLink().getHref();
          getMedia(itemsEntryUri);
        }
      }
    });
  }

  /**
   * Retrieve the media feed using the Google Base service and
   * the media feed uri for a given item. In GData all get, insert, update
   * and delete methods always receive a callback defining success
   * and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler calls showData to display the media entries.
   * 
   * @param itemsEntryUri The uri of the items entry for which to
   * retrieve the media feed
   */
  private void getMedia(String itemsEntryUri) {
    showStatus("Loading media feed...", false);
    String mediaFeedUri = itemsEntryUri + "/media/";
    service.getMediaFeed(mediaFeedUri, new MediaFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the media feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(MediaFeed result) {
        MediaEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No media found.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Google Base media entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields
  * Title and Type, along with a thumbnail preview (if available),
  * are displayed.
  * 
  * @param entries The Google Base media entries to display.
  */
  private void showData(MediaEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "Type", "Thumbnail" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      MediaEntry entry = entries[i];
      MediaContent media = entry.getMediaContent();
      String fileType = media.getType();
      String fileUrl = media.getUrl();
      MediaThumbnail[] thumbs = media.getThumbnails();
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0,
          new HTML("<a href=\"" + fileUrl + "\" target=\"_blank\">" +
              entry.getTitle().getText() + "</a>"));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label(fileType));
      mainPanel.addCell(row);
      if (thumbs.length > 0) {
        MediaThumbnail thumb = thumbs[0];
        String thumbSource = thumb.getUrl();
        double thumbWidth = thumb.getWidth();
        double thumbHeight = thumb.getHeight();
        mainPanel.setWidget(row, 2,
            new HTML("<img src=\"" + thumbSource + " width=\"" +
                thumbWidth + "px\" height=\"" +
                thumbHeight + "px\" border=\"0px\" />"));
      } else {
        mainPanel.setWidget(row, 2, new Label("Not Available"));
      }
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
    service = GoogleBaseService.newInstance(
        "HelloGData_GoogleBase_RetrieveMediaDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getItems("http://www.google.com/base/feeds/items");
    } else {
      showStatus("You are not logged on to Google Base.", true);
    }
  }
}