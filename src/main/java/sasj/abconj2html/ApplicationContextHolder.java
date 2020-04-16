package sasj.abconj2html;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContextHolder instance;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextHolder.instance = this;
    }

    public static ApplicationContextHolder getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No instance is set yet.");
        }
        return instance;
    }

    public static ApplicationContext getApplicationContext() {
        return getInstance().applicationContext;
    }

    public static AutowireCapableBeanFactory getBeanFactory() {
        return getApplicationContext().getAutowireCapableBeanFactory();
    }
}
