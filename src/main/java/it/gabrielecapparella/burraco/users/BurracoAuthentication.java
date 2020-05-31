package it.gabrielecapparella.burraco.users;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class BurracoAuthentication implements Authentication {
	User principal;

	public BurracoAuthentication(User principal) {
		this.principal = principal;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean b) throws IllegalArgumentException {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean implies(Subject subject) {
		return false;
	}
}
