package sasj.controller;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Breadcrumbs extends AbstractCollection<Breadcrumb> {
    public static final String modelAttributeName = "breadcrumbs";
    private List<Breadcrumb> breadcrumbs = new LinkedList<>();

    public Breadcrumbs(Breadcrumb breadCrumb) {
        breadCrumb.setLast(true);
        breadcrumbs.add(0, breadCrumb);
        while (breadCrumb.getParent().isPresent()) {
            breadCrumb = breadCrumb.getParent().get();
            breadCrumb.setLast(false);
            breadcrumbs.add(0, breadCrumb);
        }
    }

    @Override
    public Iterator<Breadcrumb> iterator() {
        return breadcrumbs.iterator();
    }

    @Override
    public int size() {
        return breadcrumbs.size();
    }

    @Override
    public boolean add(Breadcrumb breadCrumb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
