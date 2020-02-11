package emsbj;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RedirectingLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public RedirectingLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        String redirectUrl = super.buildRedirectUrlToLoginPage(request, response, authException);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(redirectUrl);
        uriComponentsBuilder.queryParam("requested", encode(request.getRequestURI()));
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
