package org.wpattern.javaff.gui.components;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javaff.JavaFF;
import javaff.injection.InjectorManager;
import javaff.injection.data.AlgorithmParameterBean;
import javaff.injection.data.AlgorithmParameterType;
import javaff.search.data.Search;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.miginfocom.swing.MigLayout;

import org.wpattern.javaff.gui.components.beans.AlgorithmBean;
import org.wpattern.javaff.gui.components.elements.AlgorithmParameterTreeNode;
import org.wpattern.javaff.gui.components.elements.AlgorithmTreeNode;
import org.wpattern.javaff.gui.components.elements.AlgorithmTreeNodeEditor;
import org.wpattern.javaff.gui.components.elements.AlgorithmTreeNodeRenderer;
import org.wpattern.javaff.gui.interfaces.IAlgorithms;

public class AlgorithmsPanel extends JPanel implements IAlgorithms {

	private static final long serialVersionUID = 201301270721L;

	private final String ALGORITHM_PACKAGE = "javaff.search";

	private JTree treeAlgorithms;

	public AlgorithmsPanel(List<Class<? extends Search>> algorithms) {
		this.loadAlgorithms(algorithms);
		this.initializeComponents();
	}

	@Override
	public List<AlgorithmBean> getAlgorithms() {
		List<AlgorithmBean> algorithms = new ArrayList<AlgorithmBean>();
		AlgorithmTreeNode rootNode = (AlgorithmTreeNode) this.treeAlgorithms.getModel().getRoot();
		Enumeration<?> rootEnumeration = rootNode.children();

		while (rootEnumeration.hasMoreElements()) {
			AlgorithmTreeNode algorithmNode = (AlgorithmTreeNode) rootEnumeration.nextElement();

			if (algorithmNode.isSelected()) {
				algorithms.add(algorithmNode.getAlgorithm());

				Enumeration<?> parametersEnumeration = algorithmNode.children();

				while (parametersEnumeration.hasMoreElements()) {
					AlgorithmParameterTreeNode parameterNode = (AlgorithmParameterTreeNode) parametersEnumeration.nextElement();
					parameterNode.getAlgorithmParameterBean().setValue(parameterNode.getTextFieldRenderer().getText());

					String name = parameterNode.getAlgorithmParameterBean().getName();
					String value = parameterNode.getAlgorithmParameterBean().getValue();
					AlgorithmParameterType type = parameterNode.getAlgorithmParameterBean().getType();

					algorithmNode.getAlgorithm().addParameter(name, value, type);
				}
			}
		}

		return algorithms;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.treeAlgorithms.setEnabled(enabled);
	}

	private void initializeComponents() {
		this.add(new JScrollPane(this.treeAlgorithms, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		this.treeAlgorithms.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeStructureChanged(TreeModelEvent e) {}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				if (e.getChildren()[0] instanceof AlgorithmTreeNode) {
					AlgorithmTreeNode treeNode = (AlgorithmTreeNode) e.getChildren()[0];
					treeNode.setSelected(!treeNode.isSelected());
				}
			}
		});
	}

	private void loadAlgorithms(List<Class<? extends Search>> algorithms) {
		List<Class<? extends Search>> foundedAlgorithms = new JavaFF().findAlgorithms(this.ALGORITHM_PACKAGE);

		foundedAlgorithms.addAll(algorithms);

		AlgorithmTreeNode rootNode = new AlgorithmTreeNode("Algorithms", false);
		AlgorithmTreeNodeRenderer renderer = new AlgorithmTreeNodeRenderer();

		for (Class<? extends Search> algorithm : foundedAlgorithms) {
			AlgorithmBean algorithmBean = new AlgorithmBean(algorithm);
			algorithmBean.setParameters(InjectorManager.getParameters(algorithm));
			AlgorithmTreeNode algorithmTreeNode = new AlgorithmTreeNode(algorithmBean);
			rootNode.add(algorithmTreeNode);

			for (AlgorithmParameterBean parameter : algorithmBean.getParameters().getValues()) {
				AlgorithmParameterTreeNode algorithmParameterTreeNode = new AlgorithmParameterTreeNode(parameter);
				algorithmTreeNode.add(algorithmParameterTreeNode);
			}
		}

		this.treeAlgorithms= new JTree(rootNode);
		this.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		this.treeAlgorithms.setCellRenderer(renderer);
		this.treeAlgorithms.setCellEditor(new AlgorithmTreeNodeEditor());
		this.treeAlgorithms.setEditable(true);
	}

}
