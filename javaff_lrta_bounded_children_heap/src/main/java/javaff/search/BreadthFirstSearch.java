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

import java.util.Hashtable;
import java.util.LinkedList;

import javaff.planning.State;
import javaff.search.data.Search;

public class BreadthFirstSearch extends Search
{
	protected LinkedList<State> open;

	protected Hashtable<Integer, State> closed;

	public BreadthFirstSearch(State s)
	{
		super(s);
		this.open = new LinkedList<State>();
		this.closed = new Hashtable<Integer, State>();
	}

	@SuppressWarnings("unchecked")
	public void updateOpen(State S)
	{
		this.open.addAll(S.getNextStates(this.filter.getActions(S)));
	}

	public State removeNext()
	{
		return this.open.removeFirst();
	}

	public boolean needToVisit(State s) {
		Integer Shash = new Integer(s.hashCode());
		State D = this.closed.get(Shash);

		if (this.closed.containsKey(Shash) && D.equals(s)) return false;

		this.closed.put(Shash, s);
		return true;
	}

	@Override
	public State search() {

		this.open.add(this.start);

		while (!this.open.isEmpty() && !this.stopSearch)
		{
			State s = this.removeNext();
			if (this.needToVisit(s)) {
				++this.nodeCount;
				if (s.goalReached()) {
					return s;
				} else {
					this.updateOpen(s);
				}
			}

		}
		return null;
	}
}
