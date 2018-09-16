package fr.nduheron.socle.jdbc.domain;

import java.time.LocalDate;

import fr.nduheron.socle.jdbc.annotations.core.Column;
import fr.nduheron.socle.jdbc.converter.DateConverter;

public class User extends Person {

	private static final long serialVersionUID = 1L;
	public static final String SELECT_CLAUSE = "id, last_name, first_name, age, address_number, address_street, address_city, login, email, password, role, enabled, last_connexion";

	@Column(index = 8)
	private String login;

	@Column(index = 9)
	private String email;

	@Column(index = 10)
	private String password;

	@Column(index = 11)
	private Role role;

	@Column(index = 12)
	private boolean enabled;

	@Column(index = 13, converter = DateConverter.class)
	private LocalDate lastConnexion;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDate getLastConnexion() {
		return lastConnexion;
	}

	public void setLastConnexion(LocalDate lastConnexion) {
		this.lastConnexion = lastConnexion;
	}

}
