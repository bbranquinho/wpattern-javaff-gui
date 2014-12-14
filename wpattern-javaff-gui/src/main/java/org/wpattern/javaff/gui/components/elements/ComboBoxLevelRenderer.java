package org.wpattern.javaff.gui.components.elements;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboBoxLevelRenderer<T> extends JLabel implements ListCellRenderer<T> {

	private static final long serialVersionUID = 201303041530L;

	private Map<T, ImageIcon> iconsLevel = null;

	public ComboBoxLevelRenderer(Map<T, ImageIcon> iconsLevel) {
		this.setOpaque(true);
		this.setHorizontalAlignment(LEFT);
		this.iconsLevel = iconsLevel;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			this.setBackground(list.getSelectionBackground());
			this.setForeground(list.getSelectionForeground());
		} else {
			this.setBackground(list.getBackground());
			this.setForeground(list.getForeground());
		}

		this.setIcon(this.iconsLevel.get(value));
		this.setText(this.iconsLevel.get(value).getDescription());
		this.setPreferredSize(new Dimension(50, 30));

		return this;
	}

}
