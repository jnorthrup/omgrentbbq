package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.Group;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: May 30, 2010
 * Time: 4:21:55 PM
 */
class PayGroupPanel extends VerticalPanel {
    private ContactCreationForm contactCreationForm;
    private TextBox nickname;

    PayGroupPanel() {

        add(new HTML("<h2>Adding a new Group</h2>"));
       
        

        add(new VerticalPanel() {
            {
                add(new HTML("<em>Event/Location Details"));
                contactCreationForm = new ContactCreationForm();

                add(contactCreationForm);

            }
        });
    }

    /**
     * modal dialog ctor
     *
     * @param panel
     * @param callback
     */
    public PayGroupPanel(final PopupPanel panel, final AsyncCallback<Group>... callback) {
        this();

        final CaptionPanel captionPanel = new CaptionPanel();
        add(new HorizontalPanel() {{
            add(new Button("Ok") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        captionPanel.setCaptionHTML("");
                        final Contact contact = new Contact();
                        final String s = contactCreationForm.validate(contact);
                        if (!s.isEmpty()) {
                            final String html = captionPanel.getCaptionHTML();
                            captionPanel.setCaptionHTML(html + "<br/>" + s);
                            return;
                        } else {
                            contact.address2 = contactCreationForm.address2.getText();
                        } 

                        final Group group = new Group(contact.address1 + "|" +
                                contact.address2 + "|" +
                                contact.city + "|" +
                                contact.state + "|" +
                                contact.zip, contact.name );

                        for (AsyncCallback<Group> payGroupAsyncCallback : callback) {
                            payGroupAsyncCallback.onSuccess(group);
                        }
                        panel.hide(true);
                    }
                });
            }});
            add(new Button("Cancel") {{
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        panel.hide(true);
                        for (AsyncCallback<Group> payGroupAsyncCallback : callback) {
                            payGroupAsyncCallback.onFailure(new Throwable("User Cancelled Group"));
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