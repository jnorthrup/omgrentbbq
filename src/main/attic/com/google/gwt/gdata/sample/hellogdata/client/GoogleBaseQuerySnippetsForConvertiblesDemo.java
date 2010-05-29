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

import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.gbase.GoogleBaseService;
import com.google.gwt.gdata.client.gbase.SnippetsEntry;
import com.google.gwt.gdata.client.gbase.SnippetsFeed;
import com.google.gwt.gdata.client.gbase.SnippetsFeedCallback;
import com.google.gwt.gdata.client.gbase.SnippetsQuery;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to query snippets to find
 * convertibles matching a set of parameters.
 */
public class GoogleBaseQuerySnippetsForConvertiblesDemo extends GDataDemo {

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
        return new GoogleBaseQuerySnippetsForConvertiblesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample uses a snippets query to find an items " +
            "corresponding to convertibles matching a set of criteria.</p>";
      }

      @Override
      public String getName() {
        return "Base - Querying snippets for convertibles";
      }
    };
  }

  private GoogleBaseService service;
  private FlexTable mainPanel;

  /**
   * Setup the Google Base service and create the main content panel.
   * Start the demo by querying Google Base snippets.
   */
  public GoogleBaseQuerySnippetsForConvertiblesDemo() {
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
   * Retrieves a snippets feed using a Query object.
   * In GData, feed URIs can contain query string parameters. The
   * GData query objects aid in building parameterized feed URIs.
   * Upon successfully receiving the snippets feed, the snippet entries 
   * are displayed to the user via the showData method.
   * We set the BQ parameter to search for convertibles.
   * 
   * @param itemsFeedUri The items feed uri.
   */
  private void querySnippets(String itemsFeedUri) {
    showStatus("Loading snippets feed...", false);
    SnippetsQuery query = SnippetsQuery.newInstance(itemsFeedUri);
    query.setMaxResults(25);
    query.setBq("convertible [brand:Volkswagen][year >=2003]" +
        "[color:red][vehicle type:car]");
    service.getSnippetsFeed(query, new SnippetsFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the snippets feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(SnippetsFeed result) {
        SnippetsEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No snippets matched the search criteria.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Google Base snippet entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Name, Url and Value are displayed.
  * 
  * @param entries The Google Base snippet entries to display.
  */
  private void showData(SnippetsEntry[] snippets) {
    mainPanel.clear();
    String[] labels = new String[] { "Name", "Url", "Value" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < snippets.length; i++) {
      SnippetsEntry snippet = snippets[i];
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(snippet.getTitle().getText()));
      mainPanel.addCell(row);
      if (snippet.getHtmlLink() == null) {
        mainPanel.setWidget(row, 1, new Label("Not available"));
      } else {
        String link = snippet.getHtmlLink().getHref();
        mainPanel.setWidget(row, 1,
            new HTML("<a href=\"" + link + "\" target=\"_blank\">" +
                link + "</a>"));
      }
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2,
          new Label(snippet.getPublished().getValue().getDate().toString()));
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
        "HelloGData_GoogleBase_QuerySnippetsForConvertiblesDemo_v2.0");
    querySnippets("http://www.google.com/base/feeds/snippets/");
  }
}