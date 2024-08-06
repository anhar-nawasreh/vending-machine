package com.progressoft.samples;

import java.math.BigDecimal;
import java.util.*;

public class Money {
    public static final Money Zero = new Money(0);
    public static final Money OnePiaster = new Money(0.01);
    public static final Money FivePiasters = new Money(0.05);
    public static final Money TenPiasters = new Money(0.10);
    public static final Money TwentyFivePiasters = new Money(0.25);
    public static final Money FiftyPiasters = new Money(0.5);
    public static final Money OneDinar = new Money(1);
    public static final Money FiveDinars = new Money(5);
    public static final Money TenDinars = new Money(10);
    public static final Money TwentyDinars = new Money(20);
    public static final Money FiftyDinars = new Money(50);

    private final BigDecimal amount;

    private final Map<Money, Integer> denominationCount = new HashMap<>();


    private Money(double amount) {
        this.amount = new BigDecimal(amount);
    }


    private Money(double amount, Map<Money, Integer> denominationCount) {
        this.amount = new BigDecimal(amount);
        for (Map.Entry<Money, Integer> entry : denominationCount.entrySet()) {
            Money keyCopy = new Money(entry.getKey().amount());
            this.denominationCount.put(keyCopy, entry.getValue());
        }
    }

    public static Money sum(Money... items) {
        BigDecimal sum = new BigDecimal(0);
        for (Money money : items)
            if ( money != null )
                sum = sum.add(money.getAmount());

        return new Money(sum.doubleValue());

    }

    public double amount() {
        return amount.doubleValue();
    }

    public Money times(int count) {
        if ( count < 0 )
            throw new IllegalArgumentException("Can't process negative count as " + count);

        double moneyAmount = this.getAmount().multiply(BigDecimal.valueOf(count)).doubleValue();
        denominationCount.put(this, denominationCount.getOrDefault(this, 0) + count);
        return new Money(moneyAmount, denominationCount);

    }

    public Money plus(Money other) {
        if ( other == null )
            throw new IllegalArgumentException("Cant process null parameter");

        double moneyAmount = other.getAmount().add(getAmount()).doubleValue();
        denominationCount.put(other, denominationCount.getOrDefault(other, 0) + 1);
        denominationCount.put(this, denominationCount.getOrDefault(this, 0) + 1);
        return new Money(moneyAmount, denominationCount);

    }

    public Money minus(Money other) {
        if ( other == null )
            throw new IllegalArgumentException("Cant process null parameter");


        if ( Double.compare(this.amount(), other.amount()) < 0 )
            throw new IllegalArgumentException(String.format("Can't subtract %s from %s", this.amount(), other.amount()));

        BigDecimal neededChange = this.getAmount().subtract(other.getAmount());

        if ( isZero(neededChange) )
            return new Money(0);


        if ( canProvideChange(other, neededChange) )
            return new Money(getAmount().subtract(other.getAmount()).doubleValue());

        throw new IllegalArgumentException("No change to return");
    }

    private boolean canProvideChange(Money other, BigDecimal neededChange) {
        for (Money money : denominationCount.keySet()) {
            if ( neededChange.compareTo(money.getAmount()) < 0 || isZero(money.getAmount()) )
                continue;
            BigDecimal neededCount = BigDecimal.valueOf(Math.min(denominationCount.get(money), neededChange.divide(money.getAmount()).doubleValue()));

            neededChange = neededChange.subtract(neededCount.multiply(money.getAmount()));

            if ( isZero(neededChange) )
                return true;
        }
        return false;
    }

    private BigDecimal getAmount() {
        return amount;
    }

    private boolean isZero(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }


    @Override
    public String toString() {
        return String.format("%.2f", amount());
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null )
            return false;
        if ( obj.getClass() != Money.class )
            return false;

        return ((Money) obj).getAmount().compareTo(getAmount()) == 0;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(Double.doubleToLongBits(amount()));
    }

}

