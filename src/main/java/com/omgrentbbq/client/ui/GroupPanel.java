package com.omgrentbbq.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;


/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 8, 2010
 * Time: 12:16:41 AM
 */
public class GroupPanel extends Composite {
    interface GroupPanelUiBinder extends UiBinder<HTMLPanel, GroupPanel> {
    }

    private static GroupPanelUiBinder ourUiBinder = GWT.create(GroupPanelUiBinder.class);
    @UiField
    public
    TextBox name;
    @UiField
    public
    CheckBox privacy;
    @UiField
    public
    Button okButton;
    @UiField
    public
    Button cancelButton;
    @UiField
    CaptionPanel caption;

    public GroupPanel() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }
}