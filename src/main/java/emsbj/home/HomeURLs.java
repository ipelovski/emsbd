package emsbj.home;

import emsbj.config.WebMvcConfig;
import emsbj.web.URLBuilder;
import emsbj.web.ViewInfo;
import org.springframework.stereotype.Service;

@Service
public class HomeURLs extends ViewInfo {
    private HomeController homeController;

    public HomeURLs(HomeController homeController) {
        this.homeController = homeController;
        breadcrumbSupplier = homeController::indexBreadcrumb;
    }

    @Override
    public String getURL() {
        return home();
    }

    public String home() {
        return URLBuilder.get(HomeController.class, WebMvcConfig.indexName);
    }
}
