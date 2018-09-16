package fr.nduheron.socle.jdbc.annotations.utils;

import org.apache.commons.lang3.StringUtils;

import fr.nduheron.socle.jdbc.annotations.processors.model.Field;

public class FieldUtil {

	public static String getValue(Field field, String resultSetName) {
		String result;
		if (field.isEnum()) {
			result = resultSetName + ".getString(" + field.getIndex() + ") == null ? null : " + field.getType()
					+ ".valueOf(" + resultSetName + ".getString(" + field.getIndex() + "))";
		} else if (field.isNulSafe()) {
			result = resultSetName + ".getObject(" + field.getIndex() + ", " + field.getType() + ".class)";
		} else {
			result = resultSetName + ".get" + field.getType() + "(" + field.getIndex() + ")";
		}

		if (field.getConverter() != null) {
			result = field.getConverter().getFieldName() + ".convert(" + result + ")";
		}

		return result;

	}
	
	public static String getNullableCondition(Field field) {
		String result = "";
		
		for (int i = 0; i < field.getParameters().size(); i++) {
			result += getVariableName(field, field.getParameters().get(i)) + " == null";
			if (i < field.getParameters().size() - 1) {
				result += " && ";
			}
		}
		
		return result;
	}

	public static String getConstructorValue(Field field, String resultSetName) {
		String result;
		result = "new " + field.getType() + "(";
		if (field.isNulSafe()) {
			for (int i = 0; i < field.getParameters().size(); i++) {
				result += getVariableName(field, field.getParameters().get(i));
				if (i < field.getParameters().size() - 1) {
					result += ",";
				}
			}
		} else {
			for (int i = 0; i < field.getParameters().size(); i++) {
				result += getValue(field.getParameters().get(i), resultSetName);
				if (i < field.getParameters().size() - 1) {
					result += ",";
				}
			}
		}
		result += ")";
		return result;

	}

	public static String getVariableName(Field field, Field param) {
		return StringUtils.uncapitalize(field.getName()) + param.getName();
	}
}
