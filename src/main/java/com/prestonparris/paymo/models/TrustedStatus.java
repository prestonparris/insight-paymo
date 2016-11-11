package com.prestonparris.paymo.models;

/**
 * The enum used to represent
 * the validity of each payment
 */
public enum TrustedStatus {
    trusted,
    untrusted
    ;

    public static String get(boolean isTrusted) {
        return (isTrusted) ? trusted.name()
                           : untrusted.name();
    }
}
