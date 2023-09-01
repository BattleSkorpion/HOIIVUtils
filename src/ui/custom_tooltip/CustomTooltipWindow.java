package ui.custom_tooltip;

import hoi4utils.HOIIVUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ui.HOIUtilsWindow;

public class CustomTooltipWindow extends HOIUtilsWindow {
	@FXML
	public Label idVersion;
	public Label idWindowName;

	public CustomTooltipWindow() {
		fxmlResource = "CustomTooltipWindow.fxml";
		title = "HOIIVUtils Custom ToolTip Localization Window";
	}

	@FXML
	void initialize() {
		includeVersion();
		idWindowName.setText("CustomTooltipWindow" + " WIP");
	}

	private void includeVersion() {
		idVersion.setText(HOIIVUtils.hoi4utilsVersion);
	}
}
// 	private JPanel CustomTooltipWindowJPanel;
// 	private JTextField tooltipFileTextField;
// 	private JTextField localizationFileTextField;
// 	private JTable customTooltipTable;
// 	private JButton saveChangesButton;
// 	private DefaultTableModel customTooltipTableModel;
// 	private File tooltipFile;
// 	private LocalizationFile localizationFile;
// 	private CustomTooltip[] customTooltips;

// 	public CustomTooltipWindow() {
// 		super("Custom Tooltip Window");

// 		customTooltipTableModel = new DefaultTableModel() {
// 			@Override
// 			public int getRowCount() {
// 				if (customTooltips == null) {
// 					return 1;
// 				}
// 				if (customTooltips.length == 0) {
// 					return 1;
// 				}
// 				return customTooltips.length;
// 			}

// 			@Override
// 			public int getColumnCount() {
// 				return 2;
// 			}

// 			@Override
// 			public boolean isCellEditable(int row, int column) {
// 				if (column == 1) {
// 					return true;
// 				}

// 				return false;
// 			}

// 		};

// 		customTooltipTable.setModel(customTooltipTableModel);

// 		refreshTooltipTable();

// 		setContentPane(CustomTooltipWindowJPanel);
// 		setSize(700, 500);
// 		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
// 		pack();

// 		/* action listeners */
// 		tooltipFileTextField.addMouseListener(new MouseAdapter() {
// 			/**
// 			 * {@inheritDoc}
// 			 *
// 			 * @param e
// 			 */
// 			@Override
// 			public void mouseClicked(MouseEvent e) {
// 				super.mouseClicked(e);

// 				JFileChooser j = new JFileChooser(String.valueOf(Settings.MOD_PATH));
// 				j.setFileSelectionMode(JFileChooser.FILES_ONLY);
// 				j.setDialogTitle("Choose file w/ tooltip: ");

// 				int opt = j.showOpenDialog(null);
// 				if (opt == JFileChooser.APPROVE_OPTION) {
// 					tooltipFile = new File(j.getSelectedFile().getPath());
// 				} else {
// 					return;
// 				}

// 				/* tooltip file */
// 				tooltipFileTextField.setText(tooltipFile.getPath());
// 			}
// 		});

// 		localizationFileTextField.addMouseListener(new MouseAdapter() {
// 			/**
// 			 * {@inheritDoc}
// 			 *
// 			 * @param e
// 			 */
// 			@Override
// 			public void mouseClicked(MouseEvent e) {
// 				super.mouseClicked(e);

// 				// use british spelling of "localization"
// 				JFileChooser j = new JFileChooser(Settings.MOD_PATH+ "\\localisation");
// 				j.setFileSelectionMode(JFileChooser.FILES_ONLY);
// 				j.setDialogTitle("Choose Mod Directory");

// 				int opt = j.showOpenDialog(null);
// 				if (opt == JFileChooser.APPROVE_OPTION) {
// 					try {
// 						localizationFile = new LocalizationFile(j.getSelectedFile());
// 					} catch (IOException ex) {
// 						HOIIVUtils.openError(ex);
// 						return;
// 					}
// 				} else {
// 					return;
// 				}

// 				/* localization file */
// 				localizationFileTextField.setText(localizationFile.getPath());

// 				if (tooltipFile != null) {
// 					refreshTooltipTable();
// 				}
// 			}
// 		});
// 		customTooltipTable.addKeyListener(new KeyAdapter() {
// 			/**
// 			 * Invoked when a key has been pressed.
// 			 *
// 			 * @param e
// 			 */
// 			@Override
// 			public void keyPressed(KeyEvent e) {
// 				super.keyPressed(e);

// //				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
// //					int rowSelected = customTooltipTable.getSelectedRow();
// //					String text = null;
// //					if (rowSelected >= 0) {
// //						text = (String) customTooltipTableModel.getValueAt(rowSelected, 1);
// //					}
// //
// //					if (text != null && (!text.equals("[null]"))) {
// //						String key = (String) customTooltipTableModel.getValueAt(rowSelected, 0);
// //						localizationFile.setLocalization(key, text);
// //					}
// //				}
// 			}
// 		});
// 		saveChangesButton.addActionListener(new ActionListener() {
// 			/**
// 			 * Invoked when an action occurs.
// 			 *
// 			 * @param e the event to be processed
// 			 */
// 			@Override
// 			public void actionPerformed(ActionEvent e) {
// 				saveChangesButton.setEnabled(false);

// 				/* update localizations (possibly again) - for now */
// 				for (int i = 0; i < customTooltips.length; i++) {
// 					String text;
// 					text = (String) customTooltipTableModel.getValueAt(i, 1);

// 					if (text != null && (!text.equals("[none]"))) {
// 						String key = (String) customTooltipTableModel.getValueAt(i, 0);
// 						try {
// 							localizationFile.setLocalization(key, text);
// 						} catch (ConcurrentModificationException exc) {
// 							exc.printStackTrace();
// 						}
// 					}
// 				}

// 				try {
// 					localizationFile.writeLocalization();
// 				} catch (IOException exc) {
// 					throw new RuntimeException(exc);
// 				}

// 				saveChangesButton.setEnabled(true);
// 			}
// 		});
// 	}

// 	public void refreshTooltipTable() {
// 		if (tooltipFile == null) {
// 			return;
// 		}

// 		/* init */
// 		localizationFile.readLocalization();

// 		CustomTooltip.loadTooltips(tooltipFile);
// 		customTooltips = CustomTooltip.getTooltips();
// 		if (customTooltips == null) {
// 			System.err.println("No custom tooltips found");
// 			return;
// 		}

// 		customTooltipTableModel.getDataVector().removeAllElements();
// 		if (customTooltips.length > 0) {
// 			customTooltipTableModel.setRowCount(customTooltips.length);
// 		} else {
// 			customTooltipTableModel.setRowCount(1);
// 		}
// 		customTooltipTableModel.setColumnCount(2);
// 		customTooltipTableModel.fireTableDataChanged();

// 		for (int i = 0; i < customTooltips.length; i++) {
// 			String tooltipID = customTooltips[i].getID();
// 			Localization tooltipLocalization = localizationFile.getLocalization(tooltipID);
// 			System.out.println(tooltipLocalization);

// 			customTooltipTableModel.setValueAt(customTooltips[i].getID(), i, 0);
// 			if (tooltipLocalization != null) {
// 				customTooltipTableModel.setValueAt(tooltipLocalization.text(), i, 1);
// 			} else {
// 				customTooltipTableModel.setValueAt("[none]", i, 1);
// 			}
// 		}
// 	}

// }
