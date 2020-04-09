package emsbj.config;

import emsbj.util.Pair;
import emsbj.web.UrlLocaleInterceptor;
import emsbj.controller.LocalizedController;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    public static final String[] supportedLocalesArray = { "bg", "en" };
    public static final Collection<String> supportedLocales = Arrays.asList(supportedLocalesArray);
    public static final Locale defaultLocale = Locale.forLanguageTag(supportedLocalesArray[0]);
    public static final String defaultLocalePath = "/" + defaultLocale.toLanguageTag();
    public static final String localePathName = "locale";
    public static final String localePathParam = "/{locale:en|bg}";
    public static final Pattern localePathPattern = Pattern.compile("/(en|bg)");
    public static final String objectIdPathParam = "/{id:\\d+}";
    public static final String objectIdParamName = "id";
    public static final String addPath = "/add";
    public static final String editPath = "/edit";
    public static final String removePath = "/remove";
    public static final String indexName = "index";
    public static final String listName = "list";
    public static final String addName = "add";
    public static final String editName = "edit";
    public static final String removeName = "remove";
    public static final String detailsName = "details";
    public static final String noProfilePicture = "/img/blank-user.png";
    public static final Map<String, Predicate<Class<?>>> pathPrefixes;
    public static final Map<Predicate<Class<?>>, Supplier<Pair<String, Object>>> pathPrefixValueSuppliers;
    static {
        Predicate<Class<?>> isLocalizedController =
            LocalizedController.class::isAssignableFrom;

        pathPrefixes = new LinkedHashMap<>();
        pathPrefixes.put(localePathParam, isLocalizedController);

        pathPrefixValueSuppliers = new LinkedHashMap<>();
        pathPrefixValueSuppliers.put(isLocalizedController, () ->
            new Pair<>(localePathName, LocaleContextHolder.getLocale().toLanguageTag()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        UrlLocaleInterceptor urlLocaleInterceptor = new UrlLocaleInterceptor();
        registry.addInterceptor(urlLocaleInterceptor)
            .addPathPatterns(urlLocaleInterceptor.getPathPatterns());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/WEB-INF/static/favicon.ico");
        registry.addResourceHandler("/img/**")
            .addResourceLocations("classpath:/WEB-INF/static/img/");
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/WEB-INF/static/css/");
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/WEB-INF/static/js/");
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver(ApplicationContext applicationContext) {
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("classpath:/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : pathPrefixes.entrySet()) {
            configurer.addPathPrefix(entry.getKey(), entry.getValue());
        }
    }

//    @Autowired
//    public void setHandlerMapping(RequestMappingHandlerMapping mapping)
//        throws NoSuchMethodException {
//
//        RequestMappingInfo info = RequestMappingInfo
//            .paths("/admin/grade-names").methods(RequestMethod.GET).build();
//
//        Method method = AdminGradeController.class.getMethod("list", Model.class);
//
////        mapping.registerMapping(info, "adminGradeController", method);
//    }
}
