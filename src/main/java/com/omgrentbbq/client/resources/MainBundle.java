package com.omgrentbbq.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface MainBundle extends ClientBundle {

    public static final MainBundle INSTANCE = GWT.create(MainBundle.class);


    ImageResource logo();

    public interface siteResource extends CssResource {
        String groupBox();
    }

    siteResource site();
}
