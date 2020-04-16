package sasj.web;

import sasj.Breadcrumb;

import java.util.function.Supplier;

public class ViewInfo {
    protected String url;
    protected Breadcrumb breadcrumb;
    protected Supplier<Breadcrumb> breadcrumbSupplier;

    protected ViewInfo() {}

    public ViewInfo(String url, Supplier<Breadcrumb> breadcrumbSupplier) {
        this.url = url;
        this.breadcrumbSupplier = breadcrumbSupplier;
    }

    public String getURL() {
        return url;
    }

    public Breadcrumb getBreadcrumb() {
        if (breadcrumb == null) {
            breadcrumb = breadcrumbSupplier.get();
        }
        return breadcrumb;
    }

    @Override
    public String toString() {
        return getURL();
    }
}
