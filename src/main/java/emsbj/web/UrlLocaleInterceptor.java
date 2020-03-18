package emsbj.web;

import emsbj.config.WebMvcConfig;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UrlLocaleInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            throw new IllegalStateException("No locale resolver");
        }
        Locale locale = localeResolver.resolveLocale(request);
        localeResolver.setLocale(request, response, locale);
        return true;
    }

    public String[] getPathPatterns() {
        return WebMvcConfig.supportedLocales.stream()
            .map(locale -> String.format("/%s/*", locale))
            .toArray(String[]::new);
    }
}
