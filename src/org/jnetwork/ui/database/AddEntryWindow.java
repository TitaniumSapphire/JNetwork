package org.jnetwork.ui.database;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jnetwork.database.ColumnHeader;
import org.jnetwork.database.EntrySet;

public class AddEntryWindow extends ApplicationWindow {
	private static final long serialVersionUID = -893901275066409320L;
	private EntrySet entries;

	public AddEntryWindow(EntrySet entries) {
		super("Add Entry");
		this.entries = entries;
	}

	private class AddEntryWindowContent extends IPanel {
		private static final long serialVersionUID = -933193607338792285L;

		private ArrayList<JTextField> fields = new ArrayList<>();

		public AddEntryWindowContent() {
			super();

			IPanel inner = new IPanel();
			inner.c.fill = GridBagConstraints.HORIZONTAL;
			inner.c.insets = new Insets(0, 5, 1, 0);
			int i = 0;
			for (ColumnHeader header : entries.getTableColumnHeaders()) {
				JLabel label = new JLabel(header.getColumnName() + ": ");
				JTextField field = new JTextField(15);
				field.setText(header.getStorageType() == ColumnHeader.STORAGE_TYPE_INTEGER ? "0"
						: header.getStorageType() == ColumnHeader.STORAGE_TYPE_DECIMAL ? "0.0" : "foo");
				fields.add(field);

				inner.add(label, 0, i);
				inner.add(field, 1, i);

				i++;
			}
			JPanel buttons = new JPanel();
			JButton add = new JButton("Add");
			JButton cancel = new JButton("Cancel");

			add.addActionListener(e -> {
				ChangeService.getService().change(new Change(null, Change.ADD, null, getFieldContents())
						.setChangeID(new BigInteger(128, new SecureRandom()).toString(16)));
				DatabaseGUI.getGUI().addTableRow(getFieldContents());
				DatabaseGUI.getGUI().setStatus("Added an entry.");
				DatabaseGUI.getGUI().setCanCommit(true);
				dispose();
			});
			cancel.addActionListener(e -> dispose());

			buttons.add(add);
			buttons.add(cancel);
			add(inner, 0, 0);
			add(buttons, 0, 1);
		}

		private ArrayList<Serializable> getFieldContents() {
			ArrayList<Serializable> contents = new ArrayList<>();
			for (JTextField field : fields) {
				contents.add(field.getText());
			}
			return contents;
		}
	}

	@Override
	public IPanel getApplicationPanel() {
		return new AddEntryWindowContent();
	}
}
