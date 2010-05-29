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
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.client.finance.FinanceService;
import com.google.gwt.gdata.client.finance.PortfolioEntry;
import com.google.gwt.gdata.client.finance.PortfolioFeed;
import com.google.gwt.gdata.client.finance.PortfolioFeedCallback;
import com.google.gwt.gdata.client.finance.PositionData;
import com.google.gwt.gdata.client.finance.PositionEntry;
import com.google.gwt.gdata.client.finance.PositionFeed;
import com.google.gwt.gdata.client.finance.PositionFeedCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of positions.
 */
public class FinanceRetrievePositionsDemo extends GDataDemo {

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
        return new FinanceRetrievePositionsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>As well as portfolio and transaction feeds, the Google " +
            "Finance Portfolio Data API offers an intermediate, read-only " +
            "feed know as the position feed. This is a summary of all of " +
            "the contents of a particular portfolio, grouped by ticker " +
            "symbol. Each entry in this feed, know as a position, is " +
            "derived from the transactions entered for that symbol. Each " +
            "position entry has position data that may include current " +
            "number of shares of a particular stock or mutual fund if " +
            "specified in the transactions as well as investment gain if " +
            "requested and if purchase price and date have been " +
            "specified.</p>";
      }

      @Override
      public String getName() {
        return "Finance - Retrieving positions";
      }
    };
  }

  private FinanceService service;
  private FlexTable mainPanel;
  private final String scope = "http://finance.google.com/finance/feeds/";

  /**
   * Setup the Finance service and create the main content panel.
   * If the user is not logged on to Finance display a message,
   * otherwise start the demo by retrieving the user's portfolios.
   */
  public FinanceRetrievePositionsDemo() {
    mainPanel = new FlexTable();
    initWidget(mainPanel);
    if (!GData.isLoaded(GDataSystemPackage.FINANCE)) {
      showStatus("Loading the GData Finance package...", false);
      GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
        public void run() {
          startDemo();
        }
      }, GDataSystemPackage.FINANCE);
    } else {
      startDemo();
    }
  }

  /**
   * Retrieve the portfolios feed using the Finance service and
   * the portfolios feed uri. In GData all get, insert, update
   * and delete methods always receive a callback defining
   * success and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first portfolio entry and its
   * ID and calls getPositions to retrieve the corresponding
   * positions feed.
   * 
   * @param portfoliosFeedUri The uri of the portfolios feed
   */
  private void getPortfolios(String portfoliosFeedUri) {
    showStatus("Loading portfolios feed...", false);
    service.getPortfolioFeed(portfoliosFeedUri, new PortfolioFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the portfolios feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(PortfolioFeed result) {
        PortfolioEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("You have no portfolios.", false);
        } else {
          PortfolioEntry targetPortfolio = entries[0];
          String portfolioId = targetPortfolio.getId().getValue();
          JsArrayString match = regExpMatch("\\/(\\d+)$", portfolioId);
          if (match.length() > 1) {
            portfolioId = match.get(1);
          } else {
            showStatus("Error parsing the portfolio id.", true);
          }
          getPositions(portfolioId);
        }
      }
    });
  }

  /**
   * Retrieve the positions feed for a given portfolio using the
   * Finance service and the positions feed uri.
   * The failure handler displays an error message while the
   * success handler calls showData to display the position entries.
   * 
   * @param portfolioId The id of the portfolio for which to
   * retrieve position data
   */
  private void getPositions(String portfolioId) {
    showStatus("Loading positions feed...", false);
    String positionsFeedUri = 
      "http://finance.google.com/finance/feeds/default/" +
      "portfolios/" + portfolioId + "/positions";
    service.getPositionFeed(positionsFeedUri, new PositionFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the positions feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(PositionFeed result) {
        PositionEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No transactions found.", false);
        } else {
          showData(entries);
        }
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
  * Displays a set of Finance position entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields
  * Title and Shares are displayed.
  * 
  * @param entries The Finance position entries to display.
  */
  private void showData(PositionEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Title", "Shares" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      PositionEntry entry = entries[i];
      PositionData data = entry.getPositionData();
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(entry.getTitle().getText()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label("" + data.getShares()));
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
    service = FinanceService.newInstance(
        "HelloGData_Finance_RetrievePositionsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getPortfolios(
          "http://finance.google.com/finance/feeds/default/portfolios");
    } else {
      showStatus("You are not logged on to Google Finance.", true);
    }
  }
}