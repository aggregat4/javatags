package net.aggregat4.javatags.content;

import java.io.IOException;

public class Text implements Content, Html.CanBeAChildFor {
    private final String value;

    public Text(String value) {
        this.value = value;
    }

    @Override
    public void render(Appendable appendable) throws IOException {
        appendable.append(value);
    }
}
