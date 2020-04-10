package emsbj.user;

import emsbj.util.Util;
import emsbj.abconj2html.J2HtmlView;
import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static j2html.TagCreator.*;

public class ProfileView extends J2HtmlView<User> {
    @Autowired
    private Util util;
    private User user;

    @Autowired
    public ProfileView(User user) {
        super(user);
    }

    private <T> T of(T tag, Consumer<? super T> consumer) {
        consumer.accept(tag);
        return tag;
    }

    private <T extends Tag<?>> Consumer<T> col(int col) {
        return tag -> tag.withClass("col col-" + col);
    }

    @FunctionalInterface
    interface Label {
        ContainerTag of(String text, String forElement);
    }

    @Override
    protected Tag<?> buildView() throws Exception {
        Consumer<Tag<?>> col4 = col(4);
        Consumer<Tag<?>> col8 = col(8);
        Consumer<Tag<?>> col12 = col(12);
        Supplier<ContainerTag> divCol4 = () -> of(div(), col4);
        Supplier<ContainerTag> divCol8 = () -> of(div(), col8);
        Supplier<ContainerTag> divCol12 = () -> of(div(), col12);

        Label label = (text, forElement) -> label(text).attr(Attr.FOR, forElement).withClass("label");
        return html(
            head(
                meta().withCharset(StandardCharsets.UTF_8.name()),
                title(util.c("user.profile"))),
            body(
                div(attrs(".pt4")).with(),
                div(attrs(".flex.flex-wrap")).with(
                    divCol4.get().with(
                        label.of(util.c("user.username"), "username")),
                    divCol8.get().with(
                        span(attrs("#username")).withText(user.getUsername())),
                    divCol12.get().with(
                        a().withHref("").withText(util.c("user.changePassword"))),
                    divCol4.get().with(
                        label.of(util.c("user.role"), "role")),
                    divCol8.get().with(
                        span(attrs("#role")).withText(util.c("user.role." +
                            user.getRole().name().toLowerCase()))),
                    divCol4.get().with(
                        label.of(util.c("user.email"), "email")),
                    divCol8.get().with(
                        span(attrs("#email")).withText(user.getEmail())),
                    divCol4.get().with(
                        label.of(util.c("user.firstName"), "firstName")),
                    divCol8.get().with(
                        span(attrs("#firstName")).withText(user.getPersonalInfo().getFirstName())),
                    divCol4.get().with(
                        label.of(util.c("user.middleName"), "middleName")),
                    divCol8.get().with(
                        span(attrs("#middleName")).withText(user.getPersonalInfo().getMiddleName())),
                    divCol4.get().with(
                        label.of(util.c("user.lastName"), "lastName")),
                    divCol8.get().with(
                        span(attrs("#lastName")).withText(user.getPersonalInfo().getLastName())),
                    divCol4.get().with(
                        label.of(util.c("user.gender"), "gender")),
                    divCol8.get().with(
                        span(attrs("#gender")).withText(util.c("user.gender." +
                            user.getPersonalInfo().getGender()))),
                    divCol4.get().with(
                        label.of(util.c("user.bornAt"), "bornAt")),
                    divCol8.get().with(
                        span(attrs("#bornAt")).withText(user.getPersonalInfo().getBornAt() != null ?
                            user.getPersonalInfo().getBornAt().toString() : "")),
                    divCol4.get().with(
                        label.of(util.c("user.address"), "address")),
                    divCol8.get().with(
                        span(attrs("#address")).withText(user.getPersonalInfo().getAddress() != null ?
                            user.getPersonalInfo().getAddress() : ""))
                )
            )
        );
    }
}
