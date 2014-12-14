package org.wpattern.javaff.gui.components.elements;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class DomainProblemTreeNodeRenderer implements TreeCellRenderer {
	private JCheckBox leafRenderer = new JCheckBox();

	private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

	Color selectionBorderColor, selectionForeground, selectionBackground,
	textForeground, textBackground;

	protected JCheckBox getLeafRenderer() {
		return this.leafRenderer;
	}

	public DomainProblemTreeNodeRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");

		if (fontValue != null) {
			this.leafRenderer.setFont(fontValue);
		}

		Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		this.leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));

		this.selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		this.selectionForeground = UIManager.getColor("Tree.selectionForeground");
		this.selectionBackground = UIManager.getColor("Tree.selectionBackground");
		this.textForeground = UIManager.getColor("Tree.textForeground");
		this.textBackground = UIManager.getColor("Tree.textBackground");
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component returnValue;

		if (leaf) {
			String stringValue = tree.convertValueToText(value, selected,
					expanded, leaf, row, false);
			this.leafRenderer.setText(stringValue);
			this.leafRenderer.setSelected(false);

			this.leafRenderer.setEnabled(tree.isEnabled());

			if (selected) {
				this.leafRenderer.setForeground(this.selectionForeground);
				this.leafRenderer.setBackground(this.selectionBackground);
			} else {
				this.leafRenderer.setForeground(this.textForeground);
				this.leafRenderer.setBackground(this.textBackground);
			}

			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

				if (userObject instanceof DomainProblemTreeNode) {
					DomainProblemTreeNode node = (DomainProblemTreeNode) userObject;

					this.leafRenderer.setText(node.getText());
					this.leafRenderer.setSelected(node.isSelected());
				}
			}

			returnValue = this.leafRenderer;
		} else {
			returnValue = this.nonLeafRenderer.getTreeCellRendererComponent(tree,
					value, selected, expanded, leaf, row, hasFocus);
		}

		return returnValue;
	}
}