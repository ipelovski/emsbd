package emsbj.home;

import emsbj.Breadcrumb;
import emsbj.util.Util;
import emsbj.config.WebMvcConfig;
import emsbj.web.UrlBuilder;
import emsbj.web.ViewInfo;
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
