package emsbj.abconj2html;

import emsbj.controller.SecuredController;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public interface SecuredUrlController extends UrlController, SecuredController {
    @Override
    default void configure(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        configure(registry.antMatchers(getUrlPattern().getAntMatcherPattern()));
    }

    void configure(ExpressionUrlAuthorizationConfigurer<?>.AuthorizedUrl authorizedUrl);
}
