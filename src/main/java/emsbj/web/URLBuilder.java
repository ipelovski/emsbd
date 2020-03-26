package emsbj.web;

import emsbj.config.WebMvcConfig;
import emsbj.util.Pair;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class URLBuilder {
    private static final Map<String, Method> methods = new HashMap<>();
    private final Class<?> controllerType;
    private final String requestMappingName;
    private List<Object> uriParams;
    private Map<String, Object> namedURIParams;
    private Map<String, List<String>> queryParams;
    private HttpServletRequest request;

    public URLBuilder(Class<?> controllerType, String requestMappingName) {
        Objects.requireNonNull(controllerType);
        Objects.requireNonNull(requestMappingName);
        this.controllerType = controllerType;
        this.requestMappingName = requestMappingName;
        this.uriParams = new LinkedList<>();
        this.namedURIParams = new LinkedHashMap<>();
        this.queryParams = new HashMap<>(0);
    }

    public static String get(Class<?> controllerType, String requestMappingName, Object... uriVariableValues) {
        return new URLBuilder(controllerType, requestMappingName)
            .gatherNamedURIParams()
            .namedURIParams(valuesToMap(uriVariableValues))
//            .uriParams(uriVariableValues)
            .build();
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

    public URLBuilder namedURIParams(Map<String, Object> values) {
        namedURIParams.putAll(values);
        return this;
    }

    public URLBuilder namedURIParams(String key, Object value, Object... rest) {
        namedURIParams.put(key, value);
        return namedURIParams(valuesToMap(rest));
    }

    public URLBuilder gatherNamedURIParams() {
        buildUriVariableValues(controllerType);
        return this;
    }

    public URLBuilder setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public String build() {
        Method method = getMethod(controllerType, requestMappingName);
        UriComponentsBuilder uriComponentsBuilder = fromMethod(controllerType, method);
        Object[] uriVariableValues = uriParams.toArray();
        return uriComponentsBuilder
            .queryParams(CollectionUtils.toMultiValueMap(queryParams))
            .build()
            .expand(namedURIParams)
            .expand(uriVariableValues)
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
        if (request != null) {
            return ServletUriComponentsBuilder.fromServletMapping(request);
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

    private void buildUriVariableValues(Class<?> controllerType) {
        for (Map.Entry<Predicate<Class<?>>, Supplier<Pair<String, Object>>> entry :
            WebMvcConfig.pathPrefixValueSuppliers.entrySet()
        ) {
            if (entry.getKey().test(controllerType)) {
                Pair<String, Object> uriVariable = entry.getValue().get();
                namedURIParams.put(uriVariable.getFirst(), uriVariable.getSecond());
            }
        }
    }

    private static Map<String, Object> valuesToMap(Object... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("values must be even");
        }
        Map<String, Object> valuesMap = new LinkedHashMap<>(values.length / 2);
        for (int i = 0; i < values.length; i += 2) {
            valuesMap.put(values[i].toString(), values[i + 1]);
        }
        return valuesMap;
    }
}
