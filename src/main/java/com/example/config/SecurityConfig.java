package com.example.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

/**
 * https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter
 * https://baeldung.com/java-config-spring-security
 * https://www.baeldung.com/spring-security-create-new-custom-security-expression
 */
@Configuration
@EnableWebSecurity(debug = false)
//@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig /*extends GlobalMethodSecurityConfiguration*/ {




	@Bean
	@Order(1)
	SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception {

		return http
				.securityMatchers(cfg -> cfg.requestMatchers("/api/**", "/admin/**", "/actuator/**"))
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//						.requestMatchers(request -> request.getHeader("permit") != null).permitAll()
//						.requestMatchers("/api/**").hasRole("USER")
//						.requestMatchers("/admin/**").hasRole("ADMIN")
//						.requestMatchers("/actuator/**").permitAll()
//						.anyRequest().authenticated()
						.anyRequest().permitAll()
				)
				.exceptionHandling(eh -> {
					eh.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
/*
						eh.accessDeniedHandler((request, response, exception) -> {
							exception.printStackTrace();
						});
*/
				})
				
				.build();
	}


	@Bean
	@Order(2)
	public SecurityFilterChain httpBasicFilterChain(HttpSecurity http) throws Exception {

		return http
				.securityMatcher("/**")
				.csrf(Customizer.withDefaults())
//				.cors(cors -> cors.disable())
//				.formLogin(form -> {})
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
						.anyRequest().authenticated())
//				.authorizeHttpRequests(req ->
//						req.requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico").permitAll())
//				.authorizeHttpRequests(req -> req.anyRequest().authenticated())
				.build();
	}


	@Bean
	public AuthenticationManager authenticationManager(
			HttpSecurity http, PasswordEncoder passwordEncoder,
			UserDetailsService userDetailService) throws Exception {

		return http
				.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailService)
				.passwordEncoder(passwordEncoder)
				.and().build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


/*
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurity -> webSecurity
				.ignoring().requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico");
	}
*/


	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		manager.createUser(User.withUsername("user")
				.password(passwordEncoder.encode("userPass"))
				.roles("USER")
				.build());

		manager.createUser(User.withUsername("admin")
				.password(passwordEncoder.encode("adminPass"))
				.roles("USER", "ADMIN")
				.build());

//		CachingUserDetailsService service = new CachingUserDetailsService(manager);
//		service.setUserCache(new SpringCacheBasedUserCache(new ConcurrentMapCache("USER_DETAILS_CACHE")));

		return manager;
	}

}
