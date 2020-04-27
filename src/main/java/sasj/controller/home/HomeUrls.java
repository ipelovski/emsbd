package sasj.controller.home;

import sasj.controller.Breadcrumb;
import sasj.util.Util;
import sasj.config.WebMvcConfig;
import sasj.web.UrlBuilder;
import sasj.web.ViewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeUrls extends ViewInfo {
    @Autowired
    private Util util;

    public HomeUrls() {
        breadcrumbSupplier = this::homeBreadcrumb;
    }

    @Override
    public String getURL() {
        return home();
    }

    public String home() {
        return UrlBuilder.get(HomeController.class, WebMvcConfig.indexName);
    }

    public Breadcrumb homeBreadcrumb() {
        return new Breadcrumb(home(), util.capitalize("home.home"));
    }
}
