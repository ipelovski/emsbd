package emsbj.user;

import emsbj.util.Util;
import htmlflow.DynamicHtml;
import j2html.attributes.Attr;
import j2html.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.util.HtmlUtils;
import org.xmlet.htmlapifaster.GlobalAttributes;
import org.xmlet.htmlapifaster.TextGroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import static j2html.TagCreator.*;

public class ProfileView extends AbstractView {
    private Util util;
    private User user;

    @Autowired
    public ProfileView(Util util, User user) {
        this.util = util;
        this.user = user;
    }

    private <T> T of(T tag, Consumer<? super T> consumer) {
        consumer.accept(tag);
        return tag;
    }

    private <T extends Tag<?>> Consumer<T> col(int col) {
        return tag -> tag.withClass("col col-" + col);
    }

    private <T extends GlobalAttributes<?,?>> Consumer<T> colb(int col) {
        return a -> a.attrClass("col col-" + col);
    }

    private <T extends TextGroup<?, ?>> Consumer<T> text(String text) {
        return a -> a.text(HtmlUtils.htmlEscape(text));
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
//        renderHtmlFlow(httpServletResponse);
        renderJ2Html(httpServletResponse);
    }

    private void renderHtmlFlow(HttpServletResponse httpServletResponse) throws Exception {
        DynamicHtml<User> view = DynamicHtml.view((v, user) ->
            v
                .html()
                .head()
                .meta().attrCharset(StandardCharsets.UTF_8.name()).__()
                .__()
                .body()
                .div().of(colb(8)).text(user.getUsername()).__()
                .div().text(HtmlUtils.htmlEscape("<a href=google.com>google</a>")).__()
                .div().of(text("<script>alert('hi!')</script>")).__()
                .__() //body
                .__());
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpServletResponse.setHeader("Content-Type", "text/html; charset=utf-8");
        PrintStream printStream = new PrintStream(
            httpServletResponse.getOutputStream(), false, StandardCharsets.UTF_8.name());
        view.setPrintStream(printStream).write(user);
    }

    private void renderJ2Html(HttpServletResponse httpServletResponse) throws Exception {
        Consumer<Tag<?>> col4 = col(4);
        httpServletResponse.setHeader("Content-Type", "text/html; charset=utf-8");
        html(
            head(
                meta().withCharset(StandardCharsets.UTF_8.name()),
                title(util.capitalize("user.profile"))
            ),
            body(
                div(attrs(".flex.flex-wrap")).with(
                    of(input(), col4),
                    of(div(), col4).with(
                        label().withClass("label").attr(Attr.FOR, "username").withText(util.capitalize("user.username"))),
                    of(div(), col4).with(
                        span(attrs("#username")).withText(user.getUsername()))
                )
            )
        )
            .render(httpServletResponse.getWriter());
    }
}
