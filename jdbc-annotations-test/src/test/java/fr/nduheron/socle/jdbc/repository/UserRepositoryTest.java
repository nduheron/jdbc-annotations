package fr.nduheron.socle.jdbc.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.nduheron.socle.jdbc.domain.Address;
import fr.nduheron.socle.jdbc.domain.Role;
import fr.nduheron.socle.jdbc.domain.User;
import fr.nduheron.socle.jdbc.test.config.TestConfiguration;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.StringFormatType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class UserRepositoryTest {
	private static final String[] STREETS = new String[] { "Main Street", "Church Street", "Main Street North",
			"Main Street South", "Elm Street", "High Street", "Main Street West" };

	@Autowired
	private UserRepository repository;

	@Test
	public void testWithNoNullValue() {
		MockNeat m = MockNeat.secure();

		User user = m.filler(() -> new User())
				.setter(User::setLastName, m.names().last().format(StringFormatType.UPPER_CASE))
				.setter(User::setFirstName, m.names().first().format(StringFormatType.LOWER_CASE))
				.setter(User::setAge, m.ints().bound(100))
				.setter(User::setAddress,
						m.filler(() -> new Address()).setter(Address::setNumber, m.ints().bound(300))
								.setter(Address::setStreet, m.seq(Arrays.asList(STREETS)))
								.setter(Address::setCity, m.cities().us()))
				.setter(User::setEmail, m.emails()).setter(User::setLogin, m.users())
				.setter(User::setPassword, m.passwords()).setter(User::setRole, m.seq(Arrays.asList(Role.values())))
				.setter(User::setEnabled, m.bools()).setter(User::setLastConnexion, m.localDates().thisYear()).val();

		long id = repository.save(user);
		User result = repository.findById(id);
		assertNotNull(result);
		assertEquals(id, result.getId());
		assertEquals(user.getLastName(), result.getLastName());
		assertEquals(StringUtils.capitalize(user.getFirstName()), result.getFirstName());
		assertEquals(user.getAge(), result.getAge());
		assertNotNull(user.getAddress());
		assertEquals(user.getAddress().getNumber(), result.getAddress().getNumber());
		assertEquals(user.getAddress().getStreet(), result.getAddress().getStreet());
		assertEquals(user.getAddress().getCity().toUpperCase(), result.getAddress().getCity());
		assertEquals(user.getLogin(), result.getLogin());
		assertEquals(user.getEmail(), result.getEmail());
		assertEquals(user.getPassword(), result.getPassword());
		assertEquals(user.getRole(), result.getRole());
		assertEquals(user.getLastConnexion(), result.getLastConnexion());
		assertEquals(user.isEnabled(), result.isEnabled());

	}

	@Test
	public void testWithNullValue() {
		MockNeat m = MockNeat.secure();

		User user = m.filler(() -> new User())
				.setter(User::setLastName, m.names().last().format(StringFormatType.UPPER_CASE))
				.setter(User::setFirstName, m.names().first().format(StringFormatType.UPPER_CASE))
				.setter(User::setLogin, m.users()).val();

		long id = repository.save(user);
		User result = repository.findById(id);
		assertNotNull(result);
		assertEquals(id, result.getId());
		assertEquals(user.getLastName(), result.getLastName());
		assertNull(result.getAge());
		assertEquals(StringUtils.capitalize(user.getFirstName()), result.getFirstName());
		assertNull(result.getAddress());
		assertEquals(user.getLogin(), result.getLogin());
		assertNull(result.getEmail());
		assertNull(result.getPassword());
		assertNull(result.getRole());
		assertNull(result.getLastConnexion());
		assertFalse(result.isEnabled());

	}
}
