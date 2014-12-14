package org.wpattern.javaff.gui.data;

import java.util.List;

import javaff.JavaFF;
import javaff.data.beans.SearchResultBean;
import javaff.injection.data.MapParameters;
import javaff.search.data.Search;

import org.apache.log4j.Logger;
import org.wpattern.javaff.gui.components.beans.AlgorithmBean;
import org.wpattern.javaff.gui.components.beans.DomainProblemBean;
import org.wpattern.javaff.gui.graphics.interfaces.IGraphicManager;
import org.wpattern.javaff.gui.interfaces.IAlgorithms;
import org.wpattern.javaff.gui.interfaces.IComponentManager;
import org.wpattern.javaff.gui.interfaces.IDomainsAndProblems;
import org.wpattern.javaff.gui.interfaces.ISearchProcessor;

public class SearchProcessor implements ISearchProcessor {

	private final Logger logger = Logger.getLogger(this.getClass());

	private final String PROCESSOR_NAME = "SearchProcessor-Thread-" + System.currentTimeMillis();

	private final IDomainsAndProblems domainsAndProblems;

	private final IAlgorithms algorithms;

	private final IComponentManager componentManager;

	private final IGraphicManager graphicManager;

	private Thread threadProcessor;

	public SearchProcessor(IGraphicManager graphicManager, IComponentManager componentManager, IDomainsAndProblems domainsAndProblems,
			IAlgorithms algorithms) {
		this.graphicManager = graphicManager;
		this.componentManager = componentManager;
		this.domainsAndProblems = domainsAndProblems;
		this.algorithms = algorithms;
	}

	@Override
	public void executeSearch(Integer limitTime, Integer amountRepetitions) {
		if ((this.threadProcessor != null) && this.threadProcessor.isAlive()) {
			this.logger.warn(String.format("Thread [%s] already running.", this.threadProcessor.getName()));
			return;
		}

		this.graphicManager.clearResults();

		Processor processor = new Processor(limitTime, amountRepetitions);
		this.threadProcessor = new Thread(processor, this.PROCESSOR_NAME);
		this.threadProcessor.start();
	}

	private class Processor implements Runnable {

		private final Logger logger = Logger.getLogger(this.getClass());

		private final Integer limitTime;

		private Integer amountRepetitions;

		public Processor(Integer limitTime, Integer amountRepetitions) {
			this.limitTime = limitTime;
			this.amountRepetitions = amountRepetitions;
		}

		@Override
		public void run() {
			SearchProcessor.this.componentManager.disableComponents();

			try {
				// Execute the search based on user definition and catch all exceptions to log it.
				this.executeSearch();
			} catch (Exception e) {
				this.logger.error(e.getMessage(), e);
			} catch (Throwable e) {
				this.logger.error(e.getMessage(), e);
			}

			SearchProcessor.this.componentManager.enableComponents();
		}

		private void executeSearch() {
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Starting process of search for plans.");
			}

			if (this.amountRepetitions == null) {
				this.amountRepetitions = 1;
			}

			List<DomainProblemBean> domains = SearchProcessor.this.domainsAndProblems.getDomainsAndProblems();

			for (DomainProblemBean domain : domains) {
				for (AlgorithmBean algorithm : SearchProcessor.this.algorithms.getAlgorithms()) {
					if (algorithm.isSelected()) {
						for (int i = 1; i <= this.amountRepetitions; i++) {
							if (this.logger.isInfoEnabled()) {
								this.logger.info(String.format("Searching with parameters Number [%s], Domain [%s], Problem [%s] and Algorithm [%s].",
										i, domain.getName(), domain.getProblemPath(), algorithm.getName()));
							}

							this.encapsulateSearch(domain.getDomainPath(), domain.getProblemPath(), algorithm.getClasses(), algorithm.getParameters());
						}
					}
				}
			}

			SearchProcessor.this.graphicManager.buildGraphic();

			if (this.logger.isInfoEnabled()) {
				this.logger.info("Ended the search for plans.");
			}
		}

		private void encapsulateSearch(final String domainPath, final String problemPath, final Class<? extends Search> algorithmClass,
				final MapParameters algorithmParameters) {
			SearchResultBean searchResult;

			if ((this.limitTime != null) && (this.limitTime > 0)) {
				searchResult = new JavaFF().executeSearch(domainPath, problemPath, algorithmClass, algorithmParameters, this.limitTime);
			} else {
				searchResult = new JavaFF().executeSearch(domainPath, problemPath, algorithmClass, algorithmParameters);
			}

			if (this.logger.isInfoEnabled()) {
				this.logger.info(String.format("Result of the search [%s].", searchResult));
			}

			SearchProcessor.this.graphicManager.addSearchResult(new SearchResultKey(domainPath, problemPath, algorithmClass), searchResult);

			System.gc();
		}

	}

}
