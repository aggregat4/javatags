package net.aggregat4.javatags;

import net.aggregat4.javatags.util.NodeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static net.aggregat4.javatags.content.Html.*;
import static org.junit.Assert.assertEquals;

public class HtmlTest {

    @Test
    public void simpleHtml() throws IOException {
        assertEquals(
            "<html>" +
                "<head>" +
                    "<title>foo</title>" +
                "</head>" +
                "<body>" +
                    "<h1>header1</h1>" +
                    "<p>para<img class=\"imgclass\"></p>" +
                "</body>" +
            "</html>",
        NodeUtils.toString(
            html(
                head(
                    title(t("foo"))
                ),
                body(
                    h1(t("header1")),
                    p(t("para"), img(classAttr("imgclass")))
                )
            )
        )
        );
    }

}
