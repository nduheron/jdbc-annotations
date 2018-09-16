package fr.nduheron.socle.jdbc.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.nduheron.socle.jdbc.domain.User;
import fr.nduheron.socle.jdbc.mapper.UserRowMapper;

@Repository
public class UserRepositoryImpl implements UserRepository {

	@Autowired
	private UserRowMapper mapper;

	@Autowired
	private JdbcOperations jdbcOperations;

	@Override
	public User findById(long id) {
		return jdbcOperations.queryForObject("SELECT " + User.SELECT_CLAUSE + " FROM user WHERE ID = ?", mapper, id);
	}

	@Override
	public long save(User user) {
		KeyHolder key = new GeneratedKeyHolder();
		jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement(
						"INSERT INTO  user (last_name,first_name,address_number,address_street,address_city,login,email,enabled,password,last_connexion,role,age) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getLastName());
				ps.setString(2, user.getFirstName());
				ps.setObject(3, user.getAddress() == null ? null : user.getAddress().getNumber());
				ps.setString(4, user.getAddress() == null ? null : user.getAddress().getStreet());
				ps.setString(5, user.getAddress() == null ? null : user.getAddress().getCity());
				ps.setString(6, user.getLogin());
				ps.setString(7, user.getEmail());
				ps.setBoolean(8, user.isEnabled());
				ps.setString(9, user.getPassword());
				ps.setDate(10, user.getLastConnexion() == null ? null : Date.valueOf(user.getLastConnexion()));
				ps.setString(11, user.getRole() == null ? null : user.getRole().name());
				ps.setObject(12, user.getAge());
				return ps;
			}
		}, key);
		return key.getKey().longValue();
	}

}
