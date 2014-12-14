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

package javaff.planning;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javaff.data.Action;
import javaff.data.AlgorithmConstants;
import javaff.data.GroundCondition;
import javaff.data.Plan;
import javaff.data.TotalOrderPlan;
import javaff.data.strips.Proposition;

public class STRIPSState extends State implements Cloneable,Comparable<STRIPSState>
{
	public Set facts;
	public Set actions;

	protected TotalOrderPlan plan = new TotalOrderPlan();

	protected RelaxedPlanningGraph RPG;
	protected boolean RPCalculated = false;
	protected BigDecimal HValue = null;
	public TotalOrderPlan RelaxedPlan = null;
	public Set helpfulActions = null;

	public STRIPSState parent;
	public BigDecimal c = BigDecimal.ONE;
	public BigDecimal h = AlgorithmConstants.MAX_DURATION;
	public BigDecimal f;
	public STRIPSState supp;
	public boolean visited = false;
	private int cost = 0;

	public boolean isVisited() {
		return this.visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public BigDecimal getC() {
		return this.c;
	}

	public void setC(BigDecimal c) {
		this.c = c;
	}

	public BigDecimal getH() {
		return this.h;
	}

	public void setH(BigDecimal h) {
		this.h = h;
	}

	public BigDecimal getF() {
		return this.getC().add(this.getH());
	}

	public void setF(BigDecimal f) {
		this.f = f;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getCost() {
		return this.cost;
	}

	public STRIPSState getParent() {
		return this.parent;
	}

	public void setParent(STRIPSState parent) {
		this.parent = parent;
	}

	public STRIPSState getSupp() {
		return this.supp;
	}

	public void setSupp(STRIPSState supp) {
		this.supp = supp;
	}

	protected STRIPSState()
	{

	}

	public STRIPSState(Set a, Set f, GroundCondition g)
	{
		this.facts = f;
		this.goal = g;
		this.actions = a;
		//		filter = NullFilter.getInstance();
	}

	protected STRIPSState(Set a, Set f, GroundCondition g, TotalOrderPlan p)
	{
		this(a,f,g);
		this.plan = p;
	}

	@Override
	public Object clone()
	{
		Set nf = (Set) ((HashSet) this.facts).clone();
		TotalOrderPlan p = (TotalOrderPlan) this.plan.clone();
		STRIPSState SS = new STRIPSState(this.actions, nf, this.goal, p);
		SS.setRPG(this.RPG);
		SS.setH(this.getH());
		SS.setVisited(this.visited);
		SS.setParent(this.getParent());
		//		SS.setFilter(filter);
		return SS;
	}

	public void setRPG(RelaxedPlanningGraph rpg)
	{
		this.RPG = rpg;
	}

	public RelaxedPlanningGraph getRPG()
	{
		return this.RPG;
	}

	//	public Set getNextStates()     // get all the next possible states reachable from this state
	//	{
	//		return getNextStates(filter.getActions(this));
	//	}

	@Override
	public State apply(Action a)
	{
		STRIPSState s = (STRIPSState) super.apply(a);
		s.plan.addAction(a);
		return s;
	}

	public void addProposition(Proposition p)
	{
		this.facts.add(p);
	}

	public void removeProposition(Proposition p)
	{
		this.facts.remove(p);
	}

	public boolean isTrue(Proposition p)
	{
		return this.facts.contains(p);
	}

	@Override
	public Set getActions()
	{
		return this.actions;
	}

	public void calculateRP()
	{
		if (!this.RPCalculated)
		{
			//System.out.println("calc RP");
			this.RelaxedPlan = (TotalOrderPlan) this.RPG.getPlan(this);
			this.helpfulActions = new HashSet();
			if (!(this.RelaxedPlan == null))
			{
				this.HValue = new BigDecimal(this.RelaxedPlan.getPlanLength());

				Iterator it = this.RelaxedPlan.iterator();
				while (it.hasNext())
				{
					Action a = (Action) it.next();
					if (this.RPG.getLayer(a) == 0) this.helpfulActions.add(a);
				}
			}
			else this.HValue = AlgorithmConstants.MAX_DURATION;
			this.RPCalculated = true;
		}
	}

	@Override
	public BigDecimal getHValue()
	{
		return this.HValue;
	}

	public BigDecimal calculateRPHeuristic()
	{
		//System.out.println("calc RPHeuristic");
		BigDecimal heuristic = new BigDecimal(0);
		this.RelaxedPlan = (TotalOrderPlan) this.RPG.getPlan(this);
		this.helpfulActions = new HashSet();
		if (!(this.RelaxedPlan == null))
		{
			heuristic = new BigDecimal(this.RelaxedPlan.getPlanLength());

			Iterator it = this.RelaxedPlan.iterator();
			while (it.hasNext())
			{
				Action a = (Action) it.next();
				if (this.RPG.getLayer(a) == 0) this.helpfulActions.add(a);
			}
		}
		else heuristic = AlgorithmConstants.MAX_DURATION;
		this.RPCalculated = true;
		return heuristic;
	}


	@Override
	public BigDecimal getGValue()
	{
		return new BigDecimal(this.plan.getPlanLength());
	}

	@Override
	public Plan getSolution()
	{
		return this.plan;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof STRIPSState)
		{
			STRIPSState s = (STRIPSState) obj;
			return s.facts.equals(this.facts);
		}
		else return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash ^ this.facts.hashCode();
		return hash;
	}

	@Override
	public int compareTo(STRIPSState otherSTRIPSState) {
		if(this.getH().compareTo(otherSTRIPSState.getH()) == 0)
			return 0;
		if(this.getH().compareTo(otherSTRIPSState.getH()) < 0)
			return 1;
		return -1;
	}

	@Override
	public String toString() {
		return "STRIPSState [facts=" + this.facts + ", h=" + this.h + "]";
	}

}
