package org.wpattern.javaff.gui.components.elements;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import org.wpattern.javaff.gui.components.beans.ProblemBean;

public class ListProblemMouseEvent extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent event) {
		JList<?> list = (JList<?>) event.getSource();

		// Get index of item clicked
		int index = list.locationToIndex(event.getPoint());
		ProblemBean item = (ProblemBean) list.getModel().getElementAt(index);

		// Toggle selected state
		item.setSelected(!item.isSelected());

		// Repaint cell
		list.repaint(list.getCellBounds(index, index));
	}

}
