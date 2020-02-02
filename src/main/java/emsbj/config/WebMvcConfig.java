package emsbj.config;

import emsbj.UrlLocaleInterceptor;
import emsbj.admin.AdminGradeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        UrlLocaleInterceptor urlLocaleInterceptor = new UrlLocaleInterceptor();
        registry.addInterceptor(urlLocaleInterceptor)
            .addPathPatterns(urlLocaleInterceptor.getPathPatterns());
    }

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping)
        throws NoSuchMethodException {

        RequestMappingInfo info = RequestMappingInfo
            .paths("/admin/grade-names").methods(RequestMethod.GET).build();

        Method method = AdminGradeController.class.getMethod("list", Model.class);

//        mapping.registerMapping(info, "adminGradeController", method);
    }
}
