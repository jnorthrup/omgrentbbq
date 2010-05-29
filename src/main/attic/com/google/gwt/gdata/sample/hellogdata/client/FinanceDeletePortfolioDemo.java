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
import com.google.gwt.gdata.client.finance.PortfolioEntryCallback;
import com.google.gwt.gdata.client.finance.PortfolioFeed;
import com.google.gwt.gdata.client.finance.PortfolioFeedCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to delete a portfolio.
 */
public class FinanceDeletePortfolioDemo extends GDataDemo {

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
        return new FinanceDeletePortfolioDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to delete an existing " +
            "portfolio for the authenticated user. It retrieves a list of " +
            "the user's portfolios, and deletes the first portfolio with a " +
            "title that starts with 'GWT-Finance-Client'.</p>";
      }

      @Override
      public String getName() {
        return "Finance - Deleting a portfolio";
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
  public FinanceDeletePortfolioDemo() {
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
   * Delete a portfolio entry using the Finance service and
   * the portfolio entry uri.
   * On success and failure, display a status message.
   * 
   * @param portfolioEntryUri The uri of the portfolio entry to delete
   */
  private void deletePortfolio(String portfolioEntryUri) {
    showStatus("Deleting portfolio...", false);
    service.deleteEntry(portfolioEntryUri,
        new PortfolioEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the portfolios feed: " +
            caught.getMessage(), true);
      }
      public void onSuccess(PortfolioEntry result) {
        showStatus("Deleted a portfolio.", false);
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
   * starting with "GWT-Finance-Client" and calls deletePortfolio
   * to delete the portfolio. Alternatively we could also have used
   * targetPortfolio.deleteEntry but the effect is the same.
   * If no portfolio is found a message is displayed.
   * 
   * @param portfoliosFeedUri The uri of the portfolios feed
   */
  private void getPortfolios(String portfoliosFeedUri) {
    showStatus("Loading portfolios feed...", false);
    service.getPortfolioFeed(portfoliosFeedUri, new PortfolioFeedCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while retrieving the portfolios " +
            "feed: " + caught.getMessage(), true);
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
          String portfolioEntryUri = targetPortfolio.getSelfLink().getHref();
          deletePortfolio(portfolioEntryUri);
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
        "HelloGData_Finance_DeletePortfolioDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Delete a portfolio");
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