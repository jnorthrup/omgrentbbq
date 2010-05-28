package com.omgrentbbq.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Baseline extends Composite {

	private static BaselineUiBinder uiBinder = GWT
			.create(BaselineUiBinder.class);

	interface BaselineUiBinder extends UiBinder<Widget, Baseline> {
	}

	public Baseline() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
