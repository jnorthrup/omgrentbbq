package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.omgrentbbq.client.App;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 9, 2010
 * Time: 12:06:01 AM
 */
public class ManagePayeePanel extends HorizontalPanel {

    public ManagePayeePanel(ManagePanel managePanel, final App app) {
        add(new Label("Payees"));
        add(new Anchor("(+)") {{
            app.groupList.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent changeEvent) {
                    final int index = app.groupList.getSelectedIndex();
                    setTitle("add a new Payee to " + app.groups[index].getName());
                }
            });
            addClickHandler(new addPayeeClickHandler(app));

        }});
        add(app.payeeBox);
        app.populatePayeeList(app.groups[app.groupList.getSelectedIndex()]);

    }

}
