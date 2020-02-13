package emsbj;

import emsbj.admin.AdminGradeController;
import emsbj.admin.AdminSchoolClassController;
import emsbj.admin.AdminStudentController;
import emsbj.admin.AdminTeacherController;
import emsbj.admin.AdminUserController;
import emsbj.config.WebMvcConfig;
import emsbj.controller.LocalizedController;
import emsbj.user.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class Extensions {
    @Autowired
    private MessageSource messageSource;
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
            return getUrl(AdminUserController.class, WebMvcConfig.listPath);
        }

        public String user(User user) {
            return getUrl(AdminUserController.class, AdminUserController.userIdPath);
        }

        public String grades() {
            return getUrl(AdminGradeController.class, WebMvcConfig.listPath);
        }

        public String addGrade() {
            return gradesUrlBuilder.build() + addUrl;
        }

        public String schoolClasses() {
            return getUrl(AdminSchoolClassController.class, WebMvcConfig.listPath);
        }

        public String addSchoolClass() {
            return getUrl(AdminSchoolClassController.class, WebMvcConfig.addPath);
        }

        public String teachers() {
            return getUrl(AdminTeacherController.class, WebMvcConfig.listPath);
        }

        public String students() {
            return getUrl(AdminStudentController.class, WebMvcConfig.listPath);
        }

        public String users2() {
            return MvcUriComponentsBuilder.fromMappingName("" + WebMvcConfig.listName)
                .buildAndExpand("en");
        }

        public String userDetails(User user) {
            return getUrl(AdminUserController.class, AdminUserController.userIdPath,
                Collections.singletonMap("userId", user.getId()));
//            return MvcUriComponentsBuilder.fromMappingName("AdminUserController#"+ WebMvcConfig.detailsName)
//                .buildAndExpand(user.getId(), "en");
        }

        private String getUrl(Class<?> aClass, String requestMappingPath) {
            return getUrl(aClass, requestMappingPath, new HashMap<>());
        }

        private String getUrl(Class<?> aClass, String requestMappingPath, Map<String, Object> uriVariableValues) {
            Method method = findMethod(aClass, requestMappingPath);
            Object[] parameterTypes = method.getParameterTypes();
//            UriComponentsBuilder uriComponentsBuilder =
//                MvcUriComponentsBuilder.fromMethod(aClass, method, (Object[]) method.getParameterTypes());
//            Object[] finalUriVariablesValues;
//            if (LocalizedController.class.isAssignableFrom(aClass)) {
//                finalUriVariablesValues = new Object[uriVariableValues.length + 1];
//                finalUriVariablesValues[0] = LocaleContextHolder.getLocale().toLanguageTag();
//                System.arraycopy(uriVariableValues, 0,
//                    finalUriVariablesValues, 1, uriVariableValues.length);
//            } else {
//                finalUriVariablesValues = uriVariableValues;
//            }
            Map<String, Object> finalUriVariableValues = new HashMap<>(1);
            finalUriVariableValues.put("locale", LocaleContextHolder.getLocale().toLanguageTag());
            finalUriVariableValues.putAll(uriVariableValues);
//            uriVariableValues.put("locale", LocaleContextHolder.getLocale().toLanguageTag());
            UriComponentsBuilder uriComponentsBuilder = fromMethodInternal(aClass, method, finalUriVariableValues);
            return uriComponentsBuilder.build()
                .expand("en", uriVariableValues.getOrDefault("userId", null))
//                .expand(finalUriVariableValues)
                .toUriString();
        }
        private Map<String, Method> methods = new HashMap<>();
        private Method findMethod(Class<?> aClass, String requestMappingPath) {
            String key = getKey(aClass, requestMappingPath);
            return methods.computeIfAbsent(key, s -> {
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (methodIsMappedToPath(method, requestMappingPath)) {
                        return method;
                    }
                }
                throw new RuntimeException(String.format(
                    "Cannot find method in %s with path %s",
                    aClass.getSimpleName(), requestMappingPath));
            });
        }
        private boolean methodIsMappedToPath(Method method, String requestMappingPath) {
            RequestMapping requestMapping =
                AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (requestMapping != null) {
                for (String path : requestMapping.path()) {
                    if (path.equals(requestMappingPath)) {
                        return true;
                    }
                }
            }
            return false;
        }
        private String getKey(Class<?> aClass, String requestMappingPath) {
            return aClass.getSimpleName() + "::" + requestMappingPath;
        }
        private UriComponentsBuilder fromMethodInternal(
            Class<?> controllerType, Method method, Map<String, Object> uriVars) {
            UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
            String prefix = WebMvcConfig.localePathParam;//getPathPrefix(controllerType);
            builder.path(prefix);
//            String typePath = getClassMapping(controllerType);
            RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(controllerType, RequestMapping.class);
            String typePath = mapping.path()[0];
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            String methodPath = requestMapping.path()[0];
//            String methodPath = getMethodMapping(method);
            String path = new AntPathMatcher().combine(typePath, methodPath);
            builder.path(path);
//            return applyContributors(builder, method, args);
            return builder;//.uriVariables(uriVars);
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
