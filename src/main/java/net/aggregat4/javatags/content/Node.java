package net.aggregat4.javatags.content;

import java.io.IOException;

public interface Node {
    void render(Appendable appendable) throws IOException;
}
