package org.wpattern.javaff.gui.components.elements;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class AlgorithmTreeNodeRenderer implements TreeCellRenderer {

	//Default render for Algorithms title
	private DefaultTreeCellRenderer algorithmTitleRenderer = new DefaultTreeCellRenderer();

	//Checkbox for Algorithms
	private JCheckBox algorithmRenderer = new JCheckBox();

	//JPanel with JLabel and JTextField for Algorithm Parameters
	private JPanel algorithmParameterRenderer = new JPanel();

	private JLabel algorithmParameterLabelRenderer = new JLabel();

	private Color selectionForeground, selectionBackground, textForeground, textBackground;

	public AlgorithmTreeNodeRenderer() {
	}

	protected JCheckBox getLeafRenderer() {
		return this.algorithmRenderer;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,boolean hasFocus) {

		//title
		if(row == 0){
			return this.algorithmTitleRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}

		//algorithms
		if (value instanceof AlgorithmTreeNode) {

			String stringValue = tree.convertValueToText(value, selected,expanded, leaf, row, false);
			this.algorithmRenderer.setText(stringValue);
			this.algorithmRenderer.setSelected(false);
			this.algorithmRenderer.setEnabled(tree.isEnabled());

			if (selected) {
				this.algorithmRenderer.setForeground(this.selectionForeground);
				this.algorithmRenderer.setBackground(this.selectionBackground);
			} else {
				this.algorithmRenderer.setForeground(this.textForeground);
				this.algorithmRenderer.setBackground(this.textBackground);
			}

			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof AlgorithmTreeNode) {
					AlgorithmTreeNode node = (AlgorithmTreeNode) userObject;
					this.algorithmRenderer.setText(node.getName());
					this.algorithmRenderer.setSelected(node.isSelected());
				}
			}
			return this.algorithmRenderer;
		}

		//parameters
		String stringValue = tree.convertValueToText(value, selected,expanded, leaf, row, false);

		this.algorithmParameterRenderer.removeAll();
		this.algorithmParameterLabelRenderer.setText(stringValue);
		this.algorithmParameterRenderer.add(this.algorithmParameterLabelRenderer);
		this.algorithmParameterRenderer.add(((AlgorithmParameterTreeNode)value).getTextFieldRenderer());

		return this.algorithmParameterRenderer;
	}

}
