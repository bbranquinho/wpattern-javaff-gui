package org.wpattern.javaff.gui.components.elements;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.wpattern.javaff.gui.components.beans.AlgorithmBean;

public class ListAlgorithmRenderer extends JCheckBox implements ListCellRenderer<AlgorithmBean> {

	private static final long serialVersionUID = 201301251545L;

	@Override
	public Component getListCellRendererComponent(JList<? extends AlgorithmBean> list, AlgorithmBean value,
			int index, boolean isSelected, boolean cellHasFocus) {

		this.setEnabled(list.isEnabled());
		this.setSelected(value.isSelected());
		this.setFont(list.getFont());
		this.setBackground(list.getBackground());
		this.setForeground(list.getForeground());
		this.setText(value.toString());

		return this;
	}

}
