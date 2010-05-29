package com.omgrentbbq.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.omgrentbbq.shared.model.UserSession;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 29, 2010
 * Time: 1:31:10 AM
 * To change this template use File | Settings | File Templates.
 */
class AgendaHelper {
    private final SimplePanel htmlHolder;
    private UserSession session;


    AgendaHelper(  TabPanel tabPanel, UserSession session) {
        this.htmlHolder = new SimplePanel();
        this.session = session;
        tabPanel.add(htmlHolder, "Summary Page");

        freshCalendar( );


    }

    public void freshCalendar( ) {
        final HTML html = new HTML(
                "<iframe " +
                        "src='http://www.google.com/calendar/embed?height=600&amp;wkst=1&amp;bgcolor=%23FFFFFF&amp;" +
                        "src=" +
                        session.user.emailAddress +
                        "&amp;color=%231B887A&amp;ctz=Pacific' " +
                        "style='border-width:0'  width=600 height=500 frameborder=0 scrolling='no'&amp;_r" + new Random().nextGaussian() + " />");
        htmlHolder.clear();
        htmlHolder.setWidget(html);
    }
}
