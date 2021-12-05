package by.ak.chat.security;

import by.ak.chat.view.ForgotPasswordView;
import by.ak.chat.view.LoginView;
import by.ak.chat.view.RegistrationView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final ChatRedirectingAuthenticationSuccessHandler authorisedHandler;
  private final UserDetailsService userDetailsService;
  private final AuthenticationProvider provider;
  private final PasswordEncoder passwordEncoder;

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
      .antMatchers(ForgotPasswordView.PATH).permitAll()

      // Allow all Vaadin internal requests.
      .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

      // Allow all requests by logged-in users.
      .anyRequest().authenticated()

      // Configure the login page.
      .and().formLogin()
      .loginPage(LoginView.PATH).permitAll()
      .loginProcessingUrl(LoginView.PATH)
      .successHandler(authorisedHandler)
      .failureUrl(LOGIN_FAILURE_URL)

      // Configure logout
      .and().logout();
  }

  @Profile("dev-govno")
  @SuppressWarnings("deprecation")
  @Order(1)
  @Bean
  public static NoOpPasswordEncoder passwordEncoder() {
    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
    throws Exception {
    authenticationManagerBuilder
      .authenticationProvider(provider)
      .userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder);
  }

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
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
      "/register",
      "/forgotPassword");
  }
}
