package sasj.abconj2html;

import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import java.util.HashMap;

public class HomeController {
    public static class IndexInput extends WebController.Input {
        public String name;
        public int times;
        public String self;
    }

    @Component
    public class Index extends WebController<IndexInput> {
        {
            setSupportedMethods(METHOD_GET);
        }

        @Override
        public UrlPattern getUrlPattern() {
            return new UrlPattern()
                .addPath("home2")
                .addPathVariable("name")
                .addQueryParameter("times");
        }

        @Override
        public void configure(ExpressionUrlAuthorizationConfigurer<?>.AuthorizedUrl authorizedUrl) {
            authorizedUrl.permitAll();
        }

        @Override
        protected HomeView buildView(IndexInput input) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", input.name);
            data.put("times", input.times);
            input.self = getUrlPattern().buildURI(data).toString();
            return new HomeView(input);
        }
    }
}
