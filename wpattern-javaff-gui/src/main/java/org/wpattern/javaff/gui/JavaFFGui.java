package org.wpattern.javaff.gui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javaff.search.data.Search;

import org.apache.log4j.Logger;
import org.wpattern.javaff.gui.windows.MainWindow;

public class JavaFFGui {

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(JavaFFGui.class);

	private static final String DEFAULT_PROBLEMS_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\problems";

	/**
	 * Launch the application.
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		List<String> problemsPath = new ArrayList<String>();

		problemsPath.add(DEFAULT_PROBLEMS_PATH);
		problemsPath.addAll(Arrays.asList(args));

		new JavaFFGui().startApplication(problemsPath);
	}

	@SuppressWarnings("unchecked")
	public void startApplication(final List<String> problemsPaths, final Class<? extends Search>... algorithms) {
		if (logger.isInfoEnabled()) {
			logger.info("JavaFF GUI started.");
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MainWindow(problemsPaths, Arrays.asList(algorithms));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}
}
