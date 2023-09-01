package ui.clausewitz_gfx;

import hoi4utils.HOIIVUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ui.HOIUtilsWindow;

// todo add actual effect (enable/disable) from check boxes
public class InterfaceFileListWindow extends HOIUtilsWindow {
	@FXML
	public Label idVersion;
	public Label idWindowName;

	public InterfaceFileListWindow() {
		fxmlResource = "InterfaceFileListWindow.fxml";
		title = "HOIIVUtils Interface File List Window";
	}

	@FXML
	void initialize() {
		includeVersion();
		idWindowName.setText("InterfaceFileListWindow" + " WIP");
	}

	private void includeVersion() {
		idVersion.setText(HOIIVUtils.hoi4utilsVersion);
	}
}

	
// 	private JPanel interfaceFileListJPanel;
// 	private JTable interfaceFileListTable;
// 	private DefaultTableModel interfaceFileListTableModel;
// 	private ArrayList<Boolean> enableInterfaceFiles;

// 	public InterfaceFileListWindow() {
// 		super("Interface file list");

// 		// table model
// 		interfaceFileListTableModel = new DefaultTableModel() {
// 			@Override
// 			public int getRowCount() {
// 				return Interface.numFiles();
// 			}

// 			@Override
// 			public int getColumnCount() {
// 				return 2;
// 			}

// 			@Override
// 			public boolean isCellEditable(int row, int column) {
// 				if (column == 0) {
// 					return true;
// 				}
// 				return false;
// 			}

// 			@Override
// 			public Class<?> getColumnClass(int column) {
// 				switch (column) {
// 					case 0:
// 						return Boolean.class;
// 					default:
// 						return String.class;
// 				}
// 			}

// 		};
// 		String[] columns = {"Enable/Disable", "Interface file name"};
// 		interfaceFileListTableModel.setColumnIdentifiers(columns);
// 		interfaceFileListTable.setModel(interfaceFileListTableModel);

// 		// cell rendering
// 		interfaceFileListTable.setRowHeight(25);

// 		// row sorter
// 		interfaceFileListTable.setAutoCreateRowSorter(true);

// 		// data
// 		refreshInterfaceFileListTable();

// 		/* window */
// 		setContentPane(interfaceFileListJPanel);
// 		setSize(700, 500);
// 		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
// 		pack();
// 	}

// 	private void refreshInterfaceFileListTable() {
// 		// table data update
// 		Interface[] interfaceFiles = Interface.listGFXFiles();
// 		enableInterfaceFiles = new ArrayList<>();

// 		interfaceFileListTableModel.getDataVector().removeAllElements();
// 		interfaceFileListTableModel.setRowCount(interfaceFiles.length);
// 		interfaceFileListTableModel.setColumnCount(2);
// 		interfaceFileListTableModel.fireTableDataChanged();

// 		for (int i = 0; i < interfaceFiles.length; i++) {
// 			Boolean enableFile = true;
// 			String interfaceFilename = interfaceFiles[i].getName();
// 			enableInterfaceFiles.add(enableFile);

// 			// data
// 			interfaceFileListTableModel.setValueAt(enableFile, i, 0);
// 			interfaceFileListTableModel.setValueAt(interfaceFilename, i, 1);
// 		}
// 	}

// }
