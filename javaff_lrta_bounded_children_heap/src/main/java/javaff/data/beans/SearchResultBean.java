package javaff.data.beans;

import javaff.data.Plan;

public class SearchResultBean extends BaseBean {

	private static final long serialVersionUID = 201303071301L;

	private final Plan plan;

	private final boolean planFounded;

	private final long planningTime;

	private final long groundingTime;

	private final long instantiateTime;

	private final String errorMessage;

	public SearchResultBean(String errorMessage) {
		this.planFounded = false;
		this.plan = null;
		this.planningTime = 0;
		this.groundingTime = 0;
		this.instantiateTime = 0;
		this.errorMessage = errorMessage;
	}

	public SearchResultBean(Plan plan, long instantiateTime, long groundingTime, long planningTime) {
		super();
		this.planFounded = true;
		this.plan = plan;
		this.planningTime = planningTime;
		this.groundingTime = groundingTime;
		this.instantiateTime = instantiateTime;
		this.errorMessage = null;
	}

	public Plan getPlan() {
		return this.plan;
	}

	public boolean isPlanFounded() {
		return this.planFounded;
	}

	public long getPlanningTime() {
		return this.planningTime;
	}

	public long getGroundingTime() {
		return this.groundingTime;
	}

	public long getInstantiateTime() {
		return this.instantiateTime;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

}
