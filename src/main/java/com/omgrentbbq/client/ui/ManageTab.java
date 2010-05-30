package com.omgrentbbq.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.AgendaHelper;
import com.omgrentbbq.client.TransactionManager;
import com.omgrentbbq.client.TransactionManagerAsync;
import com.omgrentbbq.shared.model.Income;
import com.omgrentbbq.shared.model.UserSession;

public class ManageTab extends Composite {
    AgendaHelper helper;
    final UserSession session;

    public ManageTab(AgendaHelper helper, UserSession session) {
        this.session = session;
        this.helper = helper;
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);

        final DisclosurePanel[] disclosurePanels = {dp1, dp2, dp3, dp4};
        for (DisclosurePanel disclosurePanel : disclosurePanels) {

            disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
                @Override
                public void onOpen(OpenEvent<DisclosurePanel> e) {
                    for (final DisclosurePanel panel : disclosurePanels) {
                        if (e.getSource() != panel) {
                            new Timer() {
                                @Override
                                public void run() {
                                    panel.setOpen(false);
                                }
                            }.schedule(250);
                        }
                    }
                }
            });
        }
        initWidget(rootElement);
    }

    interface ManageTabUiBinder extends UiBinder<HTMLPanel, ManageTab> {
    }

    private static ManageTabUiBinder ourUiBinder = GWT.create(ManageTabUiBinder.class);
    @UiField
    Button reportIncomeButton;
    @UiField
    CaptionPanel caption;
    @UiField
    DisclosurePanel dp1;
    @UiField
    DisclosurePanel dp2;
    @UiField
    DisclosurePanel dp3;
    @UiField
    DisclosurePanel dp4;
    @UiField
    Button addPayee;


    @UiHandler("reportIncomeButton")
    void reportIncome(ClickEvent e) {

        final PopupPanel panel = new PopupPanel();

        panel.setWidget(new IncomeInput(new AsyncCallback<IncomeInput>() {
            @Override
            public void onFailure(Throwable throwable) {

                panel.hide(true);
            }

            @Override
            public void onSuccess(IncomeInput incomeInput) {

                panel.hide(true);
                TransactionManagerAsync tm = GWT.create(TransactionManager.class);
                tm.addIncome(session, new Income(incomeInput.result, incomeInput.source.getText()), new AsyncCallback<Long>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        caption.setCaptionText("transaction failed :(");
                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        caption.setCaptionText("input successful" + (session.admin ? " key=" + aLong : "."));
                    }
                });

            }
        }));
        panel.setAnimationEnabled(true);
        panel.center();
        panel.setModal(true);
        panel.setVisible(true);

    }

    @UiHandler("addPayee")
    void addPayee(ClickEvent e) {
        final PopupPanel panel = new PopupPanel();

        panel.setWidget(new PayeePanel(panel));
    }

}