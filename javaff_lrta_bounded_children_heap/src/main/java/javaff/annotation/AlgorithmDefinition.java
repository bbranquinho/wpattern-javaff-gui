package javaff.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javaff.annotation.enums.FilterType;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AlgorithmDefinition {

	public String description() default "";

	public String label() default "";

	public FilterType filterType() default FilterType.HELPFUL_FILTER;

}
