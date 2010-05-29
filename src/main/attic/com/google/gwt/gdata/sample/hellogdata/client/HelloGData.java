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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.GDataSystemPackage;
import com.google.gwt.gdata.sample.hellogdata.client.GDataDemo.GDataDemoInfo;
import com.google.gwt.user.client.ui.*;

/**
 * Main class for implementing the HelloGData gwt-gdata demo.
 */
public class HelloGData implements EntryPoint {

    /**
     * The value of defaultPackage indicates the package to use for loading the
     * GData library core. We load a specific package (any package) instead of
     * loading all of them.
     */
    public static GDataSystemPackage defaultPackage =
            GDataSystemPackage.ANALYTICS;
    protected DemoList list = new DemoList();
    private GDataDemoInfo curInfo;
    private GDataDemo curGDataDemo;
    private HTML description = new HTML();
    private VerticalPanel innerPanel = new VerticalPanel();
    private FlexTable outerPanel = new FlexTable();

    /**
     * The entrypoint for this demo. Load the GData API core.
     */
    public void onModuleLoad() {
        if (!GData.isLoaded(defaultPackage)) {
            GData.loadGDataApi(GDataDemo.GDATA_API_KEY, new Runnable() {
                public void run() {
                    GWT.runAsync(new RunAsyncCallback() {
                        @Override
                        public void onFailure(Throwable throwable) {


                        }

                        @Override
                        public void onSuccess() {
                            onGDataLoad();
                        }
                    });
                }
            }, defaultPackage);
        } else {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(Throwable throwable) {


                }

                @Override
                public void onSuccess() {

                    onGDataLoad();
                }
            });
        }
    }

    /**
     * Invoked when GData has loaded. Build the UI and display the default demo.
     */
    public void onGDataLoad() {
        if (User.getStatus() == AuthSubStatus.LOGGING_IN) {
            /*
            * AuthSub causes a refresh of the browser, so if status is LOGGING_IN
            * don't render anything. An empty page refresh is friendlier.
            */
            return;
        }

        DecoratorPanel decorator = new DecoratorPanel();
        decorator.add(outerPanel);

        RootPanel.get().setStylePrimaryName("hm-body");
        RootPanel.get().add(new HTML("<img src='logo-small.png' alt='gwt logo' " +
                "align='absmiddle'><span class='hm-title'>Google GData API Library " +
                "for GWT Demo</span>"));
        RootPanel.get().add(decorator);

        innerPanel.setStylePrimaryName("hm-innerpanel");
        innerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        innerPanel.setSpacing(10);

        outerPanel.setStylePrimaryName("hm-outerpanel");
        outerPanel.insertRow(0);
        outerPanel.insertRow(0);
        outerPanel.insertRow(0);
        outerPanel.insertRow(0);

        outerPanel.addCell(0);
        outerPanel.addCell(1);
        outerPanel.addCell(2);
        outerPanel.addCell(3);

        outerPanel.setWidget(0, 0, new HTML(
                "This GData-enabled application was built using the GData "
                        + "API Library for GWT, "
                        + "<a href=\"http://code.google.com/p/gwt-gdata/\">"
                        + "http://code.google.com/p/gwt-gdata/</a>. "
                        + "The drop down list below allows you to select a scenario that "
                        + "demonstrates a particular capability of the GData support."));

        outerPanel.setWidget(1, 0, innerPanel);

        HorizontalPanel horizPanel = new HorizontalPanel();
        list.setStylePrimaryName("hm-demolistbox");
        list.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                GDataDemoInfo info = list.getGDataDemoSelection();
                if (info == null) {
                    showInfo();
                } else {
                    show(info);
                }
            }
        });
        description.setStylePrimaryName("hm-description");
        innerPanel.clear();
        innerPanel.add(horizPanel);
        innerPanel.add(description);
        horizPanel.add(new Label("Select Demo: "));
        horizPanel.add(list);
        loadGDataDemos();
        showInfo();
    }

    /**
     * Instantiates and runs a given GData demo.
     *
     * @param info An instance of an info object describing the demo
     */
    public void show(GDataDemoInfo info) {
        // Don't bother re-displaying the existing GDataDemo.
        if (info == curInfo) {
            return;
        }
        curInfo = info;

        // Remove the old GDataDemo from the display area.
        if (curGDataDemo != null) {
            innerPanel.remove(curGDataDemo);
        }

        // Get the new GDataDemo instance, and display its description in the
        // MapsDemo list.
        curGDataDemo = info.getInstance();
        list.setGDataDemoSelection(info.getName());

        // Display the new GDataDemo and update the description panel.
        innerPanel.add(curGDataDemo);
        description.setHTML(info.getDescription());

        // info is an inner class of the class we want to display. Strip off the
        // generated anonymous class name.
        String strippedClassName = info.getClass().getName();
        int lastIndex = strippedClassName.lastIndexOf('$');
        if (lastIndex > 0) {
            strippedClassName = strippedClassName.substring(0, lastIndex);
        }

        outerPanel.setWidget(3, 0, new HTML("<h5> See source in "
                + strippedClassName + "</h5><h5>GData API version: " +
                GData.getVersion()
                + "</h5>"));

        curGDataDemo.onShow();
    }

    /**
     * Adds all GDataDemos to the list. Note that this does not create actual
     * instances of all GDataDemos yet (they are created on-demand). This can
     * make a significant difference in startup time.
     */
    protected void loadGDataDemos() {
        list.addGDataDemo(AccountsAuthSubAuthenticationDemo.init());
        list.addGDataDemo(GDataPackagesDemo.init());
        list.addGDataDemo(AnalyticsYourAccountsDemo.init());
        list.addGDataDemo(AnalyticsTopPagesDemo.init());
        list.addGDataDemo(AnalyticsLanguagesDemo.init());
        list.addGDataDemo(AnalyticsTopSearchesDemo.init());
        list.addGDataDemo(AnalyticsVisitsDemo.init());
        list.addGDataDemo(AnalyticsBounceRateDemo.init());
        list.addGDataDemo(GoogleBaseRetrieveItemsDemo.init());
        list.addGDataDemo(GoogleBaseCreateItemDemo.init());
        list.addGDataDemo(GoogleBaseUpdateItemDemo.init());
        list.addGDataDemo(GoogleBaseDeleteItemDemo.init());
        list.addGDataDemo(GoogleBaseRetrieveItemAttributesDemo.init());
        list.addGDataDemo(GoogleBaseRetrieveItemTypeAttributesDemo.init());
        list.addGDataDemo(GoogleBaseRetrieveMediaDemo.init());
        list.addGDataDemo(GoogleBaseQuerySnippetsForCamerasDemo.init());
        list.addGDataDemo(GoogleBaseQuerySnippetsForJobsDemo.init());
        list.addGDataDemo(GoogleBaseQuerySnippetsForConvertiblesDemo.init());
        list.addGDataDemo(GoogleBaseQuerySnippetsForRecipesDemo.init());
        list.addGDataDemo(GoogleBaseQuerySnippetsForHousingDemo.init());
        list.addGDataDemo(BloggerRetrieveBlogsDemo.init());
        list.addGDataDemo(BloggerRetrieveBlogPostsDemo.init());
        list.addGDataDemo(BloggerCreateBlogPostDemo.init());
        list.addGDataDemo(BloggerRetrieveSpecificBlogPostDemo.init());
        list.addGDataDemo(BloggerUpdateBlogPostDemo.init());
        list.addGDataDemo(BloggerDeleteBlogPostDemo.init());
        list.addGDataDemo(BloggerQueryBlogPostsDemo.init());
        list.addGDataDemo(BloggerRetrieveBlogPostCommentsDemo.init());
        list.addGDataDemo(BloggerCreateBlogPostCommentDemo.init());
        list.addGDataDemo(BloggerDeleteBlogPostCommentDemo.init());
        list.addGDataDemo(CalendarRetrieveCalendarsDemo.init());
        list.addGDataDemo(CalendarCreateCalendarDemo.init());
        list.addGDataDemo(CalendarUpdateCalendarDemo.init());
        list.addGDataDemo(CalendarDeleteCalendarDemo.init());
        list.addGDataDemo(CalendarRetrieveEventsDemo.init());
        list.addGDataDemo(CalendarQueryEventsFullTextDemo.init());
        list.addGDataDemo(CalendarQueryEventsByDateDemo.init());
        list.addGDataDemo(CalendarCreateSingleEventDemo.init());
        list.addGDataDemo(CalendarCreateRecurringEventDemo.init());
        list.addGDataDemo(CalendarUpdateEventDemo.init());
        list.addGDataDemo(CalendarDeleteEventDemo.init());
        list.addGDataDemo(CalendarCreateEventReminderDemo.init());
        list.addGDataDemo(CalendarCreateEventWithExtendedPropertyDemo.init());
        list.addGDataDemo(ContactsRetrieveContactsDemo.init());
        list.addGDataDemo(ContactsCreateContactDemo.init());
        list.addGDataDemo(ContactsRetrieveContactsUsingQueryDemo.init());
        list.addGDataDemo(ContactsUpdateContactDemo.init());
        list.addGDataDemo(ContactsDeleteContactDemo.init());
        list.addGDataDemo(ContactsRetrieveContactGroupsDemo.init());
        list.addGDataDemo(ContactsRetrieveContactGroupMembersDemo.init());
        list.addGDataDemo(ContactsCreateContactGroupDemo.init());
        list.addGDataDemo(ContactsUpdateContactGroupDemo.init());
        list.addGDataDemo(ContactsDeleteContactGroupDemo.init());
        list.addGDataDemo(FinanceRetrievePortfoliosDemo.init());
        list.addGDataDemo(FinanceRetrievePortfolioDemo.init());
        list.addGDataDemo(FinanceRetrieveTransactionsDemo.init());
        list.addGDataDemo(FinanceRetrievePositionsDemo.init());
        list.addGDataDemo(FinanceCreatePortfolioDemo.init());
        list.addGDataDemo(FinanceUpdatePortfolioDemo.init());
        list.addGDataDemo(FinanceDeletePortfolioDemo.init());
        list.addGDataDemo(FinanceCreateTransactionDemo.init());
        list.addGDataDemo(FinanceUpdateTransactionDemo.init());
        list.addGDataDemo(FinanceDeleteTransactionDemo.init());
        list.addGDataDemo(MapsRetrieveMapsDemo.init());
        list.addGDataDemo(MapsCreateMapDemo.init());
        list.addGDataDemo(MapsUpdateMapDemo.init());
        list.addGDataDemo(MapsDeleteMapDemo.init());
        list.addGDataDemo(MapsRetrieveMapFeaturesDemo.init());
        list.addGDataDemo(MapsCreateMapFeatureDemo.init());
        list.addGDataDemo(MapsUpdateMapFeatureDemo.init());
        list.addGDataDemo(MapsDeleteMapFeatureDemo.init());
        list.addGDataDemo(SidewikiRetrieveEntriesDemo.init());
        list.addGDataDemo(SidewikiQueryEntriesBySiteDemo.init());
        list.addGDataDemo(SidewikiQueryEntriesByAuthorDemo.init());
    }

    /**
     * Displays the default GData demo.
     */
    private void showInfo() {
        show(list.find("API - Authsub Authentication"));
    }
}