package sasj.user;

import sasj.web.UrlBuilder;
import sasj.web.UrlLocaleResolver;
import sasj.config.WebMvcConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
public class UserUrls {
    public String signIn() {
        return UrlBuilder.get(UserController.class, UserController.signIn);
    }

    public String signIn(HttpServletRequest request) {
        Locale locale = new UrlLocaleResolver().resolveLocale(request);
        return new UrlBuilder(UserController.class, UserController.signIn)
            .setRequest(request)
            .namedURIParams(WebMvcConfig.localePathName, locale)
            .build();
    }

    public String signInRole() {
        return UrlBuilder.get(UserController.class, UserController.signInRole);
    }

    public String signUp() {
        return UrlBuilder.get(UserController.class, UserController.signUp);
    }

    public String signOut() {
        return UrlBuilder.get(UserController.class, UserController.signOut);
    }

    public String profile() {
        return UrlBuilder.get(UserController.class, UserController.profile);
    }
}
