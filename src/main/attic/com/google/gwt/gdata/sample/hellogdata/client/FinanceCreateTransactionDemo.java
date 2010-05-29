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
import com.google.gwt.gdata.client.finance.FinanceService;
import com.google.gwt.gdata.client.finance.PortfolioEntry;
import com.google.gwt.gdata.client.finance.PortfolioFeed;
import com.google.gwt.gdata.client.finance.PortfolioFeedCallback;
import com.google.gwt.gdata.client.finance.TransactionData;
import com.google.gwt.gdata.client.finance.TransactionEntry;
import com.google.gwt.gdata.client.finance.TransactionEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a transaction.
 */
public class FinanceCreateTransactionDemo extends GDataDemo {

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
        return new FinanceCreateTransactionDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create a transaction." +
            "A new transaction is created in a user's Google Finance " +
            "portfolio by posting a transaction entry to the appropriate " +
            "Post URL. In this example, a transaction entry object is " +
            "created and its properties (transaction type, etc.) are " +
            "set, then the portfolio feed is queried for a list of the " +
            "user's portfolios. The entry is inserted into the portfolio " +
            "whose title starts with 'GWT-Finance-Client'.</p>";
      }

      @Override
      public String getName() {
        return "Finance - Creating a transaction";
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
  public FinanceCreateTransactionDemo() {
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
   * Create a transaction by inserting a transaction entry into
   * a transaction feed.
   * Set the transaction's notes to an arbitrary string. Here
   * we prefix the notes field with 'GWT-Finance-Client' so that
   * we can identify which transactions were created by this demo.
   * The new transaction is created with a set of transaction data
   * specifying the type and shares.
   * The transaction is inserted into the transactions feed for
   * the symbol "NASDAQ:GOOG". If no position exists for this symbol
   * the insert request will fail.
   * On success and failure, display a status message.
   * 
   * @param portfolioEditUri The uri of the portfolio entry into which
   * to insert the transaction entry
   */
  private void createTransaction(String portfolioEditUri) {
    showStatus("Creating transaction...", false);
    TransactionEntry entry = TransactionEntry.newInstance();
    TransactionData data = TransactionData.newInstance();
    data.setType("Buy");
    data.setShares(141.42);
    data.setNotes("GWT-Finance-Client - inserted transaction");
    entry.setTransactionData(data);
    String ticker = "NASDAQ:GOOG";
    String transactionPostUri = portfolioEditUri + "/positions/" + 
        ticker + "/transactions";
    service.insertEntry(transactionPostUri, entry,
        new TransactionEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a transaction: " +
            caught.getMessage(), true);
      }
      public void onSuccess(TransactionEntry result) {
        showStatus("Created a transaction.", false);
      }
    });
  }

  /**
   * Retrieve the portfolios feed using the Finance service and
   * the portfolios feed uri. In GData all get, insert, update
   * and delete methods always receive a callback defining success
   * and failure handlers.
   * Here, the failure handler displays an error message while the
   * success handler obtains the first Portfolio entry with a title
   * starting with "GWT-Finance-Client" and calls createTransaction
   * to insert a transaction.
   * If no portfolio is found a message is displayed.
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
          String portfolioEditUri = targetPortfolio.getEditLink().getHref();
          createTransaction(portfolioEditUri);
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
    service = FinanceService.newInstance(
        "HelloGData_Finance_CreateTransactionDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a transaction");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          getPortfolios(
              "http://finance.google.com/finance/feeds/default/portfolios");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Finance.", true);
    }
  }
}