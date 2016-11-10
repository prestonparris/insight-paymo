package com.prestonparris.paymo.models;

public enum TrustedStatus {
    trusted,
    untrusted
    ;

    public static String get(boolean isTrusted) {
        return (isTrusted) ? trusted.name()
                           : untrusted.name();
    }
}
