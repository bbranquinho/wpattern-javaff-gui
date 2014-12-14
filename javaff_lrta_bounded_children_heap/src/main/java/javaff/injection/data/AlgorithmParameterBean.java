package javaff.injection.data;

public class AlgorithmParameterBean {

	private final String name;

	private String value;

	private AlgorithmParameterType type;

	public AlgorithmParameterBean(String name, AlgorithmParameterType type) {
		this.name = name;
		this.type = type;
	}

	public AlgorithmParameterBean(String name, String value, AlgorithmParameterType type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AlgorithmParameterType getType() {
		return this.type;
	}

	public void setType(AlgorithmParameterType type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

}
