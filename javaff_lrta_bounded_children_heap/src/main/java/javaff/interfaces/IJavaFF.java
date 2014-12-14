package javaff.interfaces;

import java.util.List;

import javaff.data.beans.SearchResultBean;
import javaff.injection.data.MapParameters;
import javaff.search.data.Search;

public interface IJavaFF {

	public SearchResultBean executeSearch(String domainFullname, String problemFullname, Class<? extends Search> algorithmClass,
			MapParameters mapFields);

	public SearchResultBean executeSearch(String domainFullname, String problemFullname, Class<? extends Search> algorithmClass,
			MapParameters mapFields, long limitTime);

	public List<Class<? extends Search>> findAlgorithms(String packageName);

}
