package javaff.data;

import java.math.BigDecimal;

public class AlgorithmConstants {

	/**
	 * Maximum duration in a duration constraint.
	 */
	public static final BigDecimal MAX_DURATION;

	public static BigDecimal EPSILON;

	static {
		MAX_DURATION = new BigDecimal("100000").setScale(2, BigDecimal.ROUND_HALF_EVEN);
		EPSILON = new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

}
