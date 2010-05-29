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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.gdata.sample.hellogdata.client.GDataDemo.GDataDemoInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

import java.util.ArrayList;

/**
 * Implements a GDataDemo list box.
 */
public class DemoList extends Composite {

  private ListBox list = new ListBox();
  private ArrayList<GDataDemoInfo> sinks = new ArrayList<GDataDemoInfo>();

  /**
   * Initializes this widget.
   */
  public DemoList() {
    initWidget(list);
    list.setVisibleItemCount(1);
  }
  
  public void addChangeHandler(ChangeHandler handler) {
    list.addChangeHandler(handler);
  }

  /**
   * Adds a demo info instance to the list.
   * @param info The demo info instance
   */
  public void addGDataDemo(final GDataDemoInfo info) {
    String name = info.getName();
    list.addItem(name);
    sinks.add(info);
  }

  /**
   * Retrieves a demo info instance by name.
   * @param sinkName The name
   * @return A demo info instance or null if none is found
   */
  public GDataDemoInfo find(String sinkName) {
    for (int i = 0; i < sinks.size(); ++i) {
      GDataDemoInfo info = sinks.get(i);
      if (info.getName().equals(sinkName)) {
        return info;
      }
    }
    return null;
  }
  
  public GDataDemoInfo getGDataDemoSelection() {
    return find(list.getItemText(list.getSelectedIndex()));
  }

  /**
   * Sets the selection for the list box.
   * @param name The name of the demo to select
   */
  public void setGDataDemoSelection(String name) {
    for (int i = 0; i < list.getItemCount(); i++) {
      if (name.equals(list.getItemText(i))) {
        list.setSelectedIndex(i);
        break;
      }
    }
  }
}
