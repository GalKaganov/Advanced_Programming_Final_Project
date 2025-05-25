package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String text) {
        this.asText = text;
        this.data = text.getBytes();
        this.asDouble = parseDouble(text);
        this.date = new Date();
    }

    public Message(byte[] bytes) {
        this(new String(bytes));
    }

    public Message(double value) {
        this(Double.toString(value));
    }

    public String toString() {
        return String.format("Message: text='%s', double=%f, date=%s", asText, asDouble, date);
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
