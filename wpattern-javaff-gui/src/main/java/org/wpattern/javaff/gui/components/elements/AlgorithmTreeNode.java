package org.wpattern.javaff.gui.components.elements;

import javax.swing.tree.DefaultMutableTreeNode;

import org.wpattern.javaff.gui.components.beans.AlgorithmBean;

public class AlgorithmTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 201302281044L;

	private AlgorithmBean algorithm;

	public AlgorithmTreeNode(String name) {
		super(name);
		this.algorithm = new AlgorithmBean(name);
	}

	public AlgorithmTreeNode(String name, boolean selected) {
		super(name);
		this.algorithm = new AlgorithmBean(name, selected);
	}

	public AlgorithmTreeNode(AlgorithmBean algorithm) {
		super(algorithm.getName());
		this.algorithm = algorithm;
	}

	public String getName() {
		return this.algorithm.getName();
	}

	public void setName(String name) {
		this.algorithm.setName(name);
	}

	public boolean isSelected() {
		return this.algorithm.isSelected();
	}

	public void setSelected(boolean selected) {
		this.algorithm.setSelected(selected);
	}

	public AlgorithmBean getAlgorithm() {
		return this.algorithm;
	}

	public void setAlgorithm(AlgorithmBean algorithm) {
		this.algorithm = algorithm;
	}

}
