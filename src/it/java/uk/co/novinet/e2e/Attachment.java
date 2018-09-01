package uk.co.novinet.e2e;

public class Attachment {
    private String filename;
    private byte[] bytes;
    private String contentType;

    public Attachment(String filename, byte[] bytes, String contentType) {
        this.filename = filename;
        this.bytes = bytes;
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getContentType() {
        return contentType;
    }
}
