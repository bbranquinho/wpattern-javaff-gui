package org.wpattern.javaff.gui.components.elements;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;

import org.wpattern.javaff.gui.components.beans.AlgorithmBean;

public class ListAlgorithmMouseEvent extends MouseAdapter {

	private JPanel panel;

	private GridLayout gridLayout;

	public ListAlgorithmMouseEvent(JPanel panelAlgorithmParameters, GridLayout gridLayout) {
		this.panel = panelAlgorithmParameters;
		this.gridLayout = gridLayout;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		JList<?> list = (JList<?>) event.getSource();

		// Get index of item clicked
		int index = list.locationToIndex(event.getPoint());
		AlgorithmBean item = (AlgorithmBean) list.getModel().getElementAt(index);

		// Toggle selected state
		item.setSelected(!item.isSelected());

		if (item.isSelected()) {
			// Load parameter components.
			this.loadParameterComponents(item);
		} else {
			this.clearParameterComponents();
		}

		// Repaint cell
		list.repaint(list.getCellBounds(index, index));
	}

	private void loadParameterComponents(AlgorithmBean item) {
		this.panel.removeAll();
		this.gridLayout.setRows(1);

		//MapParameters parameters = item.getParameters();

		//for (AlgorithmParameterBean parameter : parameters.getValues()) {
		//this.panel.add(new AlgorithmParameterPanel(parameter));
		this.gridLayout.setRows(this.gridLayout.getRows() + 1);
		//}

		this.panel.revalidate();
	}

	private void clearParameterComponents() {
		this.panel.removeAll();
		this.gridLayout.setRows(1);
		this.panel.revalidate();
	}

}
