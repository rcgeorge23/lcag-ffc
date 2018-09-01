package uk.co.novinet.e2e;

import org.apache.commons.io.IOUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.co.novinet.e2e.TestUtils.getTextFromMessage;

public class StaticMessage {
    private String contentType;
    private String content;
    private String subject;
    private String from;
    private List<Attachment> attachments = new ArrayList();

    public StaticMessage(Message message) throws IOException, MessagingException {
        this.contentType = message.getContentType();
        this.content = getTextFromMessage(message);
        this.subject = message.getSubject();
        this.from = ((InternetAddress) message.getFrom()[0]).getAddress();

        if (message.getContentType().contains("multipart")) {
            Multipart multiPart = (Multipart) message.getContent();
            for (int i = 0; i < multiPart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    attachments.add(new Attachment(part.getFileName(), IOUtils.toByteArray(part.getInputStream()), part.getContentType()));
                }
            }
        }
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public List<Attachment> getAttachments() { return attachments; }
}
