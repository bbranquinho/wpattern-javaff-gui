package org.wpattern.javaff.gui.components.beans;


public class DomainProblemBean {

	private String name;

	private String domainPath;

	private String problemPath;

	public DomainProblemBean(String name) {
		this.name = name;
	}

	public DomainProblemBean(String name, String domainPath) {
		this.name = name;
		this.domainPath = domainPath;;
	}

	public DomainProblemBean(String name, String domainPath, String problemPath) {
		this.name = name;
		this.domainPath = domainPath;
		this.problemPath = problemPath;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public String getDomainPath() {
		return this.domainPath;
	}

	public void setDomainPath(String domainPath) {
		this.domainPath = domainPath;
	}

	public String getProblemPath() {
		return this.problemPath;
	}

	public void setProblemPath(String problemPath) {
		this.problemPath = problemPath;
	}

}
