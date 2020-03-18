package emsbj.web;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Locale;

public class RedirectingLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    private UrlLocaleResolver localeResolver = new UrlLocaleResolver();

    public RedirectingLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        Locale locale = localeResolver.resolveLocale(request);
        String redirectUrl = super.buildRedirectUrlToLoginPage(request, response, authException);
        URI uri;
        try {
            uri = new URI(redirectUrl);
        } catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme(uri.getScheme());
        uriComponentsBuilder.host(uri.getHost());
        uriComponentsBuilder.port(uri.getPort());
        uriComponentsBuilder.path(locale.toLanguageTag() + uri.getPath());
        uriComponentsBuilder.query(uri.getQuery());
        uriComponentsBuilder.fragment(uri.getFragment());
        String requestUri = request.getRequestURI();
        if (request.getQueryString() != null) {
            requestUri += "?" + request.getQueryString();
        }
        uriComponentsBuilder.queryParam("requested", encode(requestUri));
        return uriComponentsBuilder.build().toUriString();
    }

    private String encode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
