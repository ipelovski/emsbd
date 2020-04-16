package sasj;

import java.util.Optional;

public class Breadcrumb {
    private String url;
    private String label;
    private Optional<Breadcrumb> parent;
    private boolean isLast;

    public Breadcrumb(Object url, String label) {
        this.url = url.toString();
        this.label = label;
        this.parent = Optional.empty();
    }

    public Breadcrumb(Object url, String label, Breadcrumb parent) {
        this.url = url.toString();
        this.label = label;
        this.parent = Optional.of(parent);
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    public Optional<Breadcrumb> getParent() {
        return parent;
    }

    public Breadcrumbs build() {
        return new Breadcrumbs(this);
    }

    public boolean isLast() {
        return isLast;
    }

    protected void setLast(boolean isLast) {
        this.isLast = isLast;
    }
}
