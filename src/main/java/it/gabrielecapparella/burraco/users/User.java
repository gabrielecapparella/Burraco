package it.gabrielecapparella.burraco.users;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique=true)
	private String username;
	@Column(nullable=false)
	private String password;
	@Column(unique=true)
	private String email;
	@Column(nullable=false)
	private UserRole userRole;

	@ColumnDefault(value="0")
	private int matchesPlayed;
	@ColumnDefault(value="0")
	private int matchesWon;
	@ColumnDefault(value="0")
	private int matchesLost;
	@ColumnDefault(value="0")
	private int matchesDrawn;
	@ColumnDefault(value="0")
	private int matchesAbandoned;
	@ColumnDefault(value="0")
	private int score;

	public User() {}

	public User(String username, String password, String email, UserRole userRole) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", userRole=" + userRole +
				'}';
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() { return true;	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) { this.password = password; }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public int getMatchesPlayed() {
		return matchesPlayed;
	}

	public void setMatchesPlayed(int matchesPlayed) {
		this.matchesPlayed = matchesPlayed;
	}

	public int getMatchesWon() {
		return matchesWon;
	}

	public void setMatchesWon(int matchesWon) {
		this.matchesWon = matchesWon;
	}

	public int getMatchesLost() {
		return matchesLost;
	}

	public void setMatchesLost(int matchesLost) {
		this.matchesLost = matchesLost;
	}

	public int getMatchesDrawn() {
		return matchesDrawn;
	}

	public void setMatchesDrawn(int matchesDrawn) {
		this.matchesDrawn = matchesDrawn;
	}

	public int getMatchesAbandoned() {
		return matchesAbandoned;
	}

	public void setMatchesAbandoned(int matchesAbandoned) {
		this.matchesAbandoned = matchesAbandoned;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}