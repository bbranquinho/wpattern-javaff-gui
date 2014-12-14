package org.wpattern.javaff.gui.graphics.interfaces;

import javaff.data.beans.SearchResultBean;

import org.wpattern.javaff.gui.data.SearchResultKey;

public interface IGraphicManager {

	public void clearResults();

	public void addSearchResult(SearchResultKey searchResultKey, SearchResultBean searchResult);

	public void buildGraphic();

}