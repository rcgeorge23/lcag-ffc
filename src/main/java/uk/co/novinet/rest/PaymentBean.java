package uk.co.novinet.rest;

import java.math.BigDecimal;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class PaymentBean {
    BigDecimal amount;
    String stripeToken;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
