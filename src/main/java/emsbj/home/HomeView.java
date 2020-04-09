package emsbj.home;

import emsbj.web.J2HtmlView;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

import static j2html.TagCreator.*;

public class HomeView extends J2HtmlView<HomeController.IndexInput> {
    public HomeView(HomeController.IndexInput model) {
        super(model);
    }

    @Override
    protected Tag<?> buildView() throws Exception {
        ContainerTag[] tags = new ContainerTag[model.times];
        for (int i = 0; i < model.times; i++) {
            tags[i] = div("Hello " + model.name + "!");
        }
        return html(
            head(
                title("Home")
            ),
            body(
                div(a("self").withHref(model.self)),
                join(tags)
            )
        );
    }
}
