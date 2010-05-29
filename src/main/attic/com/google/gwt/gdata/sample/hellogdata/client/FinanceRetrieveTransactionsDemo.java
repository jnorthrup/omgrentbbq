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
import com.google.gwt.gdata.client.finance.FinanceService;
import com.google.gwt.gdata.client.finance.PortfolioEntry;
import com.google.gwt.gdata.client.finance.PortfolioFeed;
import com.google.gwt.gdata.client.finance.PortfolioFeedCallback;
import com.google.gwt.gdata.client.finance.TransactionData;
import com.google.gwt.gdata.client.finance.TransactionEntry;
import com.google.gwt.gdata.client.finance.TransactionFeed;
import com.google.gwt.gdata.client.finance.TransactionFeedCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to retrieve a list of transactions.
 */
public class FinanceRetrieveTransactionsDemo extends GDataDemo {

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
        return new FinanceRetrieveTransactionsDemo();
      }

      @Override
      public String getDescription() {
        return "<p>In this sample, all transactions for a particular " +
            "ticker (e.g. NYSE:IBM) are retrieved for a portfolio. The " +
            "user's portfolio feed is queried and searched for a portfolio " +
            "starting with 'GWT-Finance-Client'. The transaction feed for " +
            "NASDAQ:GOOG is then queried for this portfolio and basic " +
            "information is displayed. (This portfolio and transaction " +
            "were created in the examples above -- if no transactions for " +
            "a given ticker exist, the transaction feed will not exist and " +
            "an error will be generated).</p>";
      }

      @Override
      public String getName() {
        return "Finance - Retrieving transactions";
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
  public FinanceRetrieveTransactionsDemo() {
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
   * success handler obtains the first portfolio entry with a title
   * starting with "GWT-Finance-Client" and retrieves all transactions
   * with the ticker symbol "NASDAQ:GOOG".
   * If no transactions exist with this symbol, the request will fail.
   * If no portfolio is found, a message is displayed.
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
        PortfolioEntry targetPortfolio = null;
        for (PortfolioEntry entry : entries) {
          if (entry.getTitle().getText().startsWith("GWT-Finance-Client")) {
            targetPortfolio = entry;
            break;
          }
        }
        if (targetPortfolio == null) {
          showStatus("No portfolio found that contains 'GWT-Finance-Client' " +
              "in the title.", false);
        } else {
          String ticker = "NASDAQ:GOOG";
          String transactionFeedUri = targetPortfolio.getEditLink().getHref() +
              "/positions/" + ticker + "/transactions";
          getTransactions(transactionFeedUri);
        }
      }
    });
  }

  /**
   * Retrieve the transactions feed for a given portfolio using the
   * Finance service and the transactions feed uri.
   * The failure handler displays an error message while the
   * success handler calls showData to display the transaction entries.
   * 
   * @param transactionFeedUri The transaction feed uri
   */
  private void getTransactions(String transactionFeedUri) {
    showStatus("Loading transactions feed...", false);
    service.getTransactionFeed(transactionFeedUri,
        new TransactionFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the portfolios " +
            "feed: " + caught.getMessage(), true);
      }
      public void onSuccess(TransactionFeed result) {
        TransactionEntry[] entries = result.getEntries();
        if (entries.length == 0) {
          showStatus("No transactions found.", false);
        } else {
          showData(entries);
        }
      }
    });
  }

  /**
  * Displays a set of Finance transaction entries in a tabular 
  * fashion with the help of a GWT FlexTable widget. The data fields 
  * Notes, Type and Shares are displayed.
  * 
  * @param entries The Finance transaction entries to display.
  */
  private void showData(TransactionEntry[] entries) {
    mainPanel.clear();
    String[] labels = new String[] { "Notes", "Type", "Shares" };
    mainPanel.insertRow(0);
    for (int i = 0; i < labels.length; i++) {
      mainPanel.addCell(0);
      mainPanel.setWidget(0, i, new Label(labels[i]));
      mainPanel.getFlexCellFormatter().setStyleName(0, i, "hm-tableheader");
    }
    for (int i = 0; i < entries.length; i++) {
      TransactionEntry entry = entries[i];
      TransactionData data = entry.getTransactionData();
      int row = mainPanel.insertRow(i + 1);
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 0, new Label(data.getNotes()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 1, new Label(data.getType()));
      mainPanel.addCell(row);
      mainPanel.setWidget(row, 2, new Label("" + data.getShares()));
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
        "HelloGData_Finance_RetrieveTransactionsDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      getPortfolios(
          "http://finance.google.com/finance/feeds/default/portfolios");
    } else {
      showStatus("You are not logged on to Google Finance.", true);
    }
  }
}