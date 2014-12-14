package org.wpattern.javaff.gui.components.elements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javaff.injection.data.AlgorithmParameterBean;

import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

public class AlgorithmParameterTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -8810329263586506584L;

	private String text;

	private String name;

	private String type;

	private AlgorithmParameterBean algorithmParameterBean;

	private JTextField textFieldRenderer;

	public AlgorithmParameterTreeNode(final AlgorithmParameterBean algorithmParameterBean) {
		super(algorithmParameterBean.getName() +" ["+ algorithmParameterBean.getType()+"]");
		this.algorithmParameterBean = algorithmParameterBean;
		this.text = "";
		this.name = algorithmParameterBean.getName();
		this.type = algorithmParameterBean.getType().toString();
		this.textFieldRenderer = new JTextField();
		this.textFieldRenderer.setColumns(10);
		this.textFieldRenderer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				algorithmParameterBean.setValue(e.getActionCommand());
			}
		});
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AlgorithmParameterBean getAlgorithmParameterBean() {
		return this.algorithmParameterBean;
	}

	public void setAlgorithmParameterBean(
			AlgorithmParameterBean algorithmParameterBean) {
		this.algorithmParameterBean = algorithmParameterBean;
	}

	public JTextField getTextFieldRenderer() {
		return this.textFieldRenderer;
	}

	public void setTextFieldRenderer(JTextField textFieldRenderer) {
		this.textFieldRenderer = textFieldRenderer;
	}
}
