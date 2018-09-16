package fr.nduheron.socle.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;

import fr.nduheron.socle.jdbc.annotations.core.JdbcMapper;
import fr.nduheron.socle.jdbc.domain.User;

@JdbcMapper
public interface UserRowMapper extends RowMapper<User> {

}
