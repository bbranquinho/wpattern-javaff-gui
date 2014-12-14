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

package javaff.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javaff.annotation.AlgorithmParameter;
import javaff.data.AlgorithmConstants;
import javaff.planning.STRIPSState;
import javaff.planning.State;
import javaff.search.data.HValueComparator;
import javaff.search.data.PriorityQueueHashTable;
import javaff.search.data.Search;

import org.apache.log4j.Logger;

public class LrtaBoundedSuccessorsPriorityQueueSearch extends Search {

	private final Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Best heuristic value for a set of successors.
	 */
	private BigDecimal bestFValue;

	/**
	 * Heap using a priority queue that keeps the states at a bounded memory.
	 */
	private PriorityQueueHashTable<STRIPSState> heap;

	/**
	 * Set of successors that have the same value of the best heuristic value.
	 */
	private List<STRIPSState> bestSuccessors;

	/**
	 * Max capacity of heap.
	 */
	@AlgorithmParameter(required = false)
	private Integer heapCapacity = 4000;

	/**
	 *  Rate of successors to be save during the expansion of the current state.
	 */
	@AlgorithmParameter(required = false)
	private Double percentOfSuccessorsToSave = 0.7;

	private Random generator;

	public LrtaBoundedSuccessorsPriorityQueueSearch(State s) {
		this(s, new HValueComparator<State>());
	}

	public LrtaBoundedSuccessorsPriorityQueueSearch(State s, Comparator<?> c) {
		super(s);
		this.generator = new Random();
		this.setComparator(c);
		this.heap = new PriorityQueueHashTable<STRIPSState>(this.heapCapacity);
	}

	@Override
	public State search() {
		if (this.logger.isInfoEnabled()) {
			this.logger.info("LRTA* : prune sucessor and memory bound with priority queue.");
		}

		// Validate start state, generate its relaxed heuristic.
		this.validateState((STRIPSState) this.start);
		STRIPSState currentState = (STRIPSState) this.start;

		// Add current state to the heap that keeps the states at memory.
		this.heap.insert(currentState);

		// While the current state is not the goal.
		while (!currentState.goalReached() && !this.stopSearch) {
			// remove worst states from heap until hit the low limit
			this.heap.restoreCapacity();

			// Reset best heuristic value with the highest value and take its heuristic value as the best so
			// far expand the successor of the current state.
			this.bestFValue = AlgorithmConstants.MAX_DURATION;

			@SuppressWarnings("unchecked")
			Set<STRIPSState> successors = currentState.getNextStates(this.filter.getActions(currentState));

			// Add the parent state of the current state to the successors, that makes possible to come back
			// to the parent state if the children states are worse.
			successors.add(currentState.getParent());

			if (this.logger.isDebugEnabled()) {
				this.logger.debug(String.format("Number of state at heap [%s].", this.heap.capacity()));
				this.logger.debug(String.format("Current State: heuristic = [%s] and hashcode = [%s].",
						currentState.getH(), currentState.hashCode()));
			}

			this.bestSuccessors = new ArrayList<STRIPSState>();
			Iterator<STRIPSState> succItr = successors.iterator();

			if (this.logger.isDebugEnabled()) {
				this.logger.debug(String.format("Number of successors: ", successors.size()));
			}

			// For each successors.
			while (succItr.hasNext()) {
				STRIPSState succ = succItr.next();

				// Avoid loops at the same state in cases that a state generate its self as successor.
				if (succ != null && !succ.equals(currentState)) {
					// Validate state, set its heuristic or calculate it.
					this.validateState(succ);

					// Stop if the successor is the goal.
					if (succ.goalReached()) {
						return succ;
					}

					// set its parent as current state
					succ.setParent(currentState);

					if (this.logger.isDebugEnabled()) {
						this.logger.debug(String.format("Successor: heuristc = [%s] and hashcode = [%s] IN HEAP [%s].",
								succ.getH(), succ.hashCode(), succ.isVisited()));
					}

					// If successor is better then any one, clear list of best successors, update best
					// heuristic and add it to this list.
					int comparedHeuristic = succ.getF().compareTo(this.bestFValue);

					if (comparedHeuristic < 0) {
						// Note the new best value.
						this.bestFValue = succ.getF();
						this.bestSuccessors.clear();
						this.bestSuccessors.add(succ);
					} else if (comparedHeuristic == 0) {
						// If it has the same heuristic as the best one, just add it to the list of best successors.
						this.bestSuccessors.add(succ);
					}
				}
			}

			// Update current state heuristics as best F value (C+H) and update it at heap.
			currentState.setH(this.bestFValue);
			this.heap.insert(currentState);
			State parent = currentState.getParent();

			// Select best successor from the best successors list, random choice if there is more than one.
			if (this.bestSuccessors.size() == 1) {
				currentState = this.bestSuccessors.get(0);
				this.heap.insert(currentState);

				if (this.logger.isDebugEnabled()) {
					this.logger.debug(String.format("Add Best Successor to heap: heuristc = [%s] and hashcode = [%s].",
							currentState.getH(), currentState.hashCode()));
				}
			} else {
				int rand = this.generator.nextInt(this.bestSuccessors.size());

				if (this.logger.isDebugEnabled()) {
					this.logger.debug(String.format("[%s] <- rd  size->  [%s]", rand, this.bestSuccessors.size()));
					this.logger.debug(String.format("size->  ", this.bestSuccessors.size()));
				}

				currentState = this.bestSuccessors.get(rand);
				this.heap.insert(currentState);

				if (this.logger.isDebugEnabled()) {
					this.logger.debug(String.format("Add Best Successor to heap: heuristc = [%s] and  hashcode = [%s].",
							currentState.getH(), currentState.hashCode()));
				}

				this.bestSuccessors.remove(rand);

				// Add just 70% of remains best successors to reduce elements in memory.
				int numberStatesToSave = (int) Math.round(this.bestSuccessors.size() * this.percentOfSuccessorsToSave);

				while (numberStatesToSave > 0) {
					numberStatesToSave--;
					STRIPSState successorSave = this.bestSuccessors.get(numberStatesToSave);
					this.heap.insert(successorSave);

					if (this.logger.isDebugEnabled()) {
						this.logger.debug(String.format("Add 70 percent of bestSuccessor to heap: heuristc = [%s] and  hashcode = [%s].",
								successorSave.getH(), successorSave.hashCode()));
					}
				}
			}

			// Test just for fun, if the search is going back to the tree of search.
			if (this.logger.isDebugEnabled()) {
				if (parent != null && parent.equals(currentState)) {
					this.logger.debug("Going back");
				} else {
					this.logger.debug(String.format("Moving to heuristic [%s].", currentState.getH()));
				}
			}
		}

		return null;
	}

	// get a state that is in the heap to keep the real heuristic or generate a
	// relaxed heuristic if its not in the heap
	private void validateState(STRIPSState s) {
		STRIPSState state = this.heap.getElement(s);

		// if state in heap, return it with its real heuristic
		if (state != null) {
			s.setH(state.getH());
			s.setVisited(true);
		} else { // calculate its heuristic and return it
			s.setH(s.calculateRPHeuristic());
			s.setVisited(false);
		}
	}

}
