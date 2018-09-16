package fr.nduheron.socle.jdbc.domain;

import java.io.Serializable;

import fr.nduheron.socle.jdbc.annotations.core.Column;
import fr.nduheron.socle.jdbc.annotations.core.Columns;
import fr.nduheron.socle.jdbc.converter.CapitalizedConverter;
import fr.nduheron.socle.jdbc.converter.UpperCaseConverter;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(index = 1)
	private long id;

	@Column(index = 2, converter = CapitalizedConverter.class)
	private String lastName;

	@Column(index = 3, converter = CapitalizedConverter.class)
	private String firstName;

	@Column(index = 4)
	private Integer age;

	@Columns({
		@Column(index=5),
		@Column(index=6),
		@Column(index=7, converter=UpperCaseConverter.class)
	})
	private Address address;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}
