package com.example.demo.security;

import com.example.demo.auth.ApplicationUserService;
import com.example.demo.jwt.JwtConfig;
import com.example.demo.jwt.JwtTokenVerifier;
import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static com.example.demo.security.ApplicationUserPermission.*;
import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Autowired
    public ApplicationSecurityConfig(
            PasswordEncoder passwordEncoder,
            ApplicationUserService applicationUserService, SecretKey secretKey, JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //CSRF אם נרצה להשתמש ב
//                .and()
                .csrf().disable() // csrf אם נרצה לבטל את ה
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey),JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()// אנחנו רוצים לאמת בקשות
                .antMatchers("/", "/login", "/index.html", "/css/*", "/js/*").permitAll() // בנתיבים האלה תאשר לכולם
                .antMatchers("/api/**").hasRole(STUDENT.name()) // כל מה שהוא ךפי הדפוס
                .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAnyAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/management/api/**").hasAnyAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAnyAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMIN_TRAINEE.name())
                .anyRequest() // חוץ מאלה תאמת את כל השאר
                .authenticated(); //עם שם משתמש וסיסמא

//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/courses")
//                .and()
//                .rememberMe()
//                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21)) //default to 2 weeks
//                .key("something_very_secured")
//                .and()
//                .logout()
//                    .logoutUrl("/logout") // באיזה נתיב מתנקים
//                    .clearAuthentication(true) //
//                    .invalidateHttpSession(true) //
//                    .deleteCookies("JSESSIONID", "remember-me") // איזה עוגיות למחוק
//                    .logoutSuccessUrl("/login"); // מה קורה כשמסיימים את הlogout

    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails rootUser = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("password"))
////                .roles(ADMIN.name()) //ROLE_ADMIN
//                .authorities(ADMIN.getGrantedAuthorities())
//                .build();
//
//        UserDetails studentUser = User.builder()
//                .username("student")
//                .password(passwordEncoder.encode("password"))
//                .authorities(STUDENT.getGrantedAuthorities())
//                .build();
//
//        UserDetails traineeAdmin = User.builder()
//                .username("traineeAdmin")
//                .password(passwordEncoder.encode("password"))
//                .authorities(ADMIN_TRAINEE.getGrantedAuthorities())
//                .build();
//
//        return new InMemoryUserDetailsManager(rootUser, studentUser, traineeAdmin);
//    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}
