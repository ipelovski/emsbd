package sasj.web;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public class RedirectingAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return getTargetUrl(request, response);
    }

    public String getTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = super.determineTargetUrl(request, response);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(targetUrl);
        URI uri = uriComponentsBuilder.build().toUri();
        if (uri.getAuthority() != null) {
            throw new RuntimeException("Dangerous uri " + uri.toString());
        } else {
            return uri.toString();
        }
    }
}
