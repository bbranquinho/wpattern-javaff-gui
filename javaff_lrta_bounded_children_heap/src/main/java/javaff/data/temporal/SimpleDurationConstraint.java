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

package javaff.data.temporal;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Map;

import javaff.data.AlgorithmConstants;
import javaff.data.PDDLPrinter;
import javaff.data.metric.Function;
import javaff.data.metric.MetricSymbolStore;
import javaff.planning.MetricState;

public class SimpleDurationConstraint extends DurationConstraint
{
	protected int type;
	protected DurationFunction variable;
	protected Function value;

	public SimpleDurationConstraint(DurationFunction v, Function f, int t)
	{
		this.type = t;
		this.variable = v;
		this.value = f;
	}

	@Override
	public DurationConstraint ground(Map varMap)
	{
		return new SimpleDurationConstraint((DurationFunction) this.variable.ground(varMap), this.value.ground(varMap), this.type);
	}

	@Override
	public BigDecimal getDuration(MetricState ms)
	{
		return this.value.getValue(ms);
	}

	//could put stuff about < and > using epsilon
	@Override
	public BigDecimal getMaxDuration(MetricState ms)
	{
		if (this.type == MetricSymbolStore.LESS_THAN_EQUAL) {
			return this.value.getValue(ms);
		} else if (this.type == MetricSymbolStore.GREATER_THAN_EQUAL) {
			return AlgorithmConstants.MAX_DURATION;
		} else if (this.type == MetricSymbolStore.EQUAL) {
			return this.value.getValue(ms);
		}

		else return null;
	}

	@Override
	public BigDecimal getMinDuration(MetricState ms)
	{
		if (this.type == MetricSymbolStore.LESS_THAN_EQUAL) return new BigDecimal(0);
		else if (this.type == MetricSymbolStore.GREATER_THAN_EQUAL) return this.value.getValue(ms);
		else if (this.type == MetricSymbolStore.EQUAL) return this.value.getValue(ms);
		else return null;
	}

	@Override
	public boolean staticDuration()
	{
		//return value.isStatic();
		return (this.type == MetricSymbolStore.EQUAL);
	}


	public void addConstraint(SimpleDurationConstraint sdc)
	{

	}

	@Override
	public void PDDLPrint(PrintStream p, int indent)
	{
		PDDLPrinter.printToString(this, p, true, false, indent);
	}

	@Override
	public String toString()
	{
		String str = "(" + MetricSymbolStore.getSymbol(this.type)+" " + this.variable.toString() +" "+this.value.toString()+")";
		return str;
	}

	@Override
	public String toStringTyped()
	{
		String str = "(" + MetricSymbolStore.getSymbol(this.type)+" " + this.variable.toStringTyped() +" "+this.value.toStringTyped()+")";
		return str;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash ^ this.type;
		hash = 31 * hash ^ this.variable.hashCode();
		hash = 31 * hash ^ this.value.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SimpleDurationConstraint)
		{
			SimpleDurationConstraint c = (SimpleDurationConstraint) obj;
			return (this.type == c.type && this.variable.equals(c.variable) && this.value.equals(c.value));
		}
		else return false;
	}

}
