package org.wpattern.javaff.gui.graphics.interfaces;

import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

public interface IGraphic {

	public DefaultCategoryDataset generateDataSet();
	public ChartPanel buildChart();
	
}
