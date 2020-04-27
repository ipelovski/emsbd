package sasj.controller.student.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sasj.config.WebMvcConfig;
import sasj.controller.Breadcrumb;
import sasj.controller.home.HomeController;
import sasj.util.Util;
import sasj.web.UrlBuilder;
import sasj.web.ViewInfo;

@Service
public class StudentHomeUrls extends ViewInfo {
    @Autowired
    private Util util;

    public StudentHomeUrls() {
        breadcrumbSupplier = this::homeBreadcrumb;
    }

    @Override
    public String getURL() {
        return home();
    }

    public String home() {
        return UrlBuilder.get(StudentHomeController.class, WebMvcConfig.indexName);
    }

    public Breadcrumb homeBreadcrumb() {
        return new Breadcrumb(home(), util.capitalize("home.home"));
    }
}
