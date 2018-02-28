package ru.dmzadorin.interview.tasks.moneytransfer.model;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public enum Currency {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    NOT_PRESENT("");

    private final String alias;

    Currency(String alias) {
        this.alias = alias;
    }

    public static Currency from(String currency) {
        for (Currency curr : Currency.values()) {
            if (curr.alias.equalsIgnoreCase(currency)) {
                return curr;
            }
        }
        return NOT_PRESENT;
    }

    public String getAlias() {
        return alias;
    }
}
