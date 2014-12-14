package javaff.injection.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javaff.injection.exceptions.FormatFieldException;
import javaff.injection.exceptions.MapFieldException;

public class MapParameters {

	private final Map<String, AlgorithmParameterBean> mapOfValues = new HashMap<String, AlgorithmParameterBean>();

	public void addField(String name, AlgorithmParameterType type) {
		AlgorithmParameterBean parameter = new AlgorithmParameterBean(name, type);
		this.mapOfValues.put(parameter.getName(), parameter);
	}

	public void addField(String name, String value, AlgorithmParameterType type) {
		AlgorithmParameterBean parameter = new AlgorithmParameterBean(name, value, type);
		this.mapOfValues.put(parameter.getName(), parameter);
	}

	public String getValue(String name) throws MapFieldException {
		AlgorithmParameterBean value = this.mapOfValues.get(name);

		if ((value == null) || (value.getValue() == null)) {
			throw new MapFieldException(String.format(ErrorMessages.FIELD_NOT_FOUNDED, name));
		}

		return value.getValue();
	}

	public AlgorithmParameterType getType(String name)  throws MapFieldException {
		AlgorithmParameterBean value = this.mapOfValues.get(name);

		if (value == null) {
			throw new MapFieldException(String.format(ErrorMessages.FIELD_NOT_FOUNDED, name));
		}

		return value.getType();
	}

	public Integer getIntValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Long getLongValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		try {
			return Long.parseLong(value);
		} catch(NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Double getDoubleValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		try {
			return Double.parseDouble(value);
		} catch(NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Float getFloatValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		try {
			return Float.parseFloat(value);
		} catch(NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Boolean getBooleanValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		try {
			return Boolean.parseBoolean(value);
		} catch(NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Character getCharacterValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		if (value.length() != 1) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name));
		}

		try {
			return value.charAt(0);
		} catch(NumberFormatException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Date getDateValue(String name) throws MapFieldException, FormatFieldException {
		String value = this.getValue(name);

		SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

		try {
			return formater.parse(value);
		} catch(ParseException e) {
			throw new FormatFieldException(String.format(ErrorMessages.FIELD_INVALID_FORMAT, value, name), e);
		}
	}

	public Collection<AlgorithmParameterBean> getValues() {
		return this.mapOfValues.values();
	}

}
