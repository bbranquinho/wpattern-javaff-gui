package org.wpattern.javaff.gui.components;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.wpattern.javaff.gui.components.elements.ComboBoxLevelRenderer;
import org.wpattern.javaff.gui.data.SearchProcessor;
import org.wpattern.javaff.gui.graphics.interfaces.IGraphicManager;
import org.wpattern.javaff.gui.interfaces.IAlgorithms;
import org.wpattern.javaff.gui.interfaces.IComponentManager;
import org.wpattern.javaff.gui.interfaces.IDomainsAndProblems;
import org.wpattern.javaff.gui.interfaces.ISearchProcessor;

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 201301290141L;

	private final Logger logger = Logger.getLogger(this.getClass());

	private final String JAVAFF_GUI_APPENDER_NAME = "org.wpattern.javaff.gui";

	private final String JAVAFF_APPENDER_NAME = "javaff";

	private final int MAX_CHARACTERS = 10;

	private final ISearchProcessor searchProcessor;

	private JButton btnClearLog;

	private JButton btnSearch;

	private JButton btnStop;

	private JEditorPane editorLoggerPanel;

	private JTextField textLimitOfTime;

	private JTextField textlAmountOfRepetitions;

	private JComboBox<Level> comboBoxLevel;

	private Map<Level, ImageIcon> iconsLevel;

	private JToolBar toolBar;

	public SearchPanel(IGraphicManager graphicManager, IComponentManager componentManager, IDomainsAndProblems domainsAndProblems, IAlgorithms algorithms) {
		this.searchProcessor = new SearchProcessor(graphicManager, componentManager, domainsAndProblems, algorithms);

		// Initialize all components.
		this.intializeComponents();

		// Set all events for each component.
		this.settingComponentEvents();

		// Add some appenders to log4j.
		this.addAppenders();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		this.btnStop.setEnabled(!enabled);
		this.btnSearch.setEnabled(enabled);
		this.textLimitOfTime.setEditable(enabled);
		this.textlAmountOfRepetitions.setEnabled(enabled);
	}

	private void intializeComponents() {
		this.setLayout(new MigLayout("", "[114.00,grow]", "[][20px,grow,top]"));

		this.toolBar = new JToolBar("Formatting");

		this.add(this.toolBar,"cell 0 0");

		this.btnSearch = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/run.png"), "Run"));
		this.btnSearch.setToolTipText("Run");
		this.toolBar.add(this.btnSearch);

		this.toolBar.addSeparator();

		this.btnStop = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/stop.png"),"Stop"));
		this.btnStop.setToolTipText("Stop");
		this.btnStop.setEnabled(false);
		this.toolBar.add(this.btnStop);

		this.toolBar.addSeparator();

		this.btnClearLog = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/clean.png"),"Clean Log"));
		this.btnClearLog.setToolTipText("Clear Log");
		this.toolBar.add(this.btnClearLog);

		this.toolBar.addSeparator();

		this.iconsLevel = new HashMap<Level, ImageIcon>();
		this.iconsLevel.put(Level.TRACE, new ImageIcon(ClassLoader.getSystemResource("images/trace.png"), Level.TRACE.toString()));
		this.iconsLevel.put(Level.DEBUG, new ImageIcon(ClassLoader.getSystemResource("images/debug.png"), Level.DEBUG.toString()));
		this.iconsLevel.put(Level.INFO, new ImageIcon(ClassLoader.getSystemResource("images/info.png"), Level.INFO.toString()));
		this.iconsLevel.put(Level.WARN, new ImageIcon(ClassLoader.getSystemResource("images/warn.png"), Level.WARN.toString()));
		this.iconsLevel.put(Level.ERROR, new ImageIcon(ClassLoader.getSystemResource("images/error.png"), Level.ERROR.toString()));
		this.iconsLevel.put(Level.FATAL, new ImageIcon(ClassLoader.getSystemResource("images/fatal.png"), Level.FATAL.toString()));

		this.comboBoxLevel = new JComboBox<Level>(new Level[] { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL });

		this.comboBoxLevel.setRenderer(new ComboBoxLevelRenderer<Level>(this.iconsLevel));

		JLabel labelLogLevel = new JLabel("Log Level: ");
		labelLogLevel.setFont(new Font("Arial", Font.BOLD, 12));
		this.toolBar.add(labelLogLevel);
		this.toolBar.add(this.comboBoxLevel);
		this.comboBoxLevel.setSelectedItem(Logger.getLogger(this.JAVAFF_APPENDER_NAME).getLevel());

		this.toolBar.addSeparator();

		JLabel lblLimitOfTime = new JLabel("Limit of Time (Seconds):");
		lblLimitOfTime.setFont(new Font("Arial", Font.BOLD, 12));
		this.toolBar.add(lblLimitOfTime);
		this.toolBar.add(lblLimitOfTime);

		this.textLimitOfTime = new JTextField();
		this.toolBar.add(this.textLimitOfTime);
		this.textLimitOfTime.setColumns(10);

		this.toolBar.addSeparator();

		JLabel lblAmountOfRepetitions = new JLabel("Number of Iterations: ");
		lblAmountOfRepetitions.setFont(new Font("Arial", Font.BOLD, 12));
		this.toolBar.add(lblAmountOfRepetitions);

		this.textlAmountOfRepetitions = new JTextField();
		this.toolBar.add(this.textlAmountOfRepetitions);
		this.textlAmountOfRepetitions.setColumns(10);
		this.textlAmountOfRepetitions.setText("1");

		this.toolBar.addSeparator();

		JPanel panel = new JPanel();
		this.add(panel,"cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[106px,grow,fill]", "[20px,grow,fill]"));

		this.editorLoggerPanel = new JEditorPane();
		panel.add(this.editorLoggerPanel, "cell 0 0,grow");

		panel.add(new JScrollPane(this.editorLoggerPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

	private void settingComponentEvents() {
		// Validate input of chars.
		this.textLimitOfTime.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char character = e.getKeyChar();

				if (!((character >= '0') && (character <= '9') || (character == KeyEvent.VK_BACK_SPACE) ||
						(character == KeyEvent.VK_DELETE)) || (SearchPanel.this.textLimitOfTime.getText().length() > SearchPanel.this.MAX_CHARACTERS)) {
					e.consume();
				}
			}
		});

		// Validate input of chars.
		this.textlAmountOfRepetitions.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char character = e.getKeyChar();

				if (!((character >= '0') && (character <= '9') || (character == KeyEvent.VK_BACK_SPACE) ||
						(character == KeyEvent.VK_DELETE)) || (SearchPanel.this.textlAmountOfRepetitions.getText().length() > SearchPanel.this.MAX_CHARACTERS)) {
					e.consume();
				}
			}
		});

		// Catch messages of log.
		this.comboBoxLevel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent item) {
				Logger.getLogger(SearchPanel.this.JAVAFF_APPENDER_NAME).setLevel((Level)item.getItem());
				Logger.getLogger(SearchPanel.this.JAVAFF_GUI_APPENDER_NAME).setLevel((Level)item.getItem());
			}
		});

		// Clear the log.
		this.btnClearLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.editorLoggerPanel.setText("");
			}
		});

		// Listener to process the search.
		this.btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Integer limitTime = null, amountRepetitions = 1;

				if (!SearchPanel.this.textLimitOfTime.getText().isEmpty()) {
					limitTime = Integer.parseInt(SearchPanel.this.textLimitOfTime.getText());
				}

				if (!SearchPanel.this.textlAmountOfRepetitions.getText().isEmpty()) {
					amountRepetitions = Integer.parseInt(SearchPanel.this.textlAmountOfRepetitions.getText());
				}

				try {
					SearchPanel.this.searchProcessor.executeSearch(limitTime, amountRepetitions);
				} catch (Exception ex) {
					SearchPanel.this.logger.error(ex.getMessage(), ex);
				}
			}
		});
	}

	private void addAppenders() {
		// Adding some appenders.
		JavaFFAppender javaFFAppender = new JavaFFAppender();
		Logger.getLogger(this.JAVAFF_APPENDER_NAME).addAppender(javaFFAppender);
		Logger.getLogger(this.JAVAFF_GUI_APPENDER_NAME).addAppender(javaFFAppender);
	}

	private class JavaFFAppender extends AppenderSkeleton {

		@Override
		public void close() {}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(final LoggingEvent event) {
			if (event == null) {
				return;
			}

			String errorMessage = "";

			if (event.getMessage() != null) {
				errorMessage = event.getMessage().toString();
			} else if (event.getThrowableInformation() != null) {
				for (String throwableInformation : event.getThrowableInformation().getThrowableStrRep()) {
					errorMessage += throwableInformation + "\n";
				}
			} else {
				return;
			}

			final String message = errorMessage;

			// Without this runnable can throw the error "Interrupted attempt to acquire write lock".
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (!message.isEmpty()) {
						SearchPanel.this.editorLoggerPanel.setText(String.format("%s=>%s - %s\n",
								SearchPanel.this.editorLoggerPanel.getText(), event.getLevel(), message));
					} else {
						SearchPanel.this.editorLoggerPanel.setText(String.format("%s\n", SearchPanel.this.editorLoggerPanel.getText()));
					}

					SearchPanel.this.editorLoggerPanel.setCaretPosition(SearchPanel.this.editorLoggerPanel.getDocument().getLength());
				}
			});
		}

	}

}
