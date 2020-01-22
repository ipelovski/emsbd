package emsbj;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UrlLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String requestURI = httpServletRequest.getRequestURI();
        int nextSlashIndex = requestURI.indexOf("/", 1);
        if (nextSlashIndex > -1) {
            String pathFragment = requestURI.substring(1, nextSlashIndex);
            if (Application.supportedLocales.contains(pathFragment)) {
                return Locale.forLanguageTag(pathFragment);
            }
        }
        return Application.defaultLocale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
        httpServletRequest.setAttribute("locale", locale);
    }
}
