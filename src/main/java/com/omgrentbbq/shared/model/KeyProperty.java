package com.omgrentbbq.shared.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface KeyProperty {
 public         String value();

}
