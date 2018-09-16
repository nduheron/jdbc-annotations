package fr.nduheron.socle.jdbc.annotations.processors.model;

import java.util.ArrayList;
import java.util.List;

public class Field {

	private String name;

	private String type;

	private int index;

	private boolean isEnum;

	private boolean isNulSafe;

	private Converter converter;

	private List<Field> parameters = new ArrayList<>();

	public boolean isNulSafe() {
		return isNulSafe;
	}

	public void setNulSafe(boolean isNulSafe) {
		this.isNulSafe = isNulSafe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public List<Field> getParameters() {
		return parameters;
	}

	public void setParameters(List<Field> parameters) {
		this.parameters = parameters;
	}

}
