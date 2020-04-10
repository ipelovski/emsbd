package emsbj.abconj2html;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// maybe it is better to extend ParameterizableViewController
public abstract class WebController<T extends WebController.Input> extends AbstractController implements SecuredUrlController, Controller {
    @Autowired
    protected DefaultFormattingConversionService conversionService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        J2HtmlView<?> view = buildView(buildInput(httpServletRequest, httpServletResponse));
        return new ModelAndView(view);
    }

    protected abstract J2HtmlView<?> buildView(T input);

    public static class Input {
        public HttpServletRequest request;
        public HttpServletResponse response;
    }

    // for forms and possibly json
    public static class DataInput<D> extends Input {
        public D data;
    }

    private T buildInput(HttpServletRequest request, HttpServletResponse response) {
        T input = createInput();
        fillInput(input, request, response);
        return input;
    }

    private T createInput() {
        Class<T> inputClass = (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
        try {
            Constructor<T> constructor = inputClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("The class of the Input should have a constructor " +
                "without arguments and the class should not be an inner non-static one.", e);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillInput(T input, HttpServletRequest request, HttpServletResponse response) {
        input.request = request;
        input.response = response;
        Set<Attribute> attributes = getAttributes(input.getClass());
        Map<String, String> variables = extractQueryString(request);
        AntPathMatcher pathMatcher = new AntPathMatcher();
        Map<String, String> uriVariables = pathMatcher.extractUriTemplateVariables(
            getUrlPattern().getAntMatcherPattern(), request.getRequestURI());
        variables.putAll(uriVariables);
        for (Attribute attribute : attributes) {
            if (variables.containsKey(attribute.getName())) {
                String stringValue = variables.get(attribute.getName());
                Object value = conversionService.convert(stringValue, attribute.getType());
                attribute.setValue(input, value);
            }
        }
    }

    private Map<String, String> extractQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.length() == 0) {
            return new HashMap<>(0);
        }
        String[] queryParameters = queryString.split("&");
        Map<String, String> result = new HashMap<>(queryParameters.length);
        for (String queryParameter : queryParameters) {
            String[] parts = queryParameter.split("=");
            if (parts.length == 1) {
                result.put(parts[0], null);
            } else if (parts.length == 2) {
                result.put(parts[0], parts[1]);
            } else {
                throw new RuntimeException("Invalid query string");
            }
        }
        return result;
    }

    private Set<Attribute> getAttributes(Class<?> clazz) {
        Set<Attribute> attributes = new HashSet<>();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                attributes.add(new FieldAttribute(field));
            }
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")
                && method.getParameterCount() == 1
                && hasGetter(methods, method.getName().substring(3))
            ) {
                attributes.add(new PropertyAttribute(method));
            }
        }
        return attributes;
    }

    private boolean hasGetter(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals("get" + name) && method.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    public interface Attribute {
        String getName();
        Class<?> getType();
        void setValue(Object instance, Object value);
    }

    public static class FieldAttribute implements Attribute {
        private Field field;

        public FieldAttribute(Field field) {
            this.field = field;
        }

        @Override
        public String getName() {
            return field.getName();
        }

        @Override
        public Class<?> getType() {
            return field.getType();
        }

        @Override
        public void setValue(Object instance, Object value) {
            try {
                field.set(instance, value);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class PropertyAttribute implements Attribute {
        private Method setMethod;
        private String name;
        private Class<?> type;

        public PropertyAttribute(Method setMethod) {
            this.setMethod = setMethod;
            this.name = Character.toLowerCase(setMethod.getName().charAt(3)) + setMethod.getName().substring(4);
            this.type = setMethod.getParameterTypes()[0];
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public void setValue(Object instance, Object value) {
            try {
                setMethod.invoke(instance, value);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
