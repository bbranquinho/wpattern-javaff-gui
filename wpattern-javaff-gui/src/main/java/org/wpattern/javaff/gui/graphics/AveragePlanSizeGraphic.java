package org.wpattern.javaff.gui.graphics;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javaff.data.beans.SearchResultBean;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.wpattern.javaff.gui.graphics.interfaces.IGraphic;

public class AveragePlanSizeGraphic implements IGraphic{

	private Entry<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> domain;

	public AveragePlanSizeGraphic(Entry<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> entryResultsByAlgorithmByProblemByDomain) {
		this.domain = entryResultsByAlgorithmByProblemByDomain;
	}

	@Override
	public DefaultCategoryDataset generateDataSet(){
		DefaultCategoryDataset  dataset = new DefaultCategoryDataset();
		String algorithmName;
		String problemName;
		int numberOfSuccessfulResult=0;
		double sumOfSize=0;
		double averageSize=0;
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
				sumOfSize=0;
				averageSize=0;
				// Results
				for (SearchResultBean result : entryResultsByAlgorithm.getValue()) {
					if(result.isPlanFounded()){
						numberOfSuccessfulResult++;
						sumOfSize += result.getPlan().getActions().size();
					}
				}
				averageSize=sumOfSize/numberOfSuccessfulResult;
				dataset.addValue(averageSize, algorithmName, problemName);
			}
		}
		return dataset;
	}

	@Override
	public ChartPanel buildChart(){
		DefaultCategoryDataset dataset = this.generateDataSet();
		final JFreeChart chart = ChartFactory.createBarChart(
				"Average Plan Size",        // chart title
				"problem",               // domain axis label
				"plan size",                  // range axis label
				dataset,                 // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips?
				false                     // URL generator?  Not required...
				);

		// set chart background
		chart.setBackgroundPaint(Color.white);

		return new ChartPanel(chart);
	}

}
