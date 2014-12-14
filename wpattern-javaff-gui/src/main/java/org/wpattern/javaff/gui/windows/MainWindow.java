package org.wpattern.javaff.gui.windows;

import java.util.List;

import javaff.search.data.Search;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.wpattern.javaff.gui.components.AlgorithmsPanel;
import org.wpattern.javaff.gui.components.DomainsProblemsPanel;
import org.wpattern.javaff.gui.components.SearchPanel;
import org.wpattern.javaff.gui.graphics.GraphicManager;
import org.wpattern.javaff.gui.interfaces.IComponentManager;

public class MainWindow implements IComponentManager {

	private final JFrame frmJavaff = new JFrame();

	private UIManager.LookAndFeelInfo[] looks =  UIManager.getInstalledLookAndFeels();

	private DomainsProblemsPanel domainsPanel;

	private AlgorithmsPanel algorithmsPanel;

	private SearchPanel logPanel;

	/**
	 * Create the application.
	 */
	public MainWindow(List<String> problemPaths, List<Class<? extends Search>> algorithms) {
		this.initialize(problemPaths, algorithms);
		this.frmJavaff.setVisible(true);

		try {
			UIManager.setLookAndFeel(this.looks[1].getClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this.frmJavaff);
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param problemsPaths
	 * 			Path with problems and domains.
	 * @param algorithms
	 * 			Algorithms of search.
	 */
	private void initialize(List<String> problemsPaths, List<Class<? extends Search>> algorithms) {
		this.frmJavaff.setTitle("JavaFF");
		this.frmJavaff.setBounds(100, 100, 1011, 600);
		this.frmJavaff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frmJavaff.getContentPane().setLayout(new MigLayout("", "[230.00,fill][320.00,fill][grow]", "[grow]"));
		this.frmJavaff.setExtendedState(this.frmJavaff.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		this.domainsPanel = new DomainsProblemsPanel(problemsPaths);
		this.frmJavaff.getContentPane().add(this.domainsPanel, "cell 0 0,grow");

		this.algorithmsPanel = new AlgorithmsPanel(algorithms);
		this.frmJavaff.getContentPane().add(this.algorithmsPanel, "cell 1 0,grow");

		this.logPanel = new SearchPanel(new GraphicManager(), this, this.domainsPanel, this.algorithmsPanel);
		this.frmJavaff.getContentPane().add(this.logPanel, "cell 2 0,grow");
	}

	@Override
	public void disableComponents() {
		this.domainsPanel.setEnabled(false);
		this.algorithmsPanel.setEnabled(false);
		this.logPanel.setEnabled(false);
	}

	@Override
	public void enableComponents() {
		this.domainsPanel.setEnabled(true);
		this.algorithmsPanel.setEnabled(true);
		this.logPanel.setEnabled(true);
	}

}
