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
import com.google.gwt.gdata.client.atom.Text;
import com.google.gwt.gdata.client.finance.FinanceService;
import com.google.gwt.gdata.client.finance.PortfolioData;
import com.google.gwt.gdata.client.finance.PortfolioEntry;
import com.google.gwt.gdata.client.finance.PortfolioEntryCallback;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to create a portfolio.
 */
public class FinanceCreatePortfolioDemo extends GDataDemo {

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
        return new FinanceCreatePortfolioDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to create and insert " +
            "a new portfolio. The portfolio feed post URL " +
            "(http://finance.google.com/finance/feeds/default/portfolios) " +
            "is used to insert a new portfolio entry for the authenticated " +
            "user.</p>";
      }

      @Override
      public String getName() {
        return "Finance - Creating a portfolio";
      }
    };
  }

  private FinanceService service;
  private FlexTable mainPanel;
  private final String scope = "http://finance.google.com/finance/feeds/";

  /**
   * Setup the Finance service and create the main content panel.
   * If the user is not logged on to Finance display a message,
   * otherwise start the demo by creating a portfolio.
   */
  private FinanceCreatePortfolioDemo() {
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
   * Create a portfolio by inserting a portfolio entry into
   * a portfolio feed.
   * Set the portfolio's title to an arbitrary string. Here
   * we prefix the title with 'GWT-Finance-Client' so that
   * we can identify which portfolios were created by this demo.
   * The new portfolio is created with currency code set to USD.
   * On success and failure, display a status message.
   * 
   * @param portfolioFeedUri The uri of the portfolio feed into which
   * to insert the portfolio entry
   */
  private void createPortfolio(String portfolioFeedUri) {
    showStatus("Creating portfolio...", false);
    PortfolioEntry entry = PortfolioEntry.newInstance();
    entry.setTitle(Text.newInstance());
    entry.getTitle().setText("GWT-Finance-Client - inserted portfolio");
    PortfolioData data = PortfolioData.newInstance();
    data.setCurrencyCode("USD");
    entry.setPortfolioData(data);
    service.insertEntry(portfolioFeedUri, entry,
        new PortfolioEntryCallback() {
      public void onFailure(CallErrorException caught) {
        showStatus("An error occurred while creating a portfolio: " +
            caught.getMessage(), true);
      }
      public void onSuccess(PortfolioEntry result) {
        showStatus("Created a portfolio.", false);
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
        "HelloGData_Finance_CreatePortfolioDemo_v2.0");
    if (User.getStatus(scope) == AuthSubStatus.LOGGED_IN) {
      Button startButton = new Button("Create a portfolio");
      startButton.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          createPortfolio(
              "http://finance.google.com/finance/feeds/default/portfolios");
        }
      });
      mainPanel.setWidget(0, 0, startButton);
    } else {
      showStatus("You are not logged on to Google Finance.", true);
    }
  }
}