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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * The following example demonstrates how to load individual
 * GData packages.
 */
public class GDataPackagesDemo extends GDataDemo {

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
        return new GDataPackagesDemo();
      }

      @Override
      public String getDescription() {
        return "<p>This sample demonstrates how to load individual GData " +
            "packages. In your GData apps you can enhance performance by " +
            "loading only the GData packages that are used.</p>";
      }

      @Override
      public String getName() {
        return "API - Loading GData packages";
      }
    };
  }

  private FlexTable mainPanel;

  /**
   * Create the main content panel for this demo and call
   * showPackageStatus to display the load status
   * for each GData system package.
   */
  public GDataPackagesDemo() {
    mainPanel = new FlexTable();
    mainPanel.setCellPadding(4);
    mainPanel.setCellSpacing(0);
    initWidget(mainPanel);
    startDemo();
  }

  /**
   * Refreshes this demo by clearing the contents of
   * the main panel and calling showPackageStatus to
   * display the load status for each GData system package.
   */
  private void refreshDemo() {
    mainPanel.clear();
    showPackageStatus();
  }
  
  /**
   * Display the GData package statuses in a
   * tabular fashion with the help of a GWT FlexTable widget.
   * We initialize an array containing the name, icon
   * and url for each of the supported GData packages.
   * For each package a table row is added displaying the name,
   * page, link and icon, in addition to the load status.
   * The GData class contains the isLoaded method which is used
   * to retrieve the load status for a given package.
   */
  private void showPackageStatus() {
    String[][] systems = new String[][] {
        new String[] { "Google Analytics",
            "ANALYTICS",
            "gdata-analytics.png",
            "http://code.google.com/apis/analytics/" },
        new String[] { "Google Base",
            "GBASE",
            "gdata-base.png",
            "http://code.google.com/apis/base/" },
        new String[] { "Google Blogger",
            "BLOGGER",
            "gdata-blogger.png",
            "http://code.google.com/apis/blogger/" },
        new String[] { "Google Calendar",
            "CALENDAR",
            "gdata-calendar.png",
            "http://code.google.com/apis/calendar/" },
        new String[] { "Google Contacts",
            "CONTACTS",
            "gdata-contacts.png",
            "http://code.google.com/apis/contacts/" },
        new String[] { "Google Finance",
            "FINANCE",
            "gdata-finance.png",
            "http://code.google.com/apis/finance/" },
        new String[] { "Google Maps",
            "MAPS",
            "gdata-maps.png",
            "http://code.google.com/apis/maps/documentation/mapsdata/" },
        new String[] { "Google Sidewiki",
            "SIDEWIKI",
            "gdata-sidewiki.png",
            "http://code.google.com/apis/sidewiki/" }
    };
    for (int i = 0; i < systems.length; i++) {
      String[] sys = systems[i];
      final String identifier = sys[1];
      mainPanel.insertRow(i);
      mainPanel.addCell(i);
      mainPanel.addCell(i);
      mainPanel.addCell(i);
      mainPanel.addCell(i);
      Image icon = new Image(sys[2]);
      mainPanel.setWidget(i, 0, icon);
      Label name = new HTML("<a href=\"" + sys[3] + "\">" + sys[0] + "</a>");
      mainPanel.setWidget(i, 1, name);
      Label statusLabel = new Label();
      Anchor actionLink = new Anchor();
      if (GData.isLoaded(GDataSystemPackage.valueOf(identifier))) {
        statusLabel.setText("Loaded");
      } else {
        statusLabel.setText("Not loaded");
        actionLink.setText("Load");
        actionLink.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            GData.loadGDataApi(GDATA_API_KEY, new Runnable() {
              public void run() {
                refreshDemo();
              }
            }, GDataSystemPackage.valueOf(identifier));
          }
        });
      }
      mainPanel.setWidget(i, 2, statusLabel);
      mainPanel.setWidget(i, 3, actionLink);
    }
  }
  
  /**
   * Starts this demo.
   */
  private void startDemo() {
    showPackageStatus();
  }
}