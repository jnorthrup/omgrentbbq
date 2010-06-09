package com.omgrentbbq.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.omgrentbbq.client.App;
import com.omgrentbbq.client.ui.GroupPanel;
import com.omgrentbbq.shared.model.Group;

/**
 * Copyright 2010 Glamdring Incorporated Enterprises.
 * User: jim
 * Date: Jun 8, 2010
 * Time: 1:57:56 PM
 */
class GroupsFlexTable extends FlexTable {
    public final Anchor removeAnchor;
    public final HTML headerHtml;
    public final Label groupsLabel;
    public final Anchor addAnchor;
    private final App app;


    public GroupsFlexTable(final App app) {
        this.app = app;

        for (Group group : app.groups) {
            app.groupList.addItem(group.getName(), String.valueOf(group.$$()));
        }

        headerHtml = new HTML("<h2>Manage your groups of payees and payments");
        setWidget(0, 0, headerHtml);
        groupsLabel = new Label("Groups");
        setWidget(1, 0, groupsLabel);
        addAnchor = new GrpRmAnchor();
        setWidget(1, 1, addAnchor);
        removeAnchor = new GrpAddAnchor();
        setWidget(1, 2, removeAnchor);

        setWidget(1, 4, app.groupList);
        getFlexCellFormatter().setColSpan(0, 0, 5);

        getFlexCellFormatter().setColSpan(1, 4, 2);
    }

    class GrpAddAnchor extends Anchor {
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
                                                    app.groupReload(panel1);
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


        public GrpAddAnchor() {
            super("-");
        }

        public void title() {
            if (app.groups.length > 0 && app.groups[app.groupList.getSelectedIndex()].isImmutable()) {
                setVisible(false);
            } else {
                setVisible(true);
                setTitle("remove " + app.groupList.getItemText(app.groupList.getSelectedIndex()));
            }
        }

    }

    private class GrpRmAnchor extends Anchor {
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
                                    app.groupReload(box);
                                }
                            });

                        }
                    });
                }
            }
            );

        }

        public GrpRmAnchor() {
            super("(+)");
        }
    }
}
