package sasj.abconj2html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO segments and fields should be alphanumeric with _- and nothing more
public class UrlPattern {
    private List<Segment> segments;
    private LinkedHashMap<String, QueryParameter> queryParameters;

    public UrlPattern() {
        this.segments = new ArrayList<>(0);
        this.queryParameters = new LinkedHashMap<>(0);
    }

    public UrlPattern addPath(String path) {
        this.segments.add(new Constant(path));
        return this;
    }

    public UrlPattern addPathVariable(Class<?> clazz) {
        return this.addPathVariable(clazz.getSimpleName());
    }

    public UrlPattern addPathVariable(String variableName) {
        this.segments.add(new Dynamic(variableName));
        return this;
    }

    public UrlPattern addQueryParameter(String name) {
        this.queryParameters.put(name, new QueryParameter(name));
        return this;
    }

//    public UrlPath(String pattern) {
//
//    }

//    public URL build(Object values) {
//        Map<String, String> valueMap = values;
//    }

    public String getValue() {
        String path = segments.stream()
            .map(Segment::getValue)
            .collect(Collectors.joining());
        if (queryParameters.size() == 0) {
            return path;
        } else {
            String queryString = queryParameters.values().stream()
                .map(QueryParameter::getValue)
                .collect(Collectors.joining("&"));
            return path + "\\?" + queryString;
        }
    }

    public URI buildURI(Object object) {
        return buildURI(toMap(object));
    }

    public URI buildURI(Map<String, Object> data) {
        String path = segments.stream()
            .map(segment -> {
                if (segment instanceof Constant) {
                    return segment.getValue();
                } else if (segment instanceof Dynamic) {
                    String name = ((Dynamic) segment).getVariableName();
                    if (data.containsKey(name)) {
                        return ((Dynamic) segment).getValue(data.get(name));
                    } else {
                        throw new RuntimeException("Missing value for URI variable '" + name + "'.");
                    }
                } else {
                    throw new RuntimeException("Unknown segment type " + segment.getClass().getName());
                }
            })
            .collect(Collectors.joining());
        String queryString = queryParameters.values().stream()
            .map(queryParameter -> {
                if (data.containsKey(queryParameter.getField())) {
                    return queryParameter.getValue(data.get(queryParameter.getField()));
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.joining("&"));
        try {
            return new URI(path + "?" + queryString);
        } catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAntMatcherPattern() {
        String path = segments.stream()
            .map(Segment::getValue)
            .collect(Collectors.joining());
        return path;
    }

    public interface Segment {
        String getValue();
    }

    private Map<String, Object> toMap(Object object) {
        return null;
    }

    public static class Constant implements Segment {
        private final String value;

        public Constant(String value) {
            Objects.requireNonNull(value);
            if (value.length() == 0 || value.charAt(0) != '/') {
                value = '/' + value;
            }
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public static class Dynamic implements Segment {
        private final String variableName;
        private final String prefix;
        private final String suffix;

        public Dynamic(String variableName) {
            this(variableName, "");
        }

        public Dynamic(String variableName, String prefix) {
            this(variableName, prefix, "");
        }

        public Dynamic(String variableName, String prefix, String suffix) {
            this.variableName = variableName;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getVariableName() {
            return variableName;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        @Override
        public String getValue() {
            return String.format("/%s{%s}%s", prefix, variableName, suffix);
        }

        public String getValue(Object value) {
            return String.format("/%s%s", prefix, value.toString(), suffix);
        }
    }

    public static class QueryParameter {
        private String field;
        private List<String> values;

        public QueryParameter(String name) {
            this.field = name;
            this.values = Collections.singletonList(name);
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return field + "=" + values.stream()
                .map(value -> String.format("{%s}", value))
                .collect(Collectors.joining(","));
        }

        public String getValue(Object ...objects) {
            return field + "=" + Arrays.stream(objects)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        }
    }
}
