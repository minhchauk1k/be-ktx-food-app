package springboot.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import springboot.enums.MConst;
import springboot.service.CommonService;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
	private final CommonService commonService;

	public CustomAuthorizationFilter(CommonService commonService) {
		this.commonService = commonService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		switch (request.getServletPath()) {
		case "/login":
//		case "/order/add":
			filterChain.doFilter(request, response);
			break;
		default:
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.startsWith(MConst.BEARER)) {
				// authorizationHeader != null
				try {
					String token = authorizationHeader.substring(MConst.BEARER.length());
					Algorithm algorithm = commonService.getAlgorithm();
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();

					UsernamePasswordAuthenticationToken authenToken = new UsernamePasswordAuthenticationToken(username,
							null, getAuthorities(decodedJWT));
					SecurityContextHolder.getContext().setAuthentication(authenToken);
					filterChain.doFilter(request, response);
				} catch (Exception e) {
					log.error(e.getMessage());

					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setHeader("error", e.getMessage());

					Map<String, String> error = new HashMap<>();
					error.put("error_message", e.getMessage());

					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			} else {
				// authorizationHeader = null
				filterChain.doFilter(request, response);
			}
			break;
		}
	}

	private Collection<SimpleGrantedAuthority> getAuthorities(DecodedJWT decoded) {
		List<String> roles = decoded.getClaim("roles").asList(String.class);
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		roles.forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});
		return authorities;
	}
}
