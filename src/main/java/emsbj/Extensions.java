package emsbj;

import emsbj.admin.AdminController;
import emsbj.admin.AdminCourseController;
import emsbj.admin.AdminGradeController;
import emsbj.admin.AdminRoomController;
import emsbj.admin.AdminScheduleController;
import emsbj.admin.AdminSchoolClassController;
import emsbj.admin.AdminSchoolYearController;
import emsbj.admin.AdminStudentController;
import emsbj.admin.AdminSubjectController;
import emsbj.admin.AdminTeacherController;
import emsbj.admin.AdminTermController;
import emsbj.admin.AdminUserController;
import emsbj.config.WebMvcConfig;
import emsbj.user.User;
import emsbj.user.UserController;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Extensions {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private Urls urls;
    private AdminUrls adminUrls;

    protected Extensions() {
    }

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

    public String lu(String url) {
        return localizedUrl(url);
    }

    public String localizedUrl(String url) {
        Locale locale = LocaleContextHolder.getLocale();
        return localizedUrl(url, locale);
    }

    public String localizedUrl(String url, Locale locale) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url should not be blank");
        }
        if (url.charAt(0) != '/') {
            throw new IllegalArgumentException("non absolute urls are not supported");
        }
        return "/" + locale.toLanguageTag() + url;
    }

    public String localizeCurrentRequestURL(Locale locale) {
        HttpServletRequest currentRequest = getCurrentRequest();
        String currentRequestURL = currentRequest.getRequestURL()
            .append('?')
            .append(currentRequest.getQueryString())
            .toString();
        Matcher matcher = Pattern.compile(WebMvcConfig.localePathPattern).matcher(currentRequestURL);
        if (matcher.find()) {
            return matcher.replaceFirst("/" + locale.toLanguageTag());
        } else {
            return currentRequestURL;
        }
    }

    public Urls u() {
        return getUrls();
    }

    public Urls getUrls() {
        if(urls == null) {
            urls = new Urls();
        }
        return urls;
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

    private String getUrl(Class<?> controllerType, String requestMappingName, Object... uriVariableValues) {
        return new UrlBuilder(controllerType, requestMappingName)
            .uriParams(uriVariableValues).build();
    }

    private String getUrl(HttpServletRequest request, Class<?> controllerType, String requestMappingName, Object... uriVariableValues) {
        return new UrlBuilder(request, controllerType, requestMappingName)
            .uriParams(uriVariableValues).build();
    }

    // from ServletUriComponentsBuilder.getCurrentRequest
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        return ((ServletRequestAttributes)attrs).getRequest();
    }

    public class Urls {
        private HttpServletRequest request;

        public Urls() {
            this.request = null;
        }

        public Urls(HttpServletRequest request) {
            this.request = request;
        }

        public String home() {
            return getUrl(HomeController.class, WebMvcConfig.indexName);
        }

        public String blob(Blob blob) {
            return getUrl(BlobController.class, WebMvcConfig.detailsName, blob.getId());
        }

        public String profilePicture(User user) {
            if (user.getPersonalInfo().getPicture() != null) {
                return blob(user.getPersonalInfo().getPicture());
            } else {
                return WebMvcConfig.noProfilePicture;
            }
        }

        public String uploadProfilePicture(User user) {
            return getUrl(BlobController.class, BlobController.uploadProfilePicture, user.getId());
        }

        public String signIn() {
            return getUrl(request, UserController.class, UserController.signIn);
        }

        public String signIn(Locale locale) {
            return localizedUrl("/sign-in", locale);
        }

        public String signInRole() {
            return getUrl(UserController.class, UserController.signInRole);
        }

        public String signUp() {
            return getUrl(UserController.class, UserController.signUp);
        }

        public String signOut() {
            return localizedUrl("/sign-out");
        }

        public String profile() {
            return getUrl(UserController.class, UserController.profile);
        }

        public String course(Course course) {
            return getUrl(CourseController.class, WebMvcConfig.detailsName, course.getId());
        }

        public String addNote() {
            return getUrl(NoteController.class, WebMvcConfig.addName);
        }

        public String schedule() {
            return getUrl(CourseController.class, CourseController.schedule);
        }
    }

    public class AdminUrls {

        public String adminIndex() {
            return getUrl(AdminController.class, WebMvcConfig.indexName);
        }

        public String schoolYears() {
            return getUrl(AdminSchoolYearController.class, WebMvcConfig.listName);
        }

        public String addSchoolYear() {
            return getUrl(AdminSchoolYearController.class, WebMvcConfig.addName);
        }

        public String terms() {
            return getUrl(AdminTermController.class, WebMvcConfig.listName);
        }

        public String termsBySchoolYear(SchoolYear schoolYear) {
            return new UrlBuilder(AdminTermController.class, WebMvcConfig.listName)
                .queryParam(AdminTermController.schoolYearQueryParam, schoolYear.getId())
                .build();
        }

        public String term(Term term) {
            return getUrl(AdminTermController.class, WebMvcConfig.detailsName, term.getId());
        }

        public String addTermWithSchoolYear(SchoolYear schoolYear) {
            return getUrl(AdminTermController.class, WebMvcConfig.addName, schoolYear.getId());
        }

        public String subjects() {
            return getUrl(AdminSubjectController.class, WebMvcConfig.listName);
        }

        public String addSubject() {
            return getUrl(AdminSubjectController.class, WebMvcConfig.addName);
        }

        public String users() {
            return getUrl(AdminUserController.class, WebMvcConfig.listName);
        }

        public String addUser() {
            return getUrl(AdminUserController.class, WebMvcConfig.addName);
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

        public String schoolClass(SchoolClass schoolClass) {
            return getUrl(AdminSchoolClassController.class, WebMvcConfig.detailsName, schoolClass.getId());
        }

        public String teachers() {
            return getUrl(AdminTeacherController.class, WebMvcConfig.listName);
        }

        public String teacher(Teacher teacher) {
            return getUrl(AdminTeacherController.class, WebMvcConfig.detailsName, teacher.getId());
        }

        public String selectFormMasterFragment() {
            return getUrl(AdminTeacherController.class, AdminTeacherController.selectFormMasterFragment);
        }

        public String teacherList() {
            return getUrl(AdminTeacherController.class, AdminTeacherController.teacherList);
        }

        public String students() {
            return getUrl(AdminStudentController.class, WebMvcConfig.listName);
        }

        public String student(Student student) {
            return getUrl(AdminStudentController.class, WebMvcConfig.detailsName, student.getId());
        }

        public String studentList() {
            return getUrl(AdminStudentController.class, AdminStudentController.studentList);
        }

        public String weeklySlots() {
            return getUrl(AdminScheduleController.class, AdminScheduleController.weeklySlotsList);
        }

        public String rooms() {
            return getUrl(AdminRoomController.class, WebMvcConfig.listName);
        }

        public String addRoom() {
            return getUrl(AdminRoomController.class, WebMvcConfig.addName);
        }

        public String courses() {
            return getUrl(AdminCourseController.class, WebMvcConfig.listName);
        }
    }

    private static class UrlBuilder {
        private static final Map<String, Method> methods = new HashMap<>();
        private final Optional<HttpServletRequest> request;
        private final Class<?> controllerType;
        private final String requestMappingName;
        private List<Object> uriParams;
        private Map<String, List<String>> queryParams;

        public UrlBuilder(Class<?> controllerType, String requestMappingName) {
            this(null, controllerType, requestMappingName);
        }

        public UrlBuilder(HttpServletRequest request, Class<?> controllerType, String requestMappingName) {
            this.request = Optional.ofNullable(request);
            Objects.requireNonNull(controllerType);
            Objects.requireNonNull(requestMappingName);
            this.controllerType = controllerType;
            this.requestMappingName = requestMappingName;
            this.uriParams = buildUriVariableValues(controllerType);
            this.queryParams = new HashMap<>(0);
        }

        public UrlBuilder queryParam(String name, Object... values) {
            List<String> paramValues = Arrays.stream(values)
                .map(value -> value != null ? value.toString() : null)
                .collect(Collectors.toList());
            queryParams.put(name, paramValues);
            return this;
        }

        public UrlBuilder uriParams(Object... values) {
            Collections.addAll(uriParams, values);
            return this;
        }

        public String build() {
            Method method = getMethod(controllerType, requestMappingName);
            UriComponentsBuilder uriComponentsBuilder = fromMethod(controllerType, method);
            Object[] uriVariableValues = uriParams.toArray();
            return uriComponentsBuilder
                .queryParams(CollectionUtils.toMultiValueMap(queryParams))
                .buildAndExpand(uriVariableValues)
                .toUriString();
        }

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
            UriComponentsBuilder builder = fromRequest();
            String prefix = getPathPrefix(controllerType);
            builder.path(prefix);
            String typePath = getRequestMappingPath(controllerType);
            String methodPath = getRequestMappingPath(method);
            String path = new AntPathMatcher().combine(typePath, methodPath);
            builder.path(path);
            return builder;
        }

        private UriComponentsBuilder fromRequest() {
            if (request.isPresent()) {
                return ServletUriComponentsBuilder.fromRequest(request.get());
            } else {
                return ServletUriComponentsBuilder.fromCurrentServletMapping();
            }
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

        private List<Object> buildUriVariableValues(Class<?> controllerType, Object... methodUriVariableValues) {
            List<Object> uriVariableValues = new ArrayList<>(
                methodUriVariableValues.length + WebMvcConfig.pathPrefixValueSuppliers.size());
            for (Map.Entry<Predicate<Class<?>>, Supplier<Optional<?>>> entry :
                WebMvcConfig.pathPrefixValueSuppliers.entrySet()) {
                if (entry.getKey().test(controllerType)) {
                    entry.getValue().get().ifPresent(uriVariableValues::add);
                }
            }
            Collections.addAll(uriVariableValues, methodUriVariableValues);
            return uriVariableValues;
        }
    }
}
