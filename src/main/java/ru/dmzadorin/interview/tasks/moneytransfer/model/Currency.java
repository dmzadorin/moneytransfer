package ru.dmzadorin.interview.tasks.moneytransfer.model;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public enum Currency {
    USD,
    EUR,
    GBP,
    NOT_PRESENT;

    public static Currency from(String currency) {
        for (Currency curr : Currency.values()) {
            if (curr.name().equalsIgnoreCase(currency)) {
                return curr;
            }
        }
        return NOT_PRESENT;
    }
}
