package javaff.injection;

import java.lang.reflect.Field;
import java.util.Date;

import javaff.annotation.AlgorithmParameter;
import javaff.injection.data.ErrorMessages;
import javaff.injection.exceptions.InjectionException;

class ValidatorManager {

	//=====================================================================================
	// PUBLIC METHODS
	//=====================================================================================

	public static void validateFields(Object objectInstance) throws InjectionException {
		Class<?> objectClass = objectInstance.getClass();

		Field[] fields = objectClass.getDeclaredFields();

		for (Field field : fields) {
			AlgorithmParameter annotationField = field.getAnnotation(AlgorithmParameter.class);

			if (annotationField != null) {
				if (annotationField.required()) {
					if (!isValidRequeridField(field)) {
						throw new InjectionException(String.format(ErrorMessages.FIELD_WITH_INVALID_TYPE,
								field.getName(), field.getType()));
					}
				} else if (!isValidNotRequeridField(field)) {
					throw new InjectionException(String.format(ErrorMessages.FIELD_WITH_INVALID_TYPE,
							field.getName(), field.getType()));
				}
			}
		}
	}

	//=====================================================================================
	// PRIVATE METHODS
	//=====================================================================================

	private static boolean isValidRequeridField(Field field) {
		return !((field.getType() != Integer.class) && (field.getType() != Long.class) &&
				(field.getType() != Boolean.class) && (field.getType() != Character.class) &&
				(field.getType() != Double.class) && (field.getType() != Float.class) &&
				(field.getType() != Date.class));
	}

	private static boolean isValidNotRequeridField(Field field) {
		return !((field.getType() != int.class) && (field.getType() != Integer.class) &&
				(field.getType() != long.class) && (field.getType() != Long.class) &&
				(field.getType() != boolean.class) && (field.getType() != Boolean.class) &&
				(field.getType() != char.class) && (field.getType() != Character.class) &&
				(field.getType() != double.class) && (field.getType() != Double.class) &&
				(field.getType() != float.class) && (field.getType() != Float.class) &&
				(field.getType() != Date.class));
	}

}
