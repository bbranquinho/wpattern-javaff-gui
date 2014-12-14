package javaff.search;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javaff.planning.STRIPSState;
import javaff.planning.State;
import javaff.search.data.Search;
import javaff.search.queue.LrtaPriorityQueue;

import org.apache.log4j.Logger;

public class AStar extends Search {

	private static final Logger logger = Logger.getLogger(AStar.class);

	private final MinMaxComparator minMaxComparator = new MinMaxComparator();

	public AStar(State s) {
		super(s);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public State search() {
		if (logger.isInfoEnabled()) {
			logger.info("Started search using the algorithm A*.");
		}

		LrtaPriorityQueue<STRIPSState> openLookahead = LrtaPriorityQueue.orderedBy(this.minMaxComparator).create();

		STRIPSState currentState = (STRIPSState) this.start;
		currentState.setH(currentState.calculateRPHeuristic());

		STRIPSState successorOpenHeap;

		Set<STRIPSState> closeLookahead = new HashSet<STRIPSState>();
		STRIPSState successor;

		openLookahead.add(currentState);

		while (!openLookahead.isEmpty() && !this.stopSearch) {
			currentState = openLookahead.remove();
			closeLookahead.add(currentState);

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("State [%s] with F = %s; G = %s; H = %s and C = %s.", currentState.hashCode(),
						currentState.getF(), currentState.getGValue(), currentState.getH(), currentState.getC()));
			}

			if (currentState.goalReached()) {
				return currentState;
			}

			Set successors = currentState.getNextStates(this.filter.getActions(currentState));
			Iterator<STRIPSState> successorIterator = successors.iterator();

			while (successorIterator.hasNext()) {
				successor = successorIterator.next();

				if (closeLookahead.contains(successor)) {
					continue;
				}

				successorOpenHeap = openLookahead.getElement(successor);

				if (successorOpenHeap == null) {
					successor.setH(successor.calculateRPHeuristic());
					openLookahead.add(successor);
				} else {
					if (successor.getGValue().compareTo(successorOpenHeap.getGValue()) > 0) {
						if (logger.isDebugEnabled()) {
							logger.debug(String.format("Update the heuristic G of state [%s] from [%s] to [%s].", successor,
									successor.getGValue(), successorOpenHeap.getGValue()));
						}

						openLookahead.add(successor);
					}
				}
			}
		}

		return null;
	}

	private class MinMaxComparator implements Comparator<STRIPSState> {

		@Override
		public int compare(STRIPSState state1, STRIPSState state2) {
			BigDecimal state1F = state1.getGValue().add(state1.getH());
			BigDecimal state2F = state2.getGValue().add(state2.getH());

			return state1F.compareTo(state2F);
		}

	}

}
