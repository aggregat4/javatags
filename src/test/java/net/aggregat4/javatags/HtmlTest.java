package net.aggregat4.javatags;

import org.junit.Test;

import java.io.IOException;

import static net.aggregat4.javatags.Attributes.*;
import static net.aggregat4.javatags.Tags.*;
import static org.junit.Assert.assertEquals;

public class HtmlTest {

    @Test
    public void htmlWithJustTags() throws IOException {
        assertEquals(
            "<html><head><title>foo</title></head><body><h1>header1</h1><p>para</p></body></html>",
            TagUtils.toString(
                html(
                    head(
                        title("foo")),
                    body(
                        h1("header1"),
                        p("para")
                    ))));
    }

    @Test
    public void htmlWithTagsAndAttributes() throws IOException {
        assertEquals(
            "<html><head><title>foo</title></head><body>" +
                "<h1>header1</h1><p id=\"foo\">para1</p><p id=\"bar\">para2</p><p id=\"baz\">para3</p></body></html>",
            TagUtils.toString(
                html(
                    head(
                        title("foo")),
                    body(
                        h1("header1"),
                        // not sure what approach I like better
                        p(id("foo")).content(text("para1")),
                        p("para2").attr(id("bar")),
                        p(attr(id("baz")), "para3")
                    ))));
    }

}
