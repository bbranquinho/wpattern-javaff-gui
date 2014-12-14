package org.wpattern.javaff.gui.graphics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javaff.data.beans.SearchResultBean;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import org.wpattern.javaff.gui.data.SearchResultKey;
import org.wpattern.javaff.gui.graphics.interfaces.IGraphicManager;

public class GraphicManager implements IGraphicManager {

	// Map<Domain, Map<Algorithm, Map<Problem, List<SearchResult>>>>
	private Map<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> resultsByProblemByAlgorithmByDomain;

	//Frames for render domains graphics
	List<JFrame> frames;

	public GraphicManager() {
		this.resultsByProblemByAlgorithmByDomain = new HashMap<String, Map<Class<?>, Map<String, List<SearchResultBean>>>>();
	}

	@Override
	public void addSearchResult(SearchResultKey searchResultKey, SearchResultBean searchResult) {
		String domain = searchResultKey.getDomainPath();
		String problem = searchResultKey.getProblemPath();
		Class<?> algorithm = searchResultKey.getAlgorithmClass();

		Map<Class<?>, Map<String, List<SearchResultBean>>> resultsByProblemByAlgorithm = this.resultsByProblemByAlgorithmByDomain.get(domain);

		if (resultsByProblemByAlgorithm == null) {
			resultsByProblemByAlgorithm = new HashMap<Class<?>, Map<String, List<SearchResultBean>>>();
			this.resultsByProblemByAlgorithmByDomain.put(domain, resultsByProblemByAlgorithm);
		}

		Map<String, List<SearchResultBean>> resultsByProblem = resultsByProblemByAlgorithm.get(algorithm);

		if (resultsByProblem == null) {
			resultsByProblem = new HashMap<String, List<SearchResultBean>>();
			resultsByProblemByAlgorithm.put(algorithm, resultsByProblem);
		}

		List<SearchResultBean> results = resultsByProblem.get(problem);

		if (results == null) {
			results = new ArrayList<SearchResultBean>();
			resultsByProblem.put(problem, results);
		}

		results.add(searchResult);
	}

	@Override
	public void buildGraphic() {
		//for each domain
		this.frames = new ArrayList<JFrame>();
		for(Entry<String, Map<Class<?>, Map<String, List<SearchResultBean>>>> entryResultsByAlgorithmByProblemByDomain : this.resultsByProblemByAlgorithmByDomain.entrySet()) {
			String domain = entryResultsByAlgorithmByProblemByDomain.getKey();
			File domainsAndProblemsDirectory = new File(domain);
			String domainName  = domainsAndProblemsDirectory.getParentFile().getName();
			JFrame frame = new JFrame("Domain: "+domainName);
			frame.getContentPane().setLayout(new MigLayout());
			frame.setBounds(20, 20, 50, 50);

			//Average Time Graphic
			AverageTimeGraphic averageTimeGraphic = new AverageTimeGraphic(entryResultsByAlgorithmByProblemByDomain);
			frame.getContentPane().add(averageTimeGraphic.buildChart(), "cell 0 0,grow");

			//Average Plan Size Graphic
			AveragePlanSizeGraphic averagePlanSizeGraphic = new AveragePlanSizeGraphic(entryResultsByAlgorithmByProblemByDomain);
			frame.getContentPane().add(averagePlanSizeGraphic.buildChart(), "cell 1 0,grow");

			this.frames.add(frame);
		}

		for(JFrame frame : this.frames){
			frame.pack();
			frame.setVisible(true);
		}

	}

	@Override
	public void clearResults() {
		this.resultsByProblemByAlgorithmByDomain.clear();
	}

}
