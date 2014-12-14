package org.wpattern.javaff.gui.components.elements;


import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

public class DomainProblemTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {

	private static final long serialVersionUID = 2655500930747729819L;

	private DomainProblemTreeNodeRenderer renderer = new DomainProblemTreeNodeRenderer();

	public DomainProblemTreeNodeEditor() {
	}

	@Override
	public Object getCellEditorValue() {
		JCheckBox checkbox = this.renderer.getLeafRenderer();
		DomainProblemTreeNode checkBoxNode = new DomainProblemTreeNode(checkbox.getText(), checkbox.isSelected());

		return checkBoxNode;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row) {

		Component editor = this.renderer.getTreeCellRendererComponent(tree, value,
				true, expanded, leaf, row, true);

		// editor always selected / focused
		ItemListener itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (DomainProblemTreeNodeEditor.this.stopCellEditing()) {
					DomainProblemTreeNodeEditor.this.fireEditingStopped();
				}
			}
		};

		if (editor instanceof JCheckBox) {
			((JCheckBox) editor).addItemListener(itemListener);
		}

		return editor;
	}

}