package emsbj;

import emsbj.user.JournalUserDetailsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@SpringBootApplication
public class Application {

    public static final String[] supportedLocalesArray = { "en", "bg" };
    public static final Collection<String> supportedLocales = Arrays.asList(supportedLocalesArray);
    public static final Locale defaultLocale = Locale.forLanguageTag("en");
    public static final String localePathParam = "/{locale:en|bg}/";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JournalUserDetailsService userDetailsService() {
        return new JournalUserDetailsService();
    }

    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new UrlLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/labels");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
