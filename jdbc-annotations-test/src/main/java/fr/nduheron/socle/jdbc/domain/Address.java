package fr.nduheron.socle.jdbc.domain;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer number;

	private String street;

	private String city;

	public Address() {
	}

	public Address(Integer number, String street, String city) {
		super();
		this.number = number;
		this.street = street;
		this.city = city;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
