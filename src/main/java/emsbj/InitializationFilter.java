package emsbj;

import emsbj.generation.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
@Order(1000000)
public class InitializationFilter implements Filter {
    @Autowired
    private Generator generator;
    private boolean initialized;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!initialized) {
            initialize();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void initialize() {
        generator.generate();
        initialized = true;
    }
}
