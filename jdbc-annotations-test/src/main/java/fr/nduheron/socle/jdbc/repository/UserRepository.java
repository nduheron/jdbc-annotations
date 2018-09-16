package fr.nduheron.socle.jdbc.repository;

import fr.nduheron.socle.jdbc.domain.User;

public interface UserRepository {

	User findById(long id);
	
	long save(User user);
}
