package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class URLBuilder {
    private static final Map<String, Method> methods = new HashMap<>();
    private final Optional<HttpServletRequest> request;
    private final Class<?> controllerType;
    private final String requestMappingName;
    private List<Object> uriParams;
    private Map<String, List<String>> queryParams;

    public URLBuilder(Class<?> controllerType, String requestMappingName) {
        this(null, controllerType, requestMappingName);
    }

    public URLBuilder(HttpServletRequest request, Class<?> controllerType, String requestMappingName) {
        this.request = Optional.ofNullable(request);
        Objects.requireNonNull(controllerType);
        Objects.requireNonNull(requestMappingName);
        this.controllerType = controllerType;
        this.requestMappingName = requestMappingName;
        this.uriParams = buildUriVariableValues(controllerType);
        this.queryParams = new HashMap<>(0);
    }

    public static String get(Class<?> controllerType, String requestMappingName, Object... uriVariableValues) {
        return new URLBuilder(controllerType, requestMappingName)
            .uriParams(uriVariableValues).build();
    }

    public static String get(HttpServletRequest request, Class<?> controllerType, String requestMappingName, Object... uriVariableValues) {
        return new URLBuilder(request, controllerType, requestMappingName)
            .uriParams(uriVariableValues).build();
    }

    public URLBuilder queryParam(String name, Object... values) {
        List<String> paramValues = Arrays.stream(values)
            .map(value -> value != null ? value.toString() : null)
            .collect(Collectors.toList());
        queryParams.put(name, paramValues);
        return this;
    }

    public <T> URLBuilder queryParam(
        String name, T object, Function<T, ?> mapper,
        Predicate<T> predicate
    ) {
        if (predicate.test(object)) {
            List<String> paramValues = Stream.of(mapper.apply(object))
                .map(value -> value != null ? value.toString() : null)
                .collect(Collectors.toList());
            queryParams.put(name, paramValues);
        }
        return this;
    }

    public URLBuilder uriParams(Object... values) {
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
