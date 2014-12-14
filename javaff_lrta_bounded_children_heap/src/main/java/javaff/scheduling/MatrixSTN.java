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

package javaff.scheduling;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javaff.data.Action;
import javaff.data.AlgorithmConstants;
import javaff.data.Plan;
import javaff.data.TimeStampedPlan;
import javaff.data.strips.InstantAction;
import javaff.data.strips.OperatorName;
import javaff.data.strips.STRIPSInstantAction;
import javaff.data.temporal.DurativeAction;
import javaff.data.temporal.EndInstantAction;
import javaff.data.temporal.StartInstantAction;

public class MatrixSTN implements SimpleTemporalNetwork
{
	static BigDecimal EPSILON = AlgorithmConstants.EPSILON;
	static BigDecimal ZERO = new BigDecimal(0);
	static BigDecimal INF = new BigDecimal(100000);
	static BigDecimal NEG_EPSILON = EPSILON.negate();
	static int SCALE = 2;
	static int ROUND = BigDecimal.ROUND_HALF_EVEN;

	BigDecimal[][] TheArray;
	ArrayList Timepoints;
	Plan pop;
	int Size;

	InstantAction START = new STRIPSInstantAction();

	public MatrixSTN(Plan plan)
	{
		this.pop = plan;
		this.START.name = new OperatorName("TIME_ZERO");

		this.Timepoints = new ArrayList();
		this.Timepoints.add(this.START);
		this.Timepoints.addAll(this.pop.getActions());

		ZERO = ZERO.setScale(SCALE, ROUND);
		INF = INF.setScale(SCALE, ROUND);
		NEG_EPSILON = NEG_EPSILON.setScale(SCALE, ROUND);

		this.Size = this.Timepoints.size();
		this.TheArray = new BigDecimal[this.Size][this.Size];
		this.TheArray[0][0] = ZERO;
		for (int i = 1; i < this.Size; ++i)
		{
			this.TheArray[0][i] = INF;
			this.TheArray[i][0] = NEG_EPSILON;

			for (int j=1; j < this.Size; ++j)
			{
				if (i==j) this.TheArray[i][j] = ZERO;
				else this.TheArray[i][j] = INF;
			}
		}
	}


	@Override
	public void addConstraints(Set constraints)
	{
		Iterator oit = constraints.iterator();
		while (oit.hasNext())
		{
			TemporalConstraint c = (TemporalConstraint) oit.next();
			this.addConstraint(c);
		}
	}

	@Override
	public void addConstraint(TemporalConstraint c)
	{
		int firstpos = this.Timepoints.indexOf(c.y);
		int secondpos = this.Timepoints.indexOf(c.x);
		this.TheArray[firstpos][secondpos] = this.TheArray[firstpos][secondpos].min(c.b).setScale(SCALE, ROUND);
	}

	public void constrain()
	{
		for (int k = 0; k< this.Size; ++k)
		{
			for (int i = 0; i < this.Size; ++i)
			{
				for (int j = 0; j < this.Size; ++j)
				{
					if (this.TheArray[i][j].compareTo(this.TheArray[i][k].add(this.TheArray[k][j]))>0)
					{
						this.TheArray[i][j] = this.TheArray[i][k].add(this.TheArray[k][j]).setScale(SCALE, ROUND);
					}
				}
			}
		}
	}

	public boolean check()
	{
		for (int i = 0; i < this.Size; ++i)
		{
			if (this.TheArray[i][i].compareTo(ZERO) < 0)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean consistent()
	{
		this.constrain();
		return this.check();
	}

	@Override
	public boolean consistentSource(InstantAction a)
	{
		return this.consistent();
	}

	public TimeStampedPlan getTimes()
	{
		TimeStampedPlan plan = new TimeStampedPlan();
		Iterator ait = this.Timepoints.iterator();
		while (ait.hasNext())
		{
			InstantAction a = (InstantAction) ait.next();;
			if (a instanceof StartInstantAction)
			{
				DurativeAction da = ((StartInstantAction)a).parent;
				BigDecimal time = this.TheArray[this.Timepoints.indexOf(a)][0].negate().setScale(SCALE,ROUND);
				BigDecimal dur = this.TheArray[this.Timepoints.indexOf(da.endAction)][0].negate().subtract(time).setScale(SCALE,ROUND);
				plan.addAction(da, time, dur);
			}
			else if (a instanceof STRIPSInstantAction && a != this.START)
			{
				BigDecimal time = this.TheArray[this.Timepoints.indexOf(a)][0].negate().setScale(SCALE,ROUND);
				plan.addAction(a, time);
			}
		}
		return plan;
	}

	public boolean B(Action a, Action b)
	{
		BigDecimal v = this.TheArray[this.Timepoints.indexOf(b)][this.Timepoints.indexOf(a)];
		return (v.compareTo(ZERO) < 0);
	}

	public boolean BS(Action a, Action b)
	{
		BigDecimal v = this.TheArray[this.Timepoints.indexOf(b)][this.Timepoints.indexOf(a)];
		return (v.compareTo(ZERO) <= 0);
	}

	public boolean U(Action a, Action b)
	{
		BigDecimal v1 = this.TheArray[this.Timepoints.indexOf(b)][this.Timepoints.indexOf(a)];
		BigDecimal v2 = this.TheArray[this.Timepoints.indexOf(b)][this.Timepoints.indexOf(a)];
		return (v1.compareTo(ZERO) > 0 && v2.compareTo(ZERO) > 0);
	}

	public Action getEarliest(Set s)
	{
		Iterator sit = s.iterator();
		Action c = null;
		while (sit.hasNext())
		{
			Action a = (Action) sit.next();
			if (c==null) c = a;
			else if (this.TheArray[this.Timepoints.indexOf(c)][0].compareTo(this.TheArray[this.Timepoints.indexOf(a)][0])>0) c = a;
		}
		return c;
	}

	public BigDecimal getMinimum(DurativeAction da)
	{
		return this.TheArray[this.Timepoints.indexOf(da.endAction)][this.Timepoints.indexOf(da.startAction)].negate();

	}

	public BigDecimal getMaximum(DurativeAction da)
	{
		return this.TheArray[this.Timepoints.indexOf(da.startAction)][this.Timepoints.indexOf(da.endAction)];
	}

	public void increaseMin(DurativeAction da, BigDecimal diff)
	{
		BigDecimal v1 = this.TheArray[this.Timepoints.indexOf(da.endAction)][this.Timepoints.indexOf(da.startAction)];
		BigDecimal v2 = v1.subtract(diff);
		this.TheArray[this.Timepoints.indexOf(da.endAction)][this.Timepoints.indexOf(da.startAction)] = v1.min(v2);
	}

	public void decreaseMax(DurativeAction da, BigDecimal diff)
	{
		BigDecimal v1 = this.TheArray[this.Timepoints.indexOf(da.startAction)][this.Timepoints.indexOf(da.endAction)];
		BigDecimal v2 = v1.subtract(diff);
		this.TheArray[this.Timepoints.indexOf(da.startAction)][this.Timepoints.indexOf(da.endAction)] = v1.min(v2);
	}

	public void maximize(DurativeAction da)
	{
		this.TheArray[this.Timepoints.indexOf(da.endAction)][this.Timepoints.indexOf(da.startAction)] = this.TheArray[this.Timepoints.indexOf(da.startAction)][this.Timepoints.indexOf(da.endAction)].negate();
	}

	public void minimize(DurativeAction da)
	{
		this.TheArray[this.Timepoints.indexOf(da.startAction)][this.Timepoints.indexOf(da.endAction)] = this.TheArray[this.Timepoints.indexOf(da.endAction)][this.Timepoints.indexOf(da.startAction)].negate();
	}

	public void minimizeTime()
	{
		Iterator ait = this.Timepoints.iterator();
		while (ait.hasNext())
		{
			InstantAction a = (InstantAction) ait.next();;
			if (a instanceof EndInstantAction)
			{
				int i = this.Timepoints.indexOf(a);
				if (this.TheArray[i][0].compareTo(this.TheArray[0][i].negate()) != 0)
				{
					this.TheArray[0][i] = this.TheArray[i][0].negate();
					this.constrain();
				}
			}
		}



	}

	public void minimizeDuration()
	{
		Iterator ait = this.Timepoints.iterator();
		while (ait.hasNext())
		{
			InstantAction a = (InstantAction) ait.next();;
			if (a instanceof StartInstantAction)
			{
				DurativeAction da = ((StartInstantAction)a).parent;
				this.minimize(da);
			}
		}
	}



	public void printArray()
	{
		System.out.print("                                       ");
		for (int i = 0; i < this.Size; ++i)
		{
			String istr = (new Integer(i)).toString();
			istr += " ";
			istr = "  "+istr.substring(0,2)+" ";
			System.out.print(istr);
		}
		System.out.println();
		for (int i = 0; i < this.Size; ++i)
		{
			String istr = (new Integer(i)).toString();
			istr += " ";
			istr = "  "+istr.substring(0,2)+" ";
			System.out.print((this.Timepoints.get(i).toString()+"                                                 ").substring(0,35)+istr);
			for (int j = 0; j < this.Size; ++j)
			{
				if (this.TheArray[i][j].compareTo(INF) == 0) System.out.print("INF  ");
				else System.out.print(this.TheArray[i][j]+"  ");
			}
			System.out.print("\n");
		}
	}


	@Override
	public Object clone() throws CloneNotSupportedException
	{
		MatrixSTN STN = (MatrixSTN) super.clone();
		STN.Size = this.Size;
		STN.Timepoints = this.Timepoints;
		STN.START = this.START;
		for (int i = 0; i< this.Size; ++i)
		{
			for (int j = 0; j < this.Size; ++j)
			{
				STN.TheArray[i][j] = this.TheArray[i][j].setScale(SCALE,ROUND);
			}
		}
		return STN;
	}


}
