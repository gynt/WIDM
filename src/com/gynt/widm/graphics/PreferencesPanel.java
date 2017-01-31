package com.gynt.widm.graphics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.gynt.widm.core.Preferences;
import com.gynt.widm.core.Preferences.PreferenceDir;
import com.gynt.widm.core.Preferences.PreferenceItem;
import com.gynt.widm.core.Preferences.PreferenceSub;
import com.gynt.widm.core.util.JTreeUtil;

public class PreferencesPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1551845699358510952L;
	private JPanel prefPanel;
	private JTree prefTree;

	/**
	 * Create the panel.
	 */
	public PreferencesPanel() {
		setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		splitPane.setResizeWeight(0.33);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		prefTree = new JTree();
		//prefTree.setRootVisible(false);
		prefTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				PrefPanel d = (PrefPanel) ((DefaultMutableTreeNode) arg0.getNewLeadSelectionPath()
						.getLastPathComponent()).getUserObject();

				prefPanel.removeAll();
				prefPanel.add(d, BorderLayout.CENTER);

				d.revalidate();
				prefPanel.repaint();
			}
		});
		scrollPane.setViewportView(prefTree);

		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

//		JScrollPane scrollPane_1 = new JScrollPane();
//		panel_1.add(scrollPane_1);

		prefPanel = new JPanel(new BorderLayout(0, 0));
//		scrollPane_1.setViewportView(prefPanel);
		panel_1.add(prefPanel);

	}

	public static class PrefPanel extends JPanel {

		/**
		 *
		 */
		private static final long serialVersionUID = -3068459540835447028L;
		private PreferenceDir source;

		public PrefPanel(PreferenceDir src) {
			source = src;

			setLayout(new FlowLayout(FlowLayout.LEADING));

			// ArrayList<JPanel> subpanels = new ArrayList<>();
			for (PreferenceSub sub : source.subs) {
				JPanel subpanel = new JPanel();
				subpanel.setLayout(new GridLayout(0, 1));
				add(subpanel);

				subpanel.setBorder(BorderFactory.createTitledBorder(sub.description));
				ButtonGroup bg = new ButtonGroup();
				for (int i = 0; i < sub.items.size(); i++) {
					PreferenceItem pi = sub.items.get(i);
					//System.out.println(pi.type.getSimpleName());
					switch (pi.type.getSimpleName()) {
					case "File": {
						JLabel label = new JLabel(pi.description);
						JTextField jtf = new JTextField(pi.getValue().toString());
						jtf.getDocument().addDocumentListener(new DocumentListener() {

							@Override
							public void removeUpdate(DocumentEvent e) {
								subpanel.revalidate();
								pi.setValue(jtf.getText());
							}

							@Override
							public void insertUpdate(DocumentEvent e) {
								subpanel.revalidate();
								pi.setValue(jtf.getText());
							}

							@Override
							public void changedUpdate(DocumentEvent e) {
								subpanel.revalidate();
								pi.setValue(jtf.getText());
							}
						});
						JButton browse = new JButton("Browse");
						browse.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								JFileChooser jfc = new JFileChooser(pi.getValue().toString());
								int result = jfc.showOpenDialog(browse);
								if(result==JFileChooser.APPROVE_OPTION) {
									jtf.setText(jfc.getSelectedFile().toString());
									//pi.setValue(jfc.getSelectedFile().toString()); //Use listener?
								}
							}
						});
						JPanel subsub = new JPanel(new BorderLayout());
						subsub.add(label, BorderLayout.WEST);
						subsub.add(jtf, BorderLayout.CENTER);
						subsub.add(browse, BorderLayout.EAST);
						subpanel.add(subsub);
						break;
					}
					case "Radio": {
						JRadioButton jrb = new JRadioButton(pi.description);
						jrb.setSelected((Boolean) pi.getValue());
						jrb.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent e) {
								pi.setValue(e.getStateChange() == ItemEvent.SELECTED);
							}
						});
						bg.add(jrb);
						subpanel.add(jrb);
						break;
					}
					case "Boolean": {

						JCheckBox jcb = new JCheckBox(pi.description);
						jcb.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent e) {
								pi.setValue(e.getStateChange() == ItemEvent.SELECTED);
							}
						});
						jcb.setSelected((Boolean) pi.getValue());
						subpanel.add(jcb);

						break;
					}
					case "String": {
						JLabel label = new JLabel(pi.description);
						// System.out.println(pi.buildPath());
						JTextField jtf = new JTextField((String) pi.getValue());
						jtf.getDocument().addDocumentListener(new DocumentListener() {

							@Override
							public void removeUpdate(DocumentEvent e) {
								pi.setValue(jtf.getText());
							}

							@Override
							public void insertUpdate(DocumentEvent e) {
								pi.setValue(jtf.getText());
							}

							@Override
							public void changedUpdate(DocumentEvent e) {
								pi.setValue(jtf.getText());
							}
						});
						subpanel.add(label);
						subpanel.add(jtf);
						break;
					}
					case "Float": {
						System.out.println("Rendering float");
						JLabel label = new JLabel(pi.description);
						JSpinner js = new JSpinner(new SpinnerModel() {
							
							private ArrayList<ChangeListener> listeners = new ArrayList<>();

							@Override
							public void setValue(Object value) {
								pi.setValue(value);
								for(ChangeListener l : listeners) {
									l.stateChanged(new ChangeEvent(this));
								}
							}

							@Override
							public void removeChangeListener(ChangeListener l) {
								listeners.remove(l);
							}

							@Override
							public Object getValue() {
								return pi.getValue();
							}

							@Override
							public Object getPreviousValue() {
								return (Float)getValue()-0.1f;
							}

							@Override
							public Object getNextValue() {
								return (Float)getValue()+0.1f;
							}

							@Override
							public void addChangeListener(ChangeListener l) {
								listeners.add(l);
							}
						});
						subpanel.add(label);
						subpanel.add(js);
						break;
					}
					case "Integer": {
						JLabel label = new JLabel(pi.description);
						JSpinner js = new JSpinner(new SpinnerModel() {

							private ArrayList<ChangeListener> listeners = new ArrayList<>();

							@Override
							public void setValue(Object value) {
								pi.setValue(value);
								for(ChangeListener l : listeners) {
									l.stateChanged(new ChangeEvent(this));
								}
							}

							@Override
							public void removeChangeListener(ChangeListener l) {
								listeners.remove(l);
							}

							@Override
							public Object getValue() {
								return pi.getValue();
							}

							@Override
							public Object getPreviousValue() {
								return (Integer)getValue()-1;
							}

							@Override
							public Object getNextValue() {
								return (Integer)getValue()+1;
							}

							@Override
							public void addChangeListener(ChangeListener l) {
								listeners.add(l);
							}
						});
						subpanel.add(label);
						subpanel.add(js);
						break;
					}
					case "Double": {
						JLabel label = new JLabel(pi.description);
						JSpinner js = new JSpinner(new SpinnerModel() {

							private ArrayList<ChangeListener> listeners = new ArrayList<>();

							@Override
							public void setValue(Object value) {
								pi.setValue(value);
								for(ChangeListener l : listeners) {
									l.stateChanged(new ChangeEvent(this));
								}
							}

							@Override
							public void removeChangeListener(ChangeListener l) {
								listeners.remove(l);
							}

							@Override
							public Object getValue() {
								return pi.getValue();
							}

							@Override
							public Object getPreviousValue() {
								return (Double)getValue()-0.1f;
							}

							@Override
							public Object getNextValue() {
								return (Double)getValue()+0.1f;
							}

							@Override
							public void addChangeListener(ChangeListener l) {
								listeners.add(l);
							}
						});
						subpanel.add(label);
						subpanel.add(js);
						break;
					}
					}

				}
				subpanel.revalidate();
			}

		}

		@Override
		public String toString() {
			return source.name;
		}
	}

	public void render() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new PrefPanel(Preferences.ROOT));
		prefTree.setModel(new DefaultTreeModel(root));
		dirbuild(root, Preferences.ROOT);
		JTreeUtil.expandAllNodes(prefTree);
	}

	public void dirbuild(DefaultMutableTreeNode currentnode, PreferenceDir current) {
		for (PreferenceDir dir : current.dirs) {
			DefaultMutableTreeNode sub = new DefaultMutableTreeNode(new PrefPanel(dir));
			currentnode.add(sub);
			dirbuild(sub, dir);
		}
	}

}