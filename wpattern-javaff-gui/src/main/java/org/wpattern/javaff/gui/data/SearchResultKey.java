package org.wpattern.javaff.gui.data;

import javaff.search.data.Search;

public class SearchResultKey {

	private final String domainPath;

	private final String problemPath;

	private final Class<? extends Search> algorithmClass;

	private final int hashCode;

	public SearchResultKey(String domainPath, String problemPath, Class<? extends Search> algorithmClass) {
		this.domainPath = domainPath;
		this.problemPath = problemPath;
		this.algorithmClass = algorithmClass;
		this.hashCode = this.calculateHashCode();
	}

	public String getDomainPath() {
		return this.domainPath;
	}

	public String getProblemPath() {
		return this.problemPath;
	}

	public Class<? extends Search> getAlgorithmClass() {
		return this.algorithmClass;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		SearchResultKey other = (SearchResultKey) obj;
		if (this.algorithmClass == null) {
			if (other.algorithmClass != null)
				return false;
		} else if (!this.algorithmClass.equals(other.algorithmClass))
			return false;
		if (this.domainPath == null) {
			if (other.domainPath != null)
				return false;
		} else if (!this.domainPath.equals(other.domainPath))
			return false;
		if (this.problemPath == null) {
			if (other.problemPath != null)
				return false;
		} else if (!this.problemPath.equals(other.problemPath))
			return false;

		return true;
	}

	private int calculateHashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.algorithmClass == null) ? 0 : this.algorithmClass.hashCode());
		result = prime * result + ((this.domainPath == null) ? 0 : this.domainPath.hashCode());
		result = prime * result + ((this.problemPath == null) ? 0 : this.problemPath.hashCode());

		return result;
	}

}
