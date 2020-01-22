package emsbj.config;

import emsbj.UrlLocaleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        UrlLocaleInterceptor urlLocaleInterceptor = new UrlLocaleInterceptor();
        registry.addInterceptor(urlLocaleInterceptor)
            .addPathPatterns(urlLocaleInterceptor.getPathPatterns());
    }
}
