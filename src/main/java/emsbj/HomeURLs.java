package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.stereotype.Component;

@Component
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
