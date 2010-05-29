package com.omgrentbbq.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 25, 2010
 * Time: 11:15:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WelcomeTab extends Composite
{
    interface WelcomeUiBinder extends UiBinder<HTMLPanel, WelcomeTab> {
    }

    private static WelcomeUiBinder ourUiBinder = GWT.create(WelcomeUiBinder.class);
    @UiField
    HTMLPanel top;


    public WelcomeTab() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }
}