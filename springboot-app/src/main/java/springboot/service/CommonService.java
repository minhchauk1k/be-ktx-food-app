package springboot.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.RequiredArgsConstructor;
import springboot.common.ConstDefined;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonService {
	@Autowired
	private final SystemParameterService parameterService;

	public String getSerectKey() {
		return parameterService.getByKey(ConstDefined.SERECT_KEY).getParameterValue();
	}

	public Algorithm getAlgorithm() {
		return Algorithm.HMAC256(getSerectKey().getBytes());
	}

	public String genAccessToken(User user, String url) {
		return JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + ConstDefined.WEEK)).withIssuer(url)
				.withClaim("roles", getRoleList(user)).sign(getAlgorithm());
	}

	public String genRefreshToken(User user, String url) {
		return JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + ConstDefined.WEEK * 2)).withIssuer(url)
				.sign(getAlgorithm());
	}

	private List<String> getRoleList(User user) {
		return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}
}
