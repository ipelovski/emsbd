package emsbj.config;

import emsbj.admin.SecuredController;
import emsbj.user.JournalUserDetailsService;
import emsbj.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private Map<String, SecuredController> securedControllerMap;

    @Autowired
    private JournalUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
            .csrf().disable()
            .formLogin()
            .loginPage("/sign-in")
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/sign-out")
            .permitAll()
            .and()
            .authorizeRequests();
        for (SecuredController securedController : securedControllerMap.values()) {
            securedController.configure(registry);
        }
        registry.anyRequest().authenticated();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureAuthenticationManager(AuthenticationManagerBuilder authenticationMgr, PasswordEncoder passwordEncoder) throws Exception {
        User admin = new User("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(User.Role.admin);
        authenticationMgr
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .inMemoryAuthentication()
            .withUser(admin)
            .withUser("user1").password(passwordEncoder.encode("password1")).authorities(User.Role.user.name().toUpperCase());
    }

    @Autowired
    public void findSecuredControllers(ApplicationContext applicationContext) {
        securedControllerMap =
            applicationContext.getBeansOfType(SecuredController.class);
    }
}
