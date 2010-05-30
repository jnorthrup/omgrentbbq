package com.omgrentbbq.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource ;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 28, 2010
 * Time: 12:14:22 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MainBundle extends ClientBundle {

    public static final MainBundle INSTANCE = GWT.create(MainBundle.class);


    ImageResource logo(); 
 public interface siteResource extends CssResource{String groupBox();}

    siteResource site();
}
