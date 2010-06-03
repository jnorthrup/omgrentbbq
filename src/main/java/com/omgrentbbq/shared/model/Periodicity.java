package com.omgrentbbq.shared.model;

import com.google.gwt.user.client.ui.*;

/**
 * bill is due
 */
public enum Periodicity {
    Manual,
    Monthly("Day of the month") {

        public Widget createWidget(final int... features) {
            return new ListBox() {{
                for (int d = 1; d < 32; d++) {
                    addItem(String.valueOf(d));
                }
            }};
        }
    },
    Daily,
    Weekly("Day of the week") {

        public Widget createWidget(final int... features) {
            return new ListBox() {{
                for (String s : new String[]{
                        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
                }) {
                    addItem(s);
                }
                setTitle(scheduleOptionDescriptions[0]);

            }};
        }
    },
    BiWeekly("First Week of Month", "Day of the Week") {
        public Widget createWidget(final int... features) {
            Widget widget = null;
            if (features.length > 0)

                switch (features[0]) {
                    case 1:
                        widget = Weekly.createWidget();
                        break;
                    case 0: {
                        widget = new ListBox() {{
                            final String[] wom = {
                                    "First", "Second", "Third", "Fourth",
                            };
                            for (String s : wom) {
                                addItem(s);
                            }
                        }};
                        break;
                    }
                }

            if (widget != null) {
                widget.setTitle(scheduleOptionDescriptions[features[0]]);
            }

            return widget;
        }
    },
    BiMonthly("First month of the year", "Day of the month") {
        public Widget createWidget(final int... features) {
            Widget widget = null;
            if (features.length > 0)
                switch (features[0]) {
                    case 1: {

                        widget = Monthly.createWidget();
                        break;
                    }
                    case 0: {
                        widget = new ListBox() {{
                            final String[] moy = {
                                    "January",
                                    "February",
                                    "March",
                                    "April",
                                    "May",
                                    "June",
                                    "July",
                                    "August",
                                    "September",
                                    "November",
                                    "December",

                            };
                            for (int i = 0; i < moy.length; i++) {
                                String s = moy[i];
                                addItem(s);
                            }
                        }};
                    }
                    break;
                }
            if (widget != null) {
                widget.setTitle(scheduleOptionDescriptions[features[0]]);
            }

            return widget;
        }

    },
    Quarterly("First Month of the Year", "Day of the month") {
        @Override
        public Widget createWidget(int... features) {

            return BiMonthly.createWidget(features);
        }},
    DayOfTheYear("Day of the Year") {

        public int[] getSchedule(FlexTable w, CaptionPanel captionPanel, int... values) {
            final int[] ints = super.getSchedule(w, captionPanel, values);
            if (ints != null) {
                final int anInt = ints[0];
                if (anInt > 365) {
                    final String s = captionPanel.getCaptionHTML();
                    captionPanel.setCaptionHTML(s + "<br/>" + scheduleOptionDescriptions[0] + " must be between 1 and 365");

                    return null;
                }
                return new int[]{anInt - 1};
            }
            return null;
        }
        public void setSchedule(FlexTable w, int... values) {
            for (int i = 0; i < values.length; i++) {
                int value = values[i];

                TextBox widget = (TextBox) w.getWidget(i, 1);
                widget.setText(String.valueOf(values[i] + 1));
            }
        }
    },
    DateOnEachYear("Month of the Year", "Day of the Month") {
        @Override
        public Widget createWidget(int... features) {

            final Widget widget = BiMonthly.createWidget(features);

            if (features.length > 0)
                widget.setTitle(scheduleOptionDescriptions[features[0]]);


            return widget;
        }},

    WeekdaysOfTheMonth("First week due of Month", "Day of the Week") {
        @Override
        public Widget createWidget(int... features) {

            final Widget widget = BiWeekly.createWidget(features);

            if (features.length > 0)
                widget.setTitle(scheduleOptionDescriptions[features[0]]);


            return widget;
        }},;

    public String[] scheduleOptionDescriptions;

    Periodicity(String... scheduleOptionDescriptions) {
        this.scheduleOptionDescriptions = scheduleOptionDescriptions;
    }

    public FlexTable createScheduleWidget() {
        return new FlexTable() {{
            for (int i = 0, scheduleOptionDescriptionsLength = scheduleOptionDescriptions.length; i < scheduleOptionDescriptionsLength; i++) {
                String scheduleOptionDescription = scheduleOptionDescriptions[i];
                setText(i, 0, scheduleOptionDescription);
                setWidget(i, 1, createWidget(i));
            }
        }};
    }

    public Widget createWidget(final int... features) {
        return new TextBox() {{
            if (features.length > 0)
                setTitle(scheduleOptionDescriptions[features[0]]);
        }};
    }

    public int[] getSchedule(FlexTable w, CaptionPanel captionPanel, int... values) {

        int[] v = values.length > 0 ? values : new int[scheduleOptionDescriptions.length];

        for (int i = 0; i < scheduleOptionDescriptions.length; i++) {
            String scheduleOptionDescription = scheduleOptionDescriptions[i];
            final Widget widget = w.getWidget(i, 1);
            if (widget instanceof ListBox) {
                ListBox listBox = (ListBox) widget;
                v[i] = listBox.getSelectedIndex();
            } else if (widget instanceof TextBox) {
                try {
                    TextBox textBox = (TextBox) widget;
                    v[i] = Integer.parseInt(textBox.getText());
                } catch (NumberFormatException e) {
                    if (captionPanel != null) {
                        final String s = captionPanel.getCaptionHTML() + "<br/>" +
                                scheduleOptionDescription + " must be a numerical value greater than 0";
                        captionPanel.setCaptionHTML(s);
                    }
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return null;
                }
            }
        }
        return v;
    }

    public void setSchedule(FlexTable w, int... values) {
        int[] v = values.length > 0 ? values : new int[scheduleOptionDescriptions.length];

        for (int i = 0; i < scheduleOptionDescriptions.length; i++) {

            final Widget widget = w.getWidget(i, 1);

            if (widget instanceof ListBox) {
                ListBox listBox = (ListBox) widget;
                listBox.setSelectedIndex(i);
            } else if (widget instanceof TextBox) {
                TextBox textBox = (TextBox) widget;
                textBox.setText(String.valueOf(v[i]));
            }
        }
    }
}
