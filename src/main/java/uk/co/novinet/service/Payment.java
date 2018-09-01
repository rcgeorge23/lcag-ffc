package uk.co.novinet.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.format.number.CurrencyStyleFormatter;
import uk.co.novinet.rest.PaymentStatus;
import uk.co.novinet.rest.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String country;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private Instant invoiceCreated;
    private Instant paymentReceived;
    private String stripeToken;
    private PaymentStatus paymentStatus = PaymentStatus.NEW;
    private String errorDescription;
    private PaymentType paymentType;
    private String paymentMethod;
    private ContributionType contributionType;
    private String guid;

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
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String country,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            BigDecimal vatRate,
            BigDecimal vatAmount,
            Instant invoiceCreated,
            Instant paymentReceived,
            String stripeToken,
            PaymentStatus paymentStatus,
            String errorDescription,
            PaymentType paymentType,
            String paymentMethod,
            ContributionType contributionType,
            String guid) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.membershipToken = membershipToken;
        this.hash = hash;
        this.reference = reference;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.grossAmount = grossAmount;
        this.netAmount = netAmount;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.invoiceCreated = invoiceCreated;
        this.paymentReceived = paymentReceived;
        this.stripeToken = stripeToken;
        this.paymentStatus = paymentStatus;
        this.errorDescription = errorDescription;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.contributionType = contributionType;
        this.guid = guid;
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

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Instant getInvoiceCreated() {
        return invoiceCreated;
    }

    public void setInvoiceCreated(Instant invoiceCreated) {
        this.invoiceCreated = invoiceCreated;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getUiFriendlyInvoiceCreatedDate() {
        return uiFriendlyDate(invoiceCreated);
    }

    public String getUiFriendlyPaymentReceivedDate() {
        return uiFriendlyDate(invoiceCreated);
    }

    private String uiFriendlyDate(Instant date) {
        if (date == null) {
            return "";
        }

        return DateTimeFormatter.ofPattern("dd MMM yyyy").format(ZonedDateTime.ofInstant(date, ZoneId.of("GMT")));
    }

    public String getUiFriendlyGrossAmount() {
        return uiFriendlyMoneyString(grossAmount);
    }

    public String getUiFriendlyNetAmount() {
        return uiFriendlyMoneyString(netAmount);
    }

    public String getUiFriendlyVatAmount() {
        return uiFriendlyMoneyString(vatAmount);
    }

    private String uiFriendlyMoneyString(BigDecimal moneyAmount) {
        if (moneyAmount == null) {
            return "";
        }

        return new CurrencyStyleFormatter().print(moneyAmount, Locale.UK);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public Instant getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(Instant paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
