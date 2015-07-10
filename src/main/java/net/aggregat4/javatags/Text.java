package net.aggregat4.javatags;

import java.io.IOException;

public class Text implements Tag {
    private final String content;

    public Text(String content) {
        this.content = content;
    }

    public void render(Appendable appendable) throws IOException {
        appendable.append(content);
    }

}
