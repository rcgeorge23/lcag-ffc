package uk.co.novinet.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.co.novinet.rest.PaymentStatus;
import uk.co.novinet.rest.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private Long id;
    private Long userId;
    private String username;
    private String membershipToken;
    private String hash;
    private String reference;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private BigDecimal amount;
    private Instant date;
    private String stripeToken;
    private PaymentStatus paymentStatus = PaymentStatus.NEW;
    private PaymentType paymentType;
    private ContributionType contributionType;

    public Payment() {}

    public Payment(
            Long id,
            Long userId,
            String username,
            String membershipToken,
            String hash,
            String reference,
            String firstName,
            String lastName,
            String emailAddress,
            BigDecimal amount,
            Instant date,
            String stripeToken,
            PaymentStatus paymentStatus,
            PaymentType paymentType,
            ContributionType contributionType) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.membershipToken = membershipToken;
        this.hash = hash;
        this.reference = reference;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.amount = amount;
        this.date = date;
        this.stripeToken = stripeToken;
        this.paymentStatus = paymentStatus;
        this.paymentType = paymentType;
        this.contributionType = contributionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMembershipToken() {
        return membershipToken;
    }

    public void setMembershipToken(String membershipToken) {
        this.membershipToken = membershipToken;
    }

    public ContributionType getContributionType() {
        return contributionType;
    }

    public void setContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
    }
}
