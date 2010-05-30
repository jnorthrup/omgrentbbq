package com.omgrentbbq.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;


public class IncomeInput extends Composite {
    private final AsyncCallback<IncomeInput> callback;

    interface IncomeInpuUiBinder extends UiBinder<HTMLPanel, IncomeInput> {
    }

    private static IncomeInpuUiBinder ourUiBinder = GWT.create(IncomeInpuUiBinder.class);
    @UiField
    TextBox amount;
    @UiField
    CaptionPanel caption;
    @UiField
    TextBox source;
    @UiField
    Button ok;
    @UiField
    Button cancel;

    Float result;

    public IncomeInput(AsyncCallback<IncomeInput> callback) {
        this.callback = callback;
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);

        result = null;
    }

    @UiHandler("ok")
    void ok(ClickEvent e) {
        if (validate()) {
            callback.onSuccess(this);
        }
    }

    @UiHandler("cancel")
    void cancel(ClickEvent e) {
        callback.onFailure(new Throwable("User Cancellation"));
    }

    @UiHandler("amount")
    void onEdit(ChangeEvent e) {
        try {
            if (!validate()) {
                caption.setCaptionText("Must be greater than 0");
            }
        } catch (NumberFormatException e1) {
            caption.setCaptionText("The value you entred is not a valid number, please correct");
        }


    }

    private boolean validate() {
        result = null;
        result = Float.valueOf(amount.getText());
        return this.result > 0;
    }
}