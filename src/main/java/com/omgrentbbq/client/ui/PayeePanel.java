package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.Payee;
import com.omgrentbbq.shared.model.Periodicity;


/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 30, 2010
 * Time: 4:21:55 PM
 */
public class PayeePanel extends VerticalPanel {

    private ContactCreationForm contactCreationForm;
    private ListBox payCycleListbox;
    private FlexTable scheduleWidget;
    private Label nickname;
    private TextBox account;
    private TextBox fax;
    private Timer timer;

    public PayeePanel() {

        add(new HTML("<h2>Adding a new Payee</h2>"));
        add(new HorizontalPanel() {{
            add(new Label("Nickname: "));
            nickname = new Label();
            add(nickname);
        }});

        add(new HorizontalPanel() {{
            add(new Label("Account"));
            account = new TextBox();
            add(account);
        }});
        add(new VerticalPanel() {
            {
                add(new HTML("<em>Payment Delivery to:"));
                contactCreationForm = new ContactCreationForm();

                add(contactCreationForm);
                add(new HorizontalPanel() {{
                    add(new Label("fax"));
                    fax = new TextBox();
                    add(fax);
                }});
                final SimplePanel simplePanel = new SimplePanel();
                add(new FlexTable() {{
                    payCycleListbox = new ListBox() {{
                        for (Periodicity o : Periodicity.values()) {
                            addItem(o.name());
                        }

                        addChangeHandler(new ChangeHandler() {
                            @Override
                            public void onChange(ChangeEvent changeEvent) {
                                final Periodicity periodicity = Periodicity.values()[getSelectedIndex()];
                                updatepaycycleWidget(periodicity, simplePanel);

                            }
                        });

                    }};
                    setWidget(0, 0, new Label("PaymentCycle"));
                    setWidget(0, 1, payCycleListbox);
                    updatepaycycleWidget(Periodicity.values()[payCycleListbox.getSelectedIndex()], simplePanel);
/* table1.setSize("", "100%");*/
                }});
                add(simplePanel);
            }

        });


        final KeyPressHandler pressHandler = new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent keyPressEvent) {
                if (null == timer)
                    timer = new Timer() {
                        @Override
                        public void run() {
                            nickname.setText(contactCreationForm.name.getText() + "/" + account.getText());
 
                        }
                    };
                else timer.cancel();

                timer.schedule(2500);

            }
        };
        contactCreationForm.name.addKeyPressHandler(
                pressHandler);
        account.addKeyPressHandler(pressHandler);
    }

    private void updatepaycycleWidget(Periodicity periodicity, SimplePanel simplePanel) {
        scheduleWidget = periodicity.createScheduleWidget();
        simplePanel.setWidget(scheduleWidget);

    }

    /**
     * modal dialog ctor
     *
     * @param panel
     */
    public PayeePanel(final PopupPanel panel, final AsyncCallback<Payee>... callback) {
        this();

        final CaptionPanel captionPanel = new CaptionPanel();
        add(new HorizontalPanel() {{
            add(new Button("Ok") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        captionPanel.setCaptionHTML("");
                        final Payee payee = new Payee();
                        final Contact contact = payee.getContact();

                        final String s = contactCreationForm.validate(contact);
                        if (!s.isEmpty()) {
                            final String html = captionPanel.getCaptionHTML();
                            captionPanel.setCaptionHTML(html + "<br/>" + s);
                            return;
                        } else {
                            contact.$("address2", contactCreationForm.address2.getText());

                        }
                        String acctVal = null;
                        acctVal = account.getText();
                        final String nicknameVal = nickname.getText();
                        if (nicknameVal.isEmpty()) {
                            payee.$("nickname", contact.getName() + ((acctVal.isEmpty()) ? "" : "-" + acctVal));
                            nickname.setText((String) payee.$("nickname"));
                        } else payee.$("nickname", nicknameVal);
                        payee.$("account", acctVal);
                        final Periodicity periodicity = Periodicity.values()[payCycleListbox.getSelectedIndex()];
                        payee.$("periodicity", periodicity);
                        payee.$("fax", fax.getText());
                        payee.$("schedule", periodicity.getSchedule(scheduleWidget, captionPanel));
                        if (payee.$("schedule") != null) {
                            for (AsyncCallback<Payee> payeeAsyncCallback : callback) {
                                payeeAsyncCallback.onSuccess(payee);
                            }

                            panel.hide(true);
                        }

                    }
                });
            }});
            add(new Button("Cancel") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        panel.hide(true);
                        for (AsyncCallback<Payee> payeeAsyncCallback : callback) {
                            payeeAsyncCallback.onFailure(new Throwable("User Cancelled Payee"));
                        }
                    }
                });
            }});

        }});
        add(captionPanel);

        panel.add(this);
        panel.setAnimationEnabled(true);
        panel.setModal(true);
        panel.setPopupPosition(20, 20);
        panel.show();
    }
}
