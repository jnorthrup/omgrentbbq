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

import com.google.gwt.user.client.ui.Composite;

/**
 * All HelloMaps demos extend this class.
 */
public abstract class GDataDemo extends Composite {

  public final static String GDATA_API_KEY = "ABQIAAAAWpB08GH6KmKITXI7rtGRpBREGtQZq9OFJfHndXhPP8gxXzlLARRs1Zat3MllIUzN5hpmsbfnyEF7wA";//"ABQIAAAAWpB08GH6KmKITXI7rtGRpBRi_j0U6kJrkFvY4-OX2XYmEAa76BRl5-EVx5PbQ1VFzCJyQmfA43hlLA";
  
  /**
   * This inner static class creates a factory method to return an instance of
   * GDataDemo.
   */
  public abstract static class GDataDemoInfo {

    private GDataDemo instance;

    /**
     * @return a new instance of GDataDemo
     */
    public abstract GDataDemo createInstance();

    /**
     * @return a description of this demo.
     */
    public String getDescription() {
      return "<p><i>Description not provided.</i></p>\n"
          + "<p>(Add an implementation of <code>getDescriptionHTML()</code> "
          + "for this demo)</p>";
    }

    /**
     * Factory method for GDataDemo.
     * 
     * @return an instance of this GDataDemo class
     */
    public GDataDemo getInstance() {
      /* create a new instance every time. For the purposes of this demo
       * we want the GData output to be "live" always and never cached. */
      instance = createInstance();
      return instance;
    }

    public abstract String getName();
  }

  /**
   * Method that gets called by the main demo when this demo is now active on
   * the screen.
   */
  public void onShow() {
  }
}
