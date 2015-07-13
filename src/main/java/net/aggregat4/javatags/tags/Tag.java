package net.aggregat4.javatags.tags;

import java.io.IOException;

public interface Tag {
    void render(Appendable appendable) throws IOException;
}
