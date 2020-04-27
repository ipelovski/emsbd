package sasj.controller.teacher.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sasj.config.WebMvcConfig;
import sasj.controller.Breadcrumb;
import sasj.util.Util;
import sasj.web.UrlBuilder;
import sasj.web.ViewInfo;

@Service
public class TeacherHomeUrls extends ViewInfo {
    @Autowired
    private Util util;

    public TeacherHomeUrls() {
        breadcrumbSupplier = this::homeBreadcrumb;
    }

    @Override
    public String getURL() {
        return home();
    }

    public String home() {
        return UrlBuilder.get(TeacherHomeController.class, WebMvcConfig.indexName);
    }

    public Breadcrumb homeBreadcrumb() {
        return new Breadcrumb(home(), util.capitalize("home.home"));
    }
}
