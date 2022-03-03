package bnorbert.onlineshop.config;

import bnorbert.onlineshop.service.JwtAuthenticationFilter;
import bnorbert.onlineshop.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public WebSecurityConfig(MyUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        //http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/", "/v3/api-docs", "/webjars/**", "/swagger-resources/**",
                        "/configuration/**", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/api-docs/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/create").permitAll()
                .antMatchers("/users/login/**").permitAll()
                .antMatchers(HttpMethod.POST,"/users/confirmUser").permitAll()
                .antMatchers(HttpMethod.POST,"/users/resendToken").permitAll()
                .antMatchers(HttpMethod.PUT,"/roles/addRole").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    protected AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    /*
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "PUT"));
        return firewall;
    }

     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

}


