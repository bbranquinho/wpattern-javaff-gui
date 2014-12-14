package org.wpattern.javaff.gui.components.elements;

import javax.swing.tree.DefaultMutableTreeNode;

import org.wpattern.javaff.gui.components.beans.DomainProblemBean;

public class DomainProblemTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 201302281044L;

	private final DomainProblemBean domainProblem;

	private boolean selected;

	public DomainProblemTreeNode(String name) {
		super(name);
		this.selected = false;
		this.domainProblem = new DomainProblemBean(name);
	}

	public DomainProblemTreeNode(DomainProblemBean domainProblem) {
		super(domainProblem.getName());
		this.selected = false;
		this.domainProblem = domainProblem;
	}

	public DomainProblemTreeNode(String name, boolean selected) {
		super(name);
		this.selected = selected;
		this.domainProblem = new DomainProblemBean(name);
	}

	public String getText() {
		return this.domainProblem.getName();
	}

	public boolean isSelected() {
		return this.selected;
	}

	public DomainProblemBean getDomainProblem() {
		return this.domainProblem;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
