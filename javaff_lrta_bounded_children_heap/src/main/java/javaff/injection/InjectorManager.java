package javaff.injection;

import java.lang.reflect.Field;
import java.util.Date;

import javaff.annotation.AlgorithmParameter;
import javaff.injection.data.AlgorithmParameterType;
import javaff.injection.data.ErrorMessages;
import javaff.injection.data.MapParameters;
import javaff.injection.exceptions.FormatFieldException;
import javaff.injection.exceptions.InjectionException;
import javaff.injection.exceptions.MapFieldException;

public class InjectorManager {

	//=====================================================================================
	// PUBLIC METHODS
	//=====================================================================================

	/**
	 * Inject just public fields.
	 * 
	 * @param objectInstance
	 */
	public static void injectValues(Object objectInstance, MapParameters mapFields) throws InjectionException {
		ValidatorManager.validateFields(objectInstance);

		Class<?> objClass = objectInstance.getClass();

		Field[] fields = objClass.getFields();

		for (Field field : fields) {
			AlgorithmParameter annotationField = field.getAnnotation(AlgorithmParameter.class);

			if (annotationField != null) {
				injectValue(field, annotationField, objectInstance, mapFields);
			}
		}
	}

	/**
	 * Inject all types (public, protected, private, final) of fields.
	 * 
	 * @param objectInstance
	 */
	public static void injectAllValues(Object objectInstance, MapParameters mapFields) throws InjectionException {
		ValidatorManager.validateFields(objectInstance);

		Class<?> objClass = objectInstance.getClass();

		Field[] fields = objClass.getDeclaredFields();

		for (Field field : fields) {
			AlgorithmParameter annotationField = field.getAnnotation(AlgorithmParameter.class);

			if (annotationField != null) {
				field.setAccessible(true);

				injectValue(field, annotationField, objectInstance, mapFields);
			}
		}
	}

	public static MapParameters getParameters(Class<?> algorithm) {
		MapParameters parameters = new MapParameters();
		Field[] fields = algorithm.getDeclaredFields();
		AlgorithmParameter annotation;

		for (Field field : fields) {
			annotation = field.getAnnotation(AlgorithmParameter.class);

			if (annotation != null) {
				parameters.addField(field.getName(), AlgorithmParameterType.parser(field.getType()));
			}
		}

		return parameters;
	}

	//=====================================================================================
	// PRIVATE METHODS
	//=====================================================================================

	private static void injectValue(Field field, AlgorithmParameter fieldAnnotation, Object objectInstance, MapParameters mapFields) throws InjectionException {
		Object value = null;

		try {
			validateValue(field, fieldAnnotation, mapFields);

			if ((field.getType() == int.class) || (field.getType() == Integer.class)) {            // INTEGER
				value = mapFields.getIntValue(field.getName());
			} else if ((field.getType() == long.class) || (field.getType() == Long.class)) {       // LONG
				value = mapFields.getLongValue(field.getName());
			} else if ((field.getType() == boolean.class) || (field.getType() == Boolean.class)) { // BOOLEAN
				value = mapFields.getBooleanValue(field.getName());
			} else if ((field.getType() == char.class) || (field.getType() == Character.class)) {  // CHARACTER
				value = mapFields.getCharacterValue(field.getName());
			} else if ((field.getType() == double.class) || (field.getType() == Double.class)) {   // DOUBLE
				value = mapFields.getDoubleValue(field.getName());
			} else if ((field.getType() == float.class) || (field.getType() == Float.class)) {     // FLOAT
				value = mapFields.getFloatValue(field.getName());
			} else if (field.getType() == Date.class) {                                            // DATE
				value = mapFields.getDateValue(field.getName());
			}
		} catch (MapFieldException e) {
			if (fieldAnnotation.required()) {
				throw new InjectionException(e.getMessage(), e);
			}
		} catch (FormatFieldException e) {
			throw new InjectionException(e.getMessage(), e);
		}

		try {
			if (value != null) {
				field.set(objectInstance, value);
			}
		} catch (IllegalArgumentException e) {
			throw new InjectionException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new InjectionException(e.getMessage(), e);
		}
	}

	private static void validateValue(Field field, AlgorithmParameter fieldAnnotation, MapParameters mapFields) throws MapFieldException, InjectionException {
		if (fieldAnnotation.values().length <= 0) {
			return;
		}

		String value = mapFields.getValue(field.getName());

		for (String fieldValue : fieldAnnotation.values()) {
			if (fieldValue.equalsIgnoreCase(value)) {
				return;
			}
		}

		throw new InjectionException(String.format(ErrorMessages.FIELD_WITH_INVALID_VALUE, field.getName(), value));
	}

}
