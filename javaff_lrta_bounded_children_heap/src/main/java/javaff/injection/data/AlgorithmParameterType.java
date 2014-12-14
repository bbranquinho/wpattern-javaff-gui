package javaff.injection.data;

import java.util.Date;

public enum AlgorithmParameterType {

	BOOLEAN,

	CHARACTER,

	DATE,

	FLOAT,

	INTEGER,

	LONG,

	UNKNOWN;

	public static AlgorithmParameterType parser(Class<?> type) {
		if ((type == int.class) || (type == Integer.class)) {            // INTEGER
			return INTEGER;
		} else if ((type == long.class) || (type == Long.class)) {       // LONG
			return LONG;
		} else if ((type == boolean.class) || (type == Boolean.class)) { // BOOLEAN
			return BOOLEAN;
		} else if ((type == char.class) || (type == Character.class)) {  // CHARACTER
			return CHARACTER;
		} else if ((type == double.class) || (type == Double.class)) {   // DOUBLE
			return FLOAT;
		} else if ((type == float.class) || (type == Float.class)) {     // FLOAT
			return FLOAT;
		} else if (type == Date.class) {                                 // DATE
			return DATE;
		}

		return UNKNOWN;
	}

}
