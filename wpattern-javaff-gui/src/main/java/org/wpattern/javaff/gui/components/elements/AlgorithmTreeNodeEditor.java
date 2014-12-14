package org.wpattern.javaff.gui.components.elements;


import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

public class AlgorithmTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {


	private static final long serialVersionUID = 2655500930747729819L;

	private AlgorithmTreeNodeRenderer renderer = new AlgorithmTreeNodeRenderer();

	public AlgorithmTreeNodeEditor() {
	}

	@Override
	public Object getCellEditorValue() {
		JCheckBox checkbox = this.renderer.getLeafRenderer();
		AlgorithmTreeNode checkBoxNode = new AlgorithmTreeNode(checkbox.getText(), checkbox.isSelected());
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
				if (AlgorithmTreeNodeEditor.this.stopCellEditing()) {
					AlgorithmTreeNodeEditor.this.fireEditingStopped();
				}
			}
		};
		if (editor instanceof JCheckBox) {
			((JCheckBox) editor).addItemListener(itemListener);
		}

		return editor;
	}
}