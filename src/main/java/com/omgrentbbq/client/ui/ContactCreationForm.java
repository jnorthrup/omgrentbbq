/**
 *
 */
package com.omgrentbbq.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.shared.model.Contact;
import com.omgrentbbq.shared.model.User;

/**
 * @author jim
 */
public class ContactCreationForm extends Composite {

    private static ContactCreationFormUiBinder uiBinder = GWT
            .create(ContactCreationFormUiBinder.class);
    private User user;
    private AsyncCallback<Contact> done;

    interface ContactCreationFormUiBinder extends
            UiBinder<Widget, ContactCreationForm> {

    }

    @UiField
    public
    TextBox name;

/*
    @UiField
    TextBox email;
*/
    @UiField
    TextBox address1;
    @UiField
    TextBox address2;
    @UiField
    TextBox city;
    @UiField
    TextBox state;
    @UiField
    TextBox zip;
    @UiField
    TextBox phone;
    @UiField
    Button okButton;
//    @UiField
//    Button cancelButton;
    @UiField
    CaptionPanel captionPanel;
    @UiField
    Button mapButton;
    @UiField
    Image
            mapImage;
/*
    @UiField
    PasswordTextBox password1;
    @UiField
    PasswordTextBox password2;
*/

    public ContactCreationForm(AsyncCallback<Contact>... done) {
        if (done.length > 0)
            this.done = done[0];
        initWidget(uiBinder.createAndBindUi(this));
        if (this.done == null) {
            okButton.removeFromParent();
            captionPanel.removeFromParent();
            mapButton.removeFromParent();
        }

    }

    @UiHandler("mapButton")
    void handleLattitude(ClickEvent e) {
        mapImage.setUrl(
                "http://maps.google.com/maps/api/staticmap?markers=size:small|color:red|" + address1.getText() + " " + city.getText() + " " + " , " + state.getText() + " " + zip.getText()
                        + "&sensor=false&size=320x280"
        );
        mapImage.setVisible(true);

    }

    @UiHandler("okButton")
    void handleOk(ClickEvent e) {
        final Contact contact = new Contact();

        String s = validate(contact);

        if (s.isEmpty()) {
            contact.setAddress2(address2.getText());
            done.onSuccess(contact);

        } else {
            captionPanel.setCaptionHTML(s);
        }
    }

    public String validate(Contact contact) {
        String s = "";
        contact.setName(name.getText());
        if (contact.getName().length() < 3) s += "name too short<br/>";
        /*contact.email = email.getText();
        if (contact.email.length() < 8 || !contact.email.contains("@")) s += "email is invalid<br/>";
*/
        contact.setAddress1(address1.getText());
        if (contact.getAddress1().length() < 5) s += "address1 too short<br/>";
        contact.setPhone(phone.getText());

        if (contact.getPhone().length() < 10) s += "phone too short<br/>";
        /*      contact.password = password1.getText();
                if (contact.password.length() < 6) s += "password too short<br/>";
                if (!contact.password.equals(password2.getText())) s += "passwords must match<br/>";
        */
        contact.setCity(city.getText());
        if (contact.getCity().length() < 3) s += "city too short<br/>";
        contact.setState(state.getText());
        if (contact.getState().length() < 2) s += "state too short<br/>";
        contact.setZip(zip.getText());
        if (contact.getZip().length() < 5) s += "zip too short<br/>";
        return s;
    }

}
