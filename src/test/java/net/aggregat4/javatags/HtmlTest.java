package net.aggregat4.javatags;

import static net.aggregat4.javatags.attributes.Attributes.*;
import net.aggregat4.javatags.tags.TagUtils;
import org.junit.Test;

import java.io.IOException;

import static net.aggregat4.javatags.tags.Tags.*;
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
                "<h1>header1</h1>" +
                "<p id=\"foo\">para1</p>" +
                "<p id=\"foo\">para1</p>" +
                "<p id=\"bar\">para2</p>" +
                "<p id=\"baz\">para3</p>" +
                "<p id=\"qux\">para4</p>" +
                "<p hidden>para5</p>" +
                "</body></html>",
            TagUtils.toString(
                html(
                    head(
                        title("foo")),
                    body(
                        h1("header1"),
                        // not sure what approach I like better
                        p(id("foo")).content(
                            text("para1")),
                        // this is new, with a singular utility method and some generics we have a type safe way to make generic attributes
                        // is this convenient enough?
                        p(attr(id, "foo")).content(
                            text("para1")),
                        p("para2").attr(id("bar")),
                        p(attrs(id("baz")), "para3"),
                        // the following is compact a bit the best of both worlds (multiple attributes and multiple bits
                        // of content, but it is maybe also not so clear. better to go for clarity? Is clarity just
                        // using attr instead of a or is the arr also bad? Just use attrs?
                        p(arr(a(id, "qux")), "para4"),
                        p(attrs(attr(hidden)), "para5") // boolean attributes should not require a value, setting them is setting them to true
                    ))));
    }

}
