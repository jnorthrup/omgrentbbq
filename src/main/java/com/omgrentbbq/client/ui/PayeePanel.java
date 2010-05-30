package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.shared.model.PayCycle;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 30, 2010
 * Time: 4:21:55 PM
 */
class PayeePanel extends VerticalPanel {

    private ContactCreationForm contactCreationForm;
    private PopupPanel panel;
    private ListBox payCycleListbox;
    private FlexTable scheduleWidget;

    /**
     * modal dialog ctor
     *
     * @param panel
     */
    public PayeePanel(final PopupPanel panel) {
        this();
        this.panel = panel;
        add(new HorizontalPanel() {{
            add(new Button("Ok") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        panel.hide(true);
                    }
                });
            }});
            add(new Button("Cancel") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        panel.hide(true);
                    }
                });
            }});
        }});

        panel.setAnimationEnabled(true);
        panel.setModal(true);
        panel.showRelativeTo(this);
        panel.setVisible(true);
    }

    PayeePanel() {

        add(new HTML("<h2>Adding a new Payee</h2>"));
        add(new HorizontalPanel() {{
            add(new Label("Nickname"));
            final TextBox nickname = new TextBox();
            add(nickname);
        }});
        add(new VerticalPanel() {
            {
                contactCreationForm = new ContactCreationForm();
                add(contactCreationForm);
                final SimplePanel simplePanel = new SimplePanel();
                add(new FlexTable() {{
                    payCycleListbox = new ListBox() {{
                        for (PayCycle o : PayCycle.values()) {
                            addItem(o.name());
                        }

                        addChangeHandler(new ChangeHandler() {
                            @Override
                            public void onChange(ChangeEvent changeEvent) {
                                final PayCycle payCycle = PayCycle.values()[getSelectedIndex()];
                                updatepaycycleWidget(payCycle, simplePanel);

                            }
                        });

                    }};
                    setWidget(0, 0, new Label("PaymentCycle"));
                    setWidget(0, 1, payCycleListbox);
                    updatepaycycleWidget(PayCycle.values()[payCycleListbox.getSelectedIndex()], simplePanel);
/* table1.setSize("", "100%");*/
                }});
                add(simplePanel);
            }

        });
    }

    private void updatepaycycleWidget(PayCycle payCycle, SimplePanel simplePanel) {
        scheduleWidget = payCycle.createScheduleWidget();
        simplePanel.setWidget(scheduleWidget);
    }
}
