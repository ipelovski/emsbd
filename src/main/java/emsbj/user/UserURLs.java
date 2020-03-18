package emsbj.user;

import emsbj.web.URLBuilder;
import emsbj.web.UrlLocaleResolver;
import emsbj.config.WebMvcConfig;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
public class UserURLs {
    public String signIn() {
        return URLBuilder.get(UserController.class, UserController.signIn);
    }

    public String signIn(HttpServletRequest request) {
        Locale locale = new UrlLocaleResolver().resolveLocale(request);
        return new URLBuilder(UserController.class, UserController.signIn)
            .setRequest(request)
            .namedURIParams(WebMvcConfig.localePathName, locale)
            .build();
    }

    public String signInRole() {
        return URLBuilder.get(UserController.class, UserController.signInRole);
    }

    public String signUp() {
        return URLBuilder.get(UserController.class, UserController.signUp);
    }

    public String signOut() {
        return new URLBuilder(UserController.class, UserController.signOut)
            .namedURIParams(WebMvcConfig.localePathName, LocaleContextHolder.getLocale())
            .build();
    }

    public String profile() {
        return URLBuilder.get(UserController.class, UserController.profile);
    }
}
