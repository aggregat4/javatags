package net.aggregat4.javatags;

import java.io.IOException;
import java.io.StringWriter;

public class TagUtils {
    public static String toString(Tag tag) {
        StringBuilder sb = new StringBuilder();
        try {
            tag.render(sb);
        } catch (IOException e) {
            throw new IllegalStateException("StringBuilder should not cause IOException");
        }
        return sb.toString();
    }
}
