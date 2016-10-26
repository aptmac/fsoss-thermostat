package com.redhat.thermostat.stap.gui;

import com.redhat.thermostat.shared.locale.Translate;
/**
 * Created by jmatsuok on 24/10/16.
 */
public enum LocaleResources {

    STAP,
    STAP_X_AXIS,
    STAP_Y_AXIS,
    STAP_HITS,
    STAP_MISSES,
    STAP_FOREIGN
    ;

    static final String RESOURCE_BUNDLE = "com.redhat.thermostat.stap.gui.internal.strings";

    public static Translate<LocaleResources> createLocalizer() {
        return new Translate<>(RESOURCE_BUNDLE, LocaleResources.class);
    }
}