package org.wpattern.javaff.gui.components.beans;

public class ProblemBean {

	private String path;

	private String name;

	private boolean selected = false;

	public ProblemBean(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
