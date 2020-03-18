package emsbj.web;

import emsbj.config.WebMvcConfig;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UrlLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String requestURI = httpServletRequest.getRequestURI();
        int nextSlashIndex = requestURI.indexOf("/", 1);
        int length = nextSlashIndex > -1 ? nextSlashIndex : requestURI.length();
        String pathFragment = requestURI.substring(1, length);
        if (WebMvcConfig.supportedLocales.contains(pathFragment)) {
            return Locale.forLanguageTag(pathFragment);
        } else {
            return WebMvcConfig.defaultLocale;
        }
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
        httpServletRequest.setAttribute("locale", locale);
    }
}
