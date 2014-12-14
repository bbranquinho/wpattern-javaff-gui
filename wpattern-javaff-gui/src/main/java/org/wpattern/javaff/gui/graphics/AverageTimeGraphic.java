package org.wpattern.javaff.gui.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javaff.data.beans.SearchResultBean;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.wpattern.javaff.gui.graphics.interfaces.IGraphic;

public class AverageTimeGraphic implements IGraphic {

	private Entry<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> domain;

	public AverageTimeGraphic(Entry<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> entryResultsByAlgorithmByProblemByDomain) {
		this.domain = entryResultsByAlgorithmByProblemByDomain;
	}

	@Override
	public DefaultCategoryDataset generateDataSet(){
		DefaultCategoryDataset  dataset = new DefaultCategoryDataset();
		String algorithmName;
		String problemName;
		int numberOfSuccessfulResult=0;
		double sumOfTime=0;
		double averageTime=0;
		// Algorithm
		for (Entry<Class<?>, Map<String, List<SearchResultBean>>> entryResultsByAlgorithmByProblem : this.domain.getValue().entrySet()) {
			Class<?> algorithm = entryResultsByAlgorithmByProblem.getKey();
			algorithmName = algorithm.getSimpleName();
			// Problem
			for (Entry<String, List<SearchResultBean>> entryResultsByAlgorithm : entryResultsByAlgorithmByProblem.getValue().entrySet()) {
				String problem = entryResultsByAlgorithm.getKey();
				File domainsAndProblemsDirectory = new File(problem);
				problemName  = domainsAndProblemsDirectory.getName();
				numberOfSuccessfulResult=0;
				sumOfTime=0;
				averageTime=0;
				// Results
				for (SearchResultBean result : entryResultsByAlgorithm.getValue()) {
					if ((result != null) && result.isPlanFounded()){
						numberOfSuccessfulResult++;
						sumOfTime+=result.getPlanningTime();
					}
				}
				averageTime=sumOfTime/numberOfSuccessfulResult;
				dataset.addValue(averageTime, algorithmName, problemName);
			}
		}
		return dataset;
	}

	@Override
	public ChartPanel buildChart(){
		DefaultCategoryDataset dataset = this.generateDataSet();
		final JFreeChart chart = ChartFactory.createLineChart(
				"Average Time",        // chart title
				"problem",               // domain axis label
				"time",                  // range axis label
				dataset,                 // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips?
				false                     // URL generator?  Not required...
				);

		// set chart background
		chart.setBackgroundPaint(Color.white);
		//set plot specifications
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		//CUSTOMIZE DOMAIN AXIS
		final CategoryAxis domainAxis = plot.getDomainAxis();

		//customize domain label position
		domainAxis.setCategoryLabelPositions(
				CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
				);

		//CUSTOMIZE RANGE AXIS
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		//CUSTOMIZE THE RENDERER
		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		//set dots or you can say shapes at a point
		renderer.setBaseShapesFilled(true);
		renderer.setBaseShapesVisible(true);

		//DISPLAY LINES TYPE
		Stroke stroke = new BasicStroke(
				3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		renderer.setBaseOutlineStroke(stroke);

		return new ChartPanel(chart);
	}

}
