package com.omgrentbbq.client;

import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.ui.GroupPanel;
import com.omgrentbbq.client.ui.PayeePanel;
import com.omgrentbbq.shared.model.Group;
import com.omgrentbbq.shared.model.Payee;

/**
* Copyright 2010 Glamdring Incorporated Enterprises.
* User: jim
* Date: Jun 8, 2010
* Time: 9:51:57 AM
*/
public class ManageTabAsync implements RunAsyncCallback {

    public Timer timer;
    public VerticalPanel verticalPanel;
    private App app;

    public ManageTabAsync(App app) {
        this.app = app;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSuccess() {

        final FlexTable flexTable = new FlexTable();

        for (Group group : app.groups) {

            app.groupList.addItem(group.getName(), String.valueOf(group.$$()));
        }
        flexTable.setWidget(0, 0, new HTML("<h2>Manage your groups of payees and payments"));
        flexTable.setWidget(1, 0, new Label("Groups"));
        flexTable.setWidget(1, 1, new Anchor("(+)") {
            {
                setTitle("add a new Group");
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        final DialogBox box = new DialogBox();
                        box.setAnimationEnabled(true);
                        box.setText("<h2>add a new Group payment");

                        final GroupPanel groupPanel = new GroupPanel();
                        box.setWidget(groupPanel);
                        box.setPopupPosition(20, 20);
                        box.show();
                        groupPanel.cancelButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                box.hide(true);
                            }
                        });
                        groupPanel.okButton.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                final String s = groupPanel.name.getText();
                                if (s.isEmpty()) {
                                    box.setText("each group must have a name, please enter a name in the box");
                                    return;
                                } else {
                                    for (Group group1 : app.groups) {
                                        if (group1.getName().equals(s)) {
                                            box.setText("this name is already in use, please choose a different name");
                                            return;
                                        }
                                    }
                                }


                                final Group group = new Group();
                                group.$("name", groupPanel.name.getText());
                                group.$("privacy", groupPanel.privacy.getValue());

                                app.lm.addGroup(app.user, group, new AsyncCallback<Void>() {
                                    @Override
                                    public void onFailure(Throwable throwable) {
                                    }

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        groupReload(box);
                                    }
                                });

                            }
                        });
                    }
                }
                );

            }
        });
        flexTable.setWidget(1, 2, new Anchor("-") {
            {
                app.groupList.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent changeEvent) {
                        title();
                    }
                });
                title();
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        final Group group = app.groups[app.groupList.getSelectedIndex()];
                        if (app.groups.length > 0 && !group.isImmutable()) {
                            final PopupPanel panel1 = new PopupPanel(true);
                            panel1.setAnimationEnabled(true);
                            panel1.center();
                            panel1.setWidget(new VerticalPanel() {{
                                add(new Label("are you sure you wish to delete " + group.getName()));
                                add(new HorizontalPanel() {{
                                    add(new Button("Yes, Delete the group called " + group.getName()) {{
                                        addClickHandler(new ClickHandler() {
                                            @Override
                                            public void onClick(ClickEvent clickEvent) {
                                                app.lm.deleteGroup(app.user, group, new AsyncCallback<Void>() {
                                                    @Override
                                                    public void onFailure(Throwable throwable) {
                                                        panel1.hide();
                                                        Window.setStatus("Group " + group + " deletion has failed: " + throwable.getMessage());
                                                    }

                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        groupReload(panel1);
                                                    }
                                                });

                                            }
                                        });
                                    }});
                                    add(new Button("Cancel"));
                                }});
                            }});
                            panel1.setModal(true);
                            panel1.show();
                        }

                    }
                });
            }

            ;

            public void title() {
                if (app.groups.length > 0 && app.groups[app.groupList.getSelectedIndex()].isImmutable()) {
                    setVisible(false);
                } else {
                    setVisible(true);
                    setTitle("remove " + app.groupList.getItemText(app.groupList.getSelectedIndex()));
                }
            }

        }

        );

        flexTable.setWidget(1, 4, app.groupList);
        flexTable.getFlexCellFormatter().

                setColSpan(0, 0, 5);

        flexTable.getFlexCellFormatter().

                setColSpan(1, 4, 2);

        app.populatePayeeList(app.groups[app.groupList.getSelectedIndex()

                ]);
        verticalPanel = new VerticalPanel() {
            {
                add(flexTable);
                add(new HorizontalPanel() {{
                    add(new Label("Payees"));
                    add(new Anchor("(+)") {{


                        app.groupList.addChangeHandler(new ChangeHandler() {
                            @Override
                            public void onChange(ChangeEvent changeEvent) {

                                final int index = app.groupList.getSelectedIndex();

                                setTitle("add a new Payee to " + app.groups[index].getName());

                            }
                        });

                        addClickHandler(
                                new ClickHandler() {

                                    @Override
                                    public void onClick(ClickEvent clickEvent) {
                                        final int index = app.groupList.getSelectedIndex();
                                        if(index <0)return;
                                        final Group group = app.groups[index];


                                        final DecoratedPopupPanel box = new DecoratedPopupPanel();
                                        box.setAnimationEnabled(true);
                                        final PayeePanel payeePanel = new PayeePanel(box,
                                                new AsyncCallback<Payee>() {
                                                    @Override
                                                    public void onFailure(Throwable throwable) {

                                                    }

                                                    @Override
                                                    public void onSuccess(Payee payee) {
                                                        Window.setStatus("payee " + payee.getNickname() + " was successfully added to " + group.getName());

                                                        box.hide(true);
                                                    }
                                                },
                                                new AsyncCallback<Payee>() {
                                                    @Override
                                                    public void onFailure(Throwable throwable) {
                                                        report(throwable);
                                                    }

                                                    @Override
                                                    public void onSuccess(final Payee payee) {
                                                        new Timer() {
                                                            @Override
                                                            public void run() {
                                                                app.lm.addPayeeForGroup(payee, group, new AsyncCallback<Payee>() {
                                                                    @Override
                                                                    public void onFailure(Throwable throwable) {
                                                                        report(throwable);
                                                                    }

                                                                    @Override
                                                                    public void onSuccess(final Payee payee) {
                                                                        app.populatePayeeList(group);
                                                                    }
                                                                });
                                                            }
                                                        }.schedule(1000);


                                                    }
                                                });

                                    }

                                    void report(Throwable t) {
                                        Window.setStatus("payee was not added: " + t.getMessage());
                                    }
                                });

                    }});
                    add(app.payeeBox);
                }});

            }
        };
        app.tabPanel.insert(verticalPanel, "Manage Groups", 1);

    }

    public void groupReload(PopupPanel box) {
        if (true) {

            new Timer() {
                @Override
                public void run() {
                    app.lm.getGroups(app.user, new AsyncCallback<Group[]>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                        }

                        @Override
                        public void onSuccess(Group[] groups) {
                            app.groups = groups;
                            app.groupList.clear();
                            for (Group group1 : groups) {
                                app.groupList.addItem(group1.getName());

                            }
                            if (app.groupList.getItemCount() > 0) {
                                app.groupList.setSelectedIndex(0);
                                app.populatePayeeList(groups[0]);
                            }
                        }
                    });
                }
            }.schedule(1000);
            box.hide(true);

        }
    }


//        public class CurrentSelectedGroup {
//            public Group group;
//
//            public Group getGroup() {
//                return group;
//            }
//
//            public CurrentSelectedGroup invoke() {
//                final int selectedIndex = groupList.getSelectedIndex();
//
//
//                if (selectedIndex < 0) {
//                    return this;
//                }
//                group = groups[selectedIndex];
//                return this;
//            }
//        }
}
