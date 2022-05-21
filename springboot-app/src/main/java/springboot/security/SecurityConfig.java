package springboot.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;
import springboot.common.ConstDefined;
import springboot.filter.CustomAuthenticationFilter;
import springboot.filter.CustomAuthorizationFilter;
import springboot.service.CommonService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private final CommonService commonService;

	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
				authenticationManagerBean(), commonService);
		// check for login
		customAuthenticationFilter.setFilterProcessesUrl("/login");

		// disable login
		http.csrf().disable();

		// disable cors
		http.cors().configurationSource(request -> getCorsConfiguration());

		// don't use session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// allow all user can access
//		http.authorizeRequests().anyRequest().permitAll();

		// custom access
		http.authorizeRequests().antMatchers("/login").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/refresh_token/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/product/all/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/common/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/order/add/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/product/add/**").hasAuthority(ConstDefined.ROLE_ADMIN);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/**").hasAuthority(ConstDefined.ROLE_ADMIN);
		// not allow if don't have role
		http.authorizeRequests().anyRequest().authenticated();

		// set filter
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new CustomAuthorizationFilter(commonService), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	private CorsConfiguration getCorsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method",
				"Access-Control-Request-Headers"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		return corsConfiguration;
	}
}
