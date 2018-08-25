package uk.co.novinet.service;

import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Service
public class AsynchMailSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchMailSenderService.class);

    @Value("${smtpHost}")
    private String smtpHost;

    @Value("${smtpPort}")
    private int smtpPort;

    @Value("${smtpUsername}")
    private String smtpUsername;

    @Value("${smtpPassword}")
    private String smtpPassword;

    @Value("${thankYouForYourContributionEmailSourceUrl}")
    private String thankYouForYourContributionEmailSourceUrl;

    @Value("${thankYouForYourContributionEmailSubject}")
    private String thankYouForYourContributionEmailSubject;

    @Value("${newMemberEmailSourceUrl}")
    private String newMemberEmailSourceUrl;

    @Value("${newMemberEmailSubject}")
    private String newMemberEmailSubject;

    @Value("${emailFromName}")
    private String emailFromName;

    @Value("#{'${bccEmailRecipients}'.split(',')}")
    private List<String> bccEmailRecipients;

    @Autowired
    private MemberService memberService;

    @Scheduled(initialDelayString = "${sendEmailsInitialDelayMilliseconds}", fixedRateString = "${sendEmailsIntervalMilliseconds}")
    public void sendEmails() {
        LOGGER.info("Going to check for emails to send...");
        for (Payment payment : memberService.getFfcContributionsAwaitingEmails()) {
            try {
                sendFollowUpEmail(payment);
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.error("Unable to send email for payment: {}", payment, e);
            }
        }
        LOGGER.info("Going back to sleep.");
    }

    public void sendFollowUpEmail(Payment payment) throws Exception {
        Email email = new Email();

        email.setFromAddress(emailFromName, smtpUsername);

        if (bccEmailRecipients != null && !bccEmailRecipients.isEmpty()) {
            bccEmailRecipients.forEach(bccEmailRecipient -> {
                if (bccEmailRecipient != null && !bccEmailRecipient.trim().isEmpty()) {
                    email.addRecipient(bccEmailRecipient, bccEmailRecipient, MimeMessage.RecipientType.BCC);
                }
            });
        }

        email.addRecipient(payment.getEmailAddress(), payment.getEmailAddress(), MimeMessage.RecipientType.TO);
        applySubjectAndText(payment, email);

        LOGGER.info("Going to try sending email to new ffc contributor {}", payment);
        new Mailer(smtpHost, smtpPort, smtpUsername, smtpPassword, TransportStrategy.SMTP_TLS).sendMail(email);
        LOGGER.info("Email successfully sent to new ffc contributor {}", payment);
    }

    private void applySubjectAndText(Payment payment, Email email) throws IOException {
        switch (payment.getPaymentType()) {
            case ANONYMOUS:
            case EXISTING_LCAG_MEMBER:
                email.setTextHTML(replaceTokens(retrieveEmailBodyHtmlFromGoogleDocs(thankYouForYourContributionEmailSourceUrl), payment));
                email.setSubject(thankYouForYourContributionEmailSubject);
            default:
                email.setTextHTML(replaceTokens(retrieveEmailBodyHtmlFromGoogleDocs(newMemberEmailSourceUrl), payment));
                email.setSubject(newMemberEmailSubject);
        }
    }

    private String replaceTokens(String emailTemplate, Payment payment) {
        return emailTemplate
                .replace("$NAME", payment.getFirstName() + " " + payment.getLastName())
                .replace("$AMOUNT", DecimalFormat.getCurrencyInstance(Locale.UK).format(payment.getAmount()))
                .replace("$PAYMENT_REFERENCE", payment.getReference());
    }

    private String retrieveEmailBodyHtmlFromGoogleDocs(String emailSourceUrl) throws IOException {
        try (Scanner scanner = new Scanner(new URL(emailSourceUrl).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}