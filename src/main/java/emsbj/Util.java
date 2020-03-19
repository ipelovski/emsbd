package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class Util {
    @Autowired
    private MessageSource messageSource;

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public Collection<Locale> getSupportedLocales() {
        return WebMvcConfig.supportedLocales.stream()
            .map(Locale::forLanguageTag)
            .collect(Collectors.toList());
    }

    public String c(String label, String... args) {
        return capitalize(label, args);
    }

    public String capitalize(String label, String... args) {
        return StringUtils.capitalize(localize(label, args));
    }

    public String l(String label, String... args) {
        return localize(label, args);
    }

    public String localize(String label, String... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(label, args, locale);
    }

    public LocalDate getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.toLocalDate().minusDays(dateTime.getDayOfWeek().ordinal());
    }

    public String localizeCurrentRequestURL(Locale locale) {
        HttpServletRequest currentRequest = getCurrentRequest();
        StringBuffer currentRequestURL = currentRequest.getRequestURL();
        if (currentRequest.getQueryString() != null) {
            currentRequestURL
                .append('?')
                .append(currentRequest.getQueryString());
        }
        Matcher matcher = WebMvcConfig.localePathPattern.matcher(currentRequestURL);
        if (matcher.find()) {
            return matcher.replaceFirst("/" + locale.toLanguageTag());
        } else {
            return currentRequestURL.toString();
        }
    }

    // from ServletUriComponentsBuilder.getCurrentRequest
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        return ((ServletRequestAttributes)attrs).getRequest();
    }
}
