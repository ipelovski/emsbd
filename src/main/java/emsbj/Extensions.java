package emsbj;

import emsbj.admin.AdminGradeController;
import emsbj.admin.AdminSchoolClassController;
import emsbj.admin.AdminStudentController;
import emsbj.admin.AdminTeacherController;
import emsbj.admin.AdminUserController;
import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class Extensions {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private AdminUrls adminUrls;

    protected Extensions() {
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

    public String lu(String url) {
        return localizedUrl(url);
    }

    public String localizedUrl(String url) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url should not be blank");
        }
        if (url.charAt(0) != '/') {
            throw new IllegalArgumentException("non absolute urls are not supported");
        }
        Locale locale = LocaleContextHolder.getLocale();
        return "/" + locale.toLanguageTag() + url;
    }

    public AdminUrls au() {
        return getAdminUrls();
    }

    public AdminUrls getAdminUrls() {
        if(adminUrls == null) {
            adminUrls = new AdminUrls();
        }
        return adminUrls;
    }

    public static class AdminUrls {
        private static final EntitiesUrlBuilder<SchoolYear> schoolYearsUrlBuilder =
            new EntitiesUrlBuilder<>("/school-years");
        private static final EntitiesUrlBuilder<Term> termsUrlBuilder =
            new EntitiesUrlBuilder<>("/terms");
        private static final EntitiesUrlBuilder<Subject> subjectsUrlBuilder =
            new EntitiesUrlBuilder<>("/subjects");
        private static final EntitiesUrlBuilder<Grade> gradesUrlBuilder =
            new EntitiesUrlBuilder<>("/grades");
        private static final String addUrl = "/add";

        public String schoolYears() {
            return schoolYearsUrlBuilder.build();
        }

        public String schoolYear(SchoolYear schoolYear) {
            return schoolYearsUrlBuilder.entity(schoolYear).build();
        }

        public String term(Term term) {
            return termsUrlBuilder.entity(term).build();
        }

        public String addSubject(Term term) {
            assert !term.isNew();
            return subjectsUrlBuilder.build() + addUrl + "?term=" + term.getId();
        }

        public String addSubject() {
            return subjectsUrlBuilder.build() + addUrl;
        }

        public String users() {
            return getUrl(AdminUserController.class, WebMvcConfig.listName);
        }

        public String user(User user) {
            return getUrl(AdminUserController.class, WebMvcConfig.detailsName, user.getId());
        }

        public String grades() {
            return getUrl(AdminGradeController.class, WebMvcConfig.listName);
        }

        public String addGrade() {
            return getUrl(AdminGradeController.class, WebMvcConfig.addName);
        }

        public String schoolClasses() {
            return getUrl(AdminSchoolClassController.class, WebMvcConfig.listName);
        }

        public String addSchoolClass() {
            return getUrl(AdminSchoolClassController.class, WebMvcConfig.addName);
        }

        public String teachers() {
            return getUrl(AdminTeacherController.class, WebMvcConfig.listName);
        }

        public String students() {
            return getUrl(AdminStudentController.class, WebMvcConfig.listName);
        }

        private String getUrl(Class<?> controllerType, String requestMappingName, Object... methodUriVariableValues) {
            Method method = getMethod(controllerType, requestMappingName);
            UriComponentsBuilder uriComponentsBuilder = fromMethod(controllerType, method);
            Object[] uriVariableValues = buildUriVariableValues(controllerType, methodUriVariableValues);
            return uriComponentsBuilder.build()
                .expand(uriVariableValues)
                .toUriString();
        }
        private Map<String, Method> methods = new HashMap<>();
        private Method getMethod(Class<?> controllerType, String requestMappingName) {
            String key = getKey(controllerType, requestMappingName);
            return methods.computeIfAbsent(key, s -> findMethod(controllerType, requestMappingName));
        }
        private Method findMethod(Class<?> controllerType, String requestMappingName) {
            Method[] methods = controllerType.getDeclaredMethods();
            RequestMapping[] methodRequestMappings = new RequestMapping[methods.length];
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                RequestMapping requestMapping =
                    AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                if (requestMapping != null
                    && requestMapping.name().equals(requestMappingName)) {
                    return method;
                }
                methodRequestMappings[i] = requestMapping;
            }
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equals(requestMappingName)
                    && methodRequestMappings[i] != null) {
                    return method;
                }
            }
            throw new RuntimeException(String.format(
                "Cannot find method in %s with name %s",
                controllerType.getSimpleName(), requestMappingName));
        }
        private String getKey(Class<?> aClass, String requestMappingName) {
            return aClass.getSimpleName() + "::" + requestMappingName;
        }
        // based on MvcUriComponentsBuilder.fromMethodInternal
        private UriComponentsBuilder fromMethod(Class<?> controllerType, Method method) {
            UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
            String prefix = getPathPrefix(controllerType);
            builder.path(prefix);
            String typePath = getRequestMappingPath(controllerType);
            String methodPath = getRequestMappingPath(method);
            String path = new AntPathMatcher().combine(typePath, methodPath);
            builder.path(path);
            return builder;
        }
        private String getPathPrefix(Class<?> controllerType) {
            for (Map.Entry<String, Predicate<Class<?>>> entry : WebMvcConfig.pathPrefixes.entrySet()) {
                if (entry.getValue().test(controllerType)) {
                    return entry.getKey();
                }
            }
            return "";
        }
        private String getRequestMappingPath(AnnotatedElement annotatedElement) {
            RequestMapping requestMapping = AnnotatedElementUtils.
                findMergedAnnotation(annotatedElement, RequestMapping.class);
            if (requestMapping != null && requestMapping.path().length > 0) {
                return requestMapping.path()[0];
            } else {
                return "";
            }
        }
        private Object[] buildUriVariableValues(Class<?> controllerType, Object... methodUriVariableValues) {
            List<Object> uriVariableValues = new ArrayList<>(
                methodUriVariableValues.length + WebMvcConfig.pathPrefixValueSuppliers.size());
            for (Map.Entry<Predicate<Class<?>>, Supplier<Optional<?>>> entry :
                WebMvcConfig.pathPrefixValueSuppliers.entrySet()) {
                if (entry.getKey().test(controllerType)) {
                    entry.getValue().get().ifPresent(uriVariableValues::add);
                }
            }
            Collections.addAll(uriVariableValues, methodUriVariableValues);
            return uriVariableValues.toArray();
        }

        private static class EntitiesUrlBuilder<T extends JournalPersistable> {
            private final String path;
            EntitiesUrlBuilder(String path) {
                this.path = path;
            }
            EntityUrlBuilder<T> entity(T entity) {
                return new EntityUrlBuilder<>(this, entity);
            }
            String build() {
                return "/admin" + path;
            }
        }
        private static class EntityUrlBuilder<T extends JournalPersistable> {
            private final EntitiesUrlBuilder<T> parent;
            private final T entity;
            EntityUrlBuilder(EntitiesUrlBuilder<T> parent, T entity) {
                assert !entity.isNew();
                this.parent = parent;
                this.entity = entity;
            }
            String build() {
                return parent.build() + "/" + entity.getId();
            }
        }
    }
}
