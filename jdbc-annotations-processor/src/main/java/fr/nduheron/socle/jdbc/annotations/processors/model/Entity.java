package fr.nduheron.socle.jdbc.annotations.processors.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity {

	private String fullClassName;
	private String className;
	private List<Field> fields = new ArrayList<>();
	private Set<Converter> converters = new HashSet<>();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public void addField(Field fieldModel) {
		fields.add(fieldModel);
	}

	public Set<Converter> getConverters() {
		return converters;
	}

	public void setConverters(Set<Converter> converters) {
		this.converters = converters;
	}

}
