package th.ac.chula.fims.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import th.ac.chula.fims.security.jwt.AuthEntryPointJwt;
import th.ac.chula.fims.security.jwt.AuthTokenFilter;
import th.ac.chula.fims.security.services.UserDetailsServiceImpl;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;

    private final AuthEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected Filter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.addExposedHeader("Location");

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/api/auth/status",
                        "/api/auth/me",
                        "/api/auth/change-pwd")
                .hasAnyRole("USER", "LAB_USER", "ADMIN")
                .antMatchers(
                        "/api/test/mod",
                        "/api/files/upload",
                        "/api/person/**",
                        "/api/samples/allele",
                        "/api/forenseq-sequences/pattern-alignment",
                        "/api/persons",
                        "/api/files/example-cedata",
                        "/api/files/example-person",
                        "/api/persons-custom/**",
                        "/api/admins/samples/existing")
                .hasAnyRole("LAB_USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/profile-search/").hasAnyRole("LAB_USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/profile-search").hasAnyRole("LAB_USER", "ADMIN")
                .antMatchers(
                        "/api/loci/all",
                        "/api/loci/global",
                        "/api/forenseq-sequences/graph",
                        "/api/forenseq-sequences/isnp",
                        "/api/loci/isnp",
                        "/api/auth/**",
                        "/api/samples/person",
                        "/api/samples/person/excel",
                        "/api/kits/**",
                        "/api/files/example-excel",
                        "/api/forenseq-sequences/map",
                        "/api/summaries/locus/**",
                        "/api/summaries/dashboard",
                        "/api/configuration/keys",
                        "/api/admins/maps",
                        "/api/admins/test",
                        "/api/core-loci/**",
                        "/api/profile-search/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/public-api/**")
                .permitAll()
                .antMatchers("/api/admins/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated();

        http.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
