package org.udesa.tp1.model;

import java.util.List;

public abstract class GiftCard {
    protected int balance;
    protected String ownerName;

    public abstract GiftCard claim(  String userName );
    public abstract GiftCard charge( String userName, int price, Clock clock);

    public abstract List<Expense> getExpenses( String userName );
    public abstract int           getBalance(  String userName );
}
