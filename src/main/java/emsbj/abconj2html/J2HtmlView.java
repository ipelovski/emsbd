package emsbj.abconj2html;

import j2html.tags.Tag;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class J2HtmlView<M> extends AbstractView {
    protected M model;

    protected J2HtmlView() {
        this(null);
    }

    protected J2HtmlView(M model) {
        this.model = model;
        ApplicationContextHolder.getBeanFactory().autowireBean(this);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setContentType("text/html;charset=" + StandardCharsets.UTF_8.name());
        Tag<?> html = buildView();
        if (html == null) {
            throw new RuntimeException("A J2HtmlView.buildView() implementation should return a non null Tag instance.");
        }
        html.render(httpServletResponse.getWriter());
    }

    protected abstract Tag<?> buildView() throws Exception;
}
