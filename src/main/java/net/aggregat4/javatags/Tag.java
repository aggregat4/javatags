package net.aggregat4.javatags;

import java.io.IOException;
import java.io.Writer;

public interface Tag {
    void render(Appendable appendable) throws IOException;
}
