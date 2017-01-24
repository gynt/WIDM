package com.gynt.widm.graphics;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.gynt.widm.core.Game;
import com.gynt.widm.core.Round;

public class GamePanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -3704163141493782314L;
	private JTabbedPane tabbedPane;
	private Game game;

	private GamePanel() {
		//setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		add(tabbedPane, BorderLayout.CENTER);


	}

	public GamePanel(Game g) {
		this();
		tabbedPane.removeAll();
		game=g;
		if(g.rounds.size()==0) {
			g.rounds.add(new Round());
			RoundScreen round1Panel = new RoundScreen(g.rounds.get(0));
			tabbedPane.addTab("Round 1", null, round1Panel, null);
		} else {
			for(int i = 0; i < g.rounds.size(); i++) {
				tabbedPane.addTab("Round "+(i+1), null, new RoundScreen(g.rounds.get(i)), null);
			}
		}

		PreferencesScreen preferencesPanel = new PreferencesScreen();
		tabbedPane.addTab("Preferences", null, preferencesPanel, null);
	}

}
