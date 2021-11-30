package by.ak.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String LOGIN_FAILURE_URL = LoginView.PATH + "?error";

  /**
   * Require login to access internal pages and configure login form.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Vaadin handles CSRF internally
    http.csrf().disable()

      // Register our CustomRequestCache, which saves unauthorized access attempts, so the user is redirected after login.
      .requestCache().requestCache(new CustomRequestCache())

      // Restrict access to our application.
      .and().authorizeRequests()
      .antMatchers(RegistrationView.PATH).permitAll()

      // Allow all Vaadin internal requests.
      .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

      // Allow all requests by logged-in users.
      .anyRequest().authenticated()

      // Configure the login page.
      .and().formLogin()
      .loginPage(LoginView.PATH).permitAll()
      .loginProcessingUrl(LoginView.PATH)
      .successForwardUrl(ChatView.PATH)
      .failureUrl(LOGIN_FAILURE_URL)

      // Configure logout
      .and().logout().logoutSuccessUrl(LoginView.PATH);
  }

  @Profile("dev")
  @SuppressWarnings("deprecation")
  @Bean
  public static NoOpPasswordEncoder passwordEncoder() {
    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user")
      .password("123")
      .roles("USER")
      .build();

    return new InMemoryUserDetailsManager(user);
  }

  /**
   * Allows access to static resources, bypassing Spring Security.
   */
  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(
      // Client-side JS
      "/VAADIN/**",

      // the standard favicon URI
      "/favicon.ico",

      // the robots exclusion standard
      "/robots.txt",

      // web application manifest
      "/manifest.webmanifest",
      "/sw.js",
      "/offline.html",

      // icons and images
      "/icons/**",
      "/images/**",
      "/styles/**",

      // (development mode) H2 debugging console
      "/h2-console/**",

      // register page
      "/register");
  }
}
