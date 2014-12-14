package org.wpattern.javaff.gui.components.beans;

import javaff.injection.data.AlgorithmParameterType;
import javaff.injection.data.MapParameters;
import javaff.search.data.Search;

public class AlgorithmBean {

	private String name;

	private boolean selected = false;

	private Class<? extends Search> classes;

	private MapParameters parameters;

	public AlgorithmBean(String name) {
		this.name = name;
	}

	public AlgorithmBean(String name, boolean selected) {
		this.name = name;
		this.selected = selected;
	}

	public AlgorithmBean(Class<? extends Search> classes) {
		this.classes = classes;
		this.name = this.classes.getSimpleName();
		this.parameters = new MapParameters();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<? extends Search> getClasses() {
		return this.classes;
	}

	public void setClasses(Class<? extends Search> classes) {
		this.classes = classes;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void addParameter(String name, String value, AlgorithmParameterType type) {
		this.parameters.addField(name, value, type);
	}

	public MapParameters getParameters() {
		return this.parameters;
	}

	public void setParameters(MapParameters parameters) {
		this.parameters = parameters;
	}

}
