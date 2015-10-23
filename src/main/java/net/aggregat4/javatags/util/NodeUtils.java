package net.aggregat4.javatags.util;

import net.aggregat4.javatags.content.Node;

import java.io.IOException;

public class NodeUtils {

    public static String toString(Node node) {
        StringBuilder sb = new StringBuilder();
        try {
            node.render(sb);
        } catch (IOException e) {
            throw new IllegalStateException("StringBuilder should not cause IOException");
        }
        return sb.toString();
    }

}
