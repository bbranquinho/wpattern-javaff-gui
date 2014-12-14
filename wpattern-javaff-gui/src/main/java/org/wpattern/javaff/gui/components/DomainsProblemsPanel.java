package org.wpattern.javaff.gui.components;


import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.wpattern.javaff.gui.components.beans.DomainProblemBean;
import org.wpattern.javaff.gui.components.elements.DomainProblemTreeNode;
import org.wpattern.javaff.gui.components.elements.DomainProblemTreeNodeEditor;
import org.wpattern.javaff.gui.components.elements.DomainProblemTreeNodeRenderer;
import org.wpattern.javaff.gui.interfaces.IDomainsAndProblems;
import org.wpattern.javaff.gui.utils.ErrorMessages;

public class DomainsProblemsPanel extends JPanel implements IDomainsAndProblems {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2810197289925111144L;

	private static final String ROOT_NODE_NAME = "Domains and Problems";

	private final Logger logger = Logger.getLogger(this.getClass());

	private final String DOMAIN_FILE_EXTENSION = ".pddl";

	private JTree treeDomainsAndProblems;

	private List<String> problemsPaths;

	public DomainsProblemsPanel(List<String> problemsPaths) {
		this.problemsPaths = problemsPaths;
		this.initializeComponents();
	}

	@Override
	public List<DomainProblemBean> getDomainsAndProblems() {
		List<DomainProblemBean> domainsAndProblems = new ArrayList<DomainProblemBean>();
		DomainProblemTreeNode rootNode = (DomainProblemTreeNode) this.treeDomainsAndProblems.getModel().getRoot();
		Enumeration<?> domainEnumeration = rootNode.getPath()[0].children();

		while (domainEnumeration.hasMoreElements()) {
			DomainProblemTreeNode domainNode = (DomainProblemTreeNode) domainEnumeration.nextElement();
			Enumeration<?> problemEnumeration = domainNode.children();

			while (problemEnumeration.hasMoreElements()) {
				DomainProblemTreeNode problemNode = (DomainProblemTreeNode) problemEnumeration.nextElement();

				if (problemNode.isSelected()) {
					domainsAndProblems.add(problemNode.getDomainProblem());
				}
			}
		}

		return domainsAndProblems;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.treeDomainsAndProblems.setEnabled(enabled);
	}

	private void initializeComponents() {
		this.setLayout(new MigLayout("", "[192px,grow,fill]", "[286px,grow,fill]"));

		this.treeDomainsAndProblems = new JTree(this.getTreeRootNode());

		this.add(this.treeDomainsAndProblems, "cell 0 0,alignx left,growy");
		this.add(new JScrollPane(this.treeDomainsAndProblems, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		DomainProblemTreeNodeRenderer renderer = new DomainProblemTreeNodeRenderer();

		this.treeDomainsAndProblems.setCellRenderer(renderer);
		this.treeDomainsAndProblems.setCellEditor(new DomainProblemTreeNodeEditor());
		this.treeDomainsAndProblems.setEditable(true);

		this.treeDomainsAndProblems.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeStructureChanged(TreeModelEvent e) {}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				DomainProblemTreeNode treeNode = (DomainProblemTreeNode) e.getChildren()[0];
				treeNode.setSelected(!treeNode.isSelected());
			}
		});
	}

	private DomainProblemTreeNode getTreeRootNode() {
		DomainProblemTreeNode rootNode = new DomainProblemTreeNode(ROOT_NODE_NAME);

		for (String path : this.problemsPaths) {
			if (this.logger.isInfoEnabled()) {
				this.logger.info(String.format("Loading domains and problems from the path [%s].", path));
			}

			File domainsAndProblemsDirectory = new File(path);

			if (domainsAndProblemsDirectory.isDirectory()) {
				for (File domainFile : domainsAndProblemsDirectory.listFiles()) {
					if (domainFile.isDirectory() && this.containsDomain(domainFile)) {
						DomainProblemTreeNode domainRoot = new DomainProblemTreeNode(domainFile.getName());

						rootNode.add(domainRoot);

						for (File problemFile : domainFile.listFiles()) {
							if (problemFile.isFile() && !this.isDomainFile(problemFile.getName())) {
								DomainProblemBean domainProblemBean = new DomainProblemBean(problemFile.getName(),
										this.getDomainFilePath(domainFile), problemFile.getAbsolutePath());
								domainRoot.add(new DomainProblemTreeNode(domainProblemBean));
							}
						}
					}
				}
			} else {
				this.logger.warn(String.format(ErrorMessages.DOMAINS_AND_PROBLEMS_PATH_NOT_EXIST, path));
			}
		}

		return rootNode;
	}

	private String getDomainFilePath(File domainFile) {
		for (File file : domainFile.listFiles()) {
			if (file.isFile() && this.isDomainFile(file.getAbsolutePath())) {
				return file.getAbsolutePath();
			}
		}

		this.logger.error(String.format("Domain file not found in the path [%s].", domainFile.getAbsolutePath()));

		return null;
	}

	private boolean isDomainFile(String filename) {
		return filename.lastIndexOf(this.DOMAIN_FILE_EXTENSION) > 0;
	}

	private boolean containsDomain(File fileDirectory) {
		for (File file : fileDirectory.listFiles()) {
			if (file.isFile() && this.isDomainFile(file.getName())) {
				return true;
			}
		}

		return false;
	}

}
