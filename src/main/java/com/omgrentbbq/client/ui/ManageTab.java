package com.omgrentbbq.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.omgrentbbq.client.rpc.TransactionManager;
import com.omgrentbbq.client.rpc.TransactionManagerAsync;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Income;
import com.omgrentbbq.shared.model.Payee;
import com.omgrentbbq.shared.model.UserSession;

import java.util.ArrayList;

public class ManageTab extends Composite {
    AgendaHelper helper;
    final UserSession session;
    private ArrayList<Group> groups;

    public ManageTab(AgendaHelper helper, UserSession session) {


        this.session = session;
        this.helper = helper;
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        updateGroupList(session);

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


    private void updateGroupList(UserSession session) {
        TransactionManagerAsync tm = GWT.create(TransactionManager.class);

        tm.getGroups(session, new GroupListUpdater());
    }

    interface ManageTabUiBinder extends UiBinder<HTMLPanel, ManageTab> {
    }

    private static ManageTabUiBinder ourUiBinder = GWT.create(ManageTabUiBinder.class);

    @UiField
    ListBox groupListBox;

    @UiHandler("groupListBox")
    void onGroupSelect(ChangeEvent e) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess() {
                payeeListUpdate();
            }
        });
    }

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
    @UiField
    Button addPayGroup;
    @UiField
    ListBox payeeListBox;

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
        final AsyncCallback<Payee> asyncCallback = new AsyncCallback<Payee>() {
            @Override
            public void onFailure(Throwable throwable) {
                caption.setCaptionHTML("<em>Payee was not added: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(final Payee payee) {
                final Group[] group = {groups.get(groupListBox.getSelectedIndex())};
                final TransactionManagerAsync lm = GWT.create(TransactionManager.class);

                group[0].payees.add(payee);

                lm.addPayee(session, payee, group[0], new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        caption.setCaptionHTML(
                                caption.getCaptionHTML() + "<br/>" +
                                        "<em>not saved: " + throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Void v) {
                        caption.setCaptionHTML(
                                caption.getCaptionHTML() +
                                        "<br/>" + "payee <em>" + payee.nickname + " </em> created." +
                                        "<br/>" + "<em>payee associated with </em>: " + group[0].nickname
                        );
                        group[0] = groups.get(groupListBox.getSelectedIndex());
                        updateGroupList(session);
                        groupListBox.setSelectedIndex(groups.indexOf(group[0]));
                    }
                });
            }
        };
        final PayeePanel payeePanel = new PayeePanel(panel, asyncCallback);
    }

    @UiHandler("addPayGroup")
    void addPayGroup(ClickEvent e) {

        final PopupPanel panel = new PopupPanel();
        final PayGroupPanel paygrouppanel = new PayGroupPanel(panel, (AsyncCallback<Group>) new AsyncCallback<Group>() {
            @Override
            public void onFailure(Throwable throwable) {
                caption.setCaptionHTML("<em>Group was not added: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(final Group group) {
                final TransactionManagerAsync tm = GWT.create(TransactionManager.class);
                tm.addGroup(session, group, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(Void aVoid) {

                        tm.createMembership(session, session.user, group, new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                //To change body of implemented methods use File | Settings | File Templates.
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                               caption.setCaptionHTML(  caption.getCaptionHTML() +
                                        "<br/>" + "group <em>" + group.nickname + " </em> created." +
                                        "<br/>" + "<em>group associated with </em>: " + session.user.nickname);
                            }
                        });

                    }
                });

            }
        });
    }

    class GroupListUpdater implements AsyncCallback<ArrayList<Group>> {

        @Override
        public void onFailure(Throwable throwable) {


        }

        @Override
        public void onSuccess(ArrayList<Group> groups) {
            Group group1 = null;

            if (groupListBox.getItemCount() > 0)
                group1 = ManageTab.this.groups.get(groupListBox.getSelectedIndex());


            groupListBox.clear();
            ManageTab.this.groups = groups;
            for (Group group : groups) {
                groupListBox.addItem(group.nickname);
            }

            if (group1 != null)
                groupListBox.setSelectedIndex(groups.indexOf(group1));
        }

    }

    void payeeListUpdate() {
        final int index = groupListBox.getSelectedIndex();
        final Group group = groups.get(index);

        payeeListBox.clear();
        for (Payee payee : group.payees) {
            payeeListBox.addItem(payee.nickname);
        }
    }
}
