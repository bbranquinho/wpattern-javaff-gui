/************************************************************************
 * Strathclyde Planning Group,
 * Department of Computer and Information Sciences,
 * University of Strathclyde, Glasgow, UK
 * http://planning.cis.strath.ac.uk/
 * 
 * Copyright 2007, Keith Halsey
 * Copyright 2008, Andrew Coles and Amanda Smith
 *
 * (Questions/bug reports now to be sent to Andrew Coles)
 *
 * This file is part of JavaFF.
 * 
 * JavaFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JavaFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JavaFF.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package javaff;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javaff.annotation.AlgorithmDefinition;
import javaff.data.GroundProblem;
import javaff.data.Plan;
import javaff.data.TotalOrderPlan;
import javaff.data.UngroundProblem;
import javaff.data.beans.SearchResultBean;
import javaff.injection.InjectorManager;
import javaff.injection.data.ErrorMessages;
import javaff.injection.data.MapParameters;
import javaff.injection.exceptions.InjectionException;
import javaff.interfaces.IJavaFF;
import javaff.parser.PDDL21parser;
import javaff.planning.HelpfulFilter;
import javaff.planning.NullFilter;
import javaff.planning.State;
import javaff.planning.TemporalMetricState;
import javaff.search.data.Search;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

public class JavaFF implements IJavaFF {

	private static final Logger logger = Logger.getLogger(JavaFF.class);

	// FIXME augusto.branquinho: Move these variables to each method.
	private volatile SearchResultBean searchResult;

	private volatile Search algorithmInstance;

	@Override
	public SearchResultBean executeSearch(String domainFullname, String problemFullname, Class<? extends Search> algorithmClass, MapParameters mapFields) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Executing a search to domain [%s] and problem [%s].", domainFullname, problemFullname));
		}

		File domainFile = new File(domainFullname);
		File problemFile = new File(problemFullname);

		// Get the initial state.
		long startTime = System.currentTimeMillis();

		State initialState = this.retrieveInitialState(domainFile, problemFile);

		long groundingTime = System.currentTimeMillis() - startTime;

		// Search for a plan.
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Grouding time [%s milliseconds].", groundingTime));
		}

		// Occurs an error.
		if (initialState == null) {
			return new SearchResultBean(ErrorMessages.ERROR_TO_CREATE_INITIAL_STATE);
		}

		// Create a instance of type "algorithmClass".
		startTime = System.currentTimeMillis();

		this.algorithmInstance = this.instantiateAlgorithm(initialState, algorithmClass, mapFields);

		// Occurs an error.
		if (this.algorithmInstance == null) {
			return null;
		}

		// End time of after instantiation.
		long instantiateTime = System.currentTimeMillis() - startTime;

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Time spent to instantiate the algorithm [%s] is [%s milliseconds].",
					algorithmClass,	instantiateTime));
		}

		// Start the search.
		startTime = System.currentTimeMillis();

		Plan plan = this.buildPlan(domainFile, problemFile, this.algorithmInstance);

		// Time after the planning.
		long planningTime = System.currentTimeMillis() - startTime;

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Time spent planning [%s milliseconds].", planningTime));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Plan founded [%s].", plan));
		}

		return new SearchResultBean(plan, instantiateTime, groundingTime, planningTime);
	}

	@Override
	public SearchResultBean executeSearch(final String domainFullname, final String problemFullname, final Class<? extends Search> algorithmClass,
			final MapParameters mapFields, long limitTime) {
		this.searchResult = null;

		final Thread currentThread = Thread.currentThread();
		final Thread searchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				JavaFF.this.searchResult = JavaFF.this.executeSearch(domainFullname, problemFullname, algorithmClass, mapFields);

				synchronized (currentThread) {
					currentThread.interrupt();
				}
			}
		}, "SearchThread-" + limitTime);

		searchThread.start();

		synchronized (currentThread) {
			try {
				currentThread.wait(limitTime * 1000);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}

		if (searchThread.isAlive()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Stop the thread used for search.");
			}

			if (this.algorithmInstance != null) {
				this.algorithmInstance.stopSearch();
			}

			this.searchResult = new SearchResultBean("Limit of time reached.");
		}

		return this.searchResult;
	}

	@Override
	public List<Class<? extends Search>> findAlgorithms(String packageName) {
		Reflections reflections = new Reflections(packageName);

		List<Class<? extends Search>> algorithms = new ArrayList<Class<? extends Search>>(reflections.getSubTypesOf(Search.class));

		Collections.sort(algorithms, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> algorithm1, Class<?> algorithm2) {
				return algorithm1.getSimpleName().compareTo(algorithm2.getSimpleName());
			}
		});

		return algorithms;
	}

	private Search instantiateAlgorithm(State initialState, Class<? extends Search> algorithmClass, MapParameters mapFields) {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Start instantiation of algorithm [%s].", algorithmClass));
		}

		try {
			// Instantiate the algorithm.
			Constructor<? extends Search> constructor = algorithmClass.getConstructor(State.class);

			Search algorithmInstance = constructor.newInstance(initialState);

			// Inject all parameter values.
			InjectorManager.injectAllValues(algorithmInstance, mapFields);

			// Set the "filter".
			this.processAlgorithmDefinition(algorithmInstance);

			return algorithmInstance;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
				IllegalArgumentException | InvocationTargetException | InjectionException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private TemporalMetricState retrieveInitialState(File domainFile, File problemFile) {
		// Parse and ground the problem.
		if (logger.isTraceEnabled()) {
			logger.trace("Parse and ground the problem.");
		}

		UngroundProblem unground = PDDL21parser.parseFiles(domainFile, problemFile);

		if (unground == null) {
			logger.error("Parsing error - see log for details.");
			return null;
		}

		GroundProblem ground = unground.ground();

		// Get the initial state
		TemporalMetricState initialState = ground.getTemporalMetricInitialState();

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Initial state [%s].", initialState));
		}

		return initialState;
	}

	private Plan buildPlan(File domainFile, File problemFile, Search algorithmInstance) {
		// Search for a plan.
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Starting search for a plan using the algorithm [%s].", algorithmInstance.getClass().getSimpleName()));
		}

		// Return the "plan"/"goal state".
		State goalState = algorithmInstance.search();

		TotalOrderPlan totalOrderPlan = null;

		if (goalState != null) {
			totalOrderPlan = (TotalOrderPlan) goalState.getSolution();
		}

		return totalOrderPlan;
	}

	private void processAlgorithmDefinition(Search algorithmInstance) {
		Class<? extends Search> algorithmClass = algorithmInstance.getClass();

		AlgorithmDefinition algorithmDefinition = algorithmClass.getAnnotation(AlgorithmDefinition.class);

		// Set default values of algorithm definition.
		if (algorithmDefinition == null) {
			algorithmInstance.setFilter(HelpfulFilter.getInstance());
			return;
		}

		switch (algorithmDefinition.filterType()) {
		case HELPFUL_FILTER:
			algorithmInstance.setFilter(HelpfulFilter.getInstance());
			break;

		case NULL_FILTER:
			algorithmInstance.setFilter(NullFilter.getInstance());
			break;

		default:
			logger.warn(String.format("Filter type [%s] not defined.", algorithmDefinition.filterType()));
			break;
		}
	}

}
