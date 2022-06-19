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
import springboot.enums.MConst;
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
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/login").permitAll();

		// xử lý với common
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/common/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/common/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/common/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/common/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);

		// xử lý với order
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/order/report/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/order/all/user/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF, MConst.ROLE_USER);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/order/all/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF);
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/order/update/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF);
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/order/add/**").hasAnyAuthority(MConst.ROLE_USER);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/order/update/momo/**").hasAnyAuthority(MConst.ROLE_USER);

		// xử lý với role
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/role/all/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		// xử lý với orderLot
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/order/lot/all/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF);
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/order/lot/add/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF);

		// xử lý với user
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/find/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/find/fullname/**")
				.hasAnyAuthority(MConst.ROLE_USER);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/current/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_STAFF, MConst.ROLE_USER);
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/user/all/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/user/update/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER, MConst.ROLE_USER);
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/add/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/exist/**").permitAll();

		// xử lý với product
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/product/all/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/product/add/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/product/update/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/product/delete/**").hasAnyAuthority(MConst.ROLE_ADMIN,
				MConst.ROLE_OWNER, MConst.ROLE_MANAGER);

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
