package com.mycompany.app;

import java.awt.event.*;

//this class deal with  the input from the Server view
public class ServerController {
	private ServerView view;
	private ServerModel model;

	public ServerController(ServerView thisview, ServerModel thismodel) {
		view = thisview;
		model = thismodel;

		// handel startServer button actions
		view.saveMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("map:");
				// model.getMap().printMap();
				model.writeMapToFile("savedMap");
				// model.getMap().setMapFromFile("null");
			}
		});

		view.saveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("map:");
				// model.getMap().printMap();
				model.saveState("savedState");
				// model.getMap().setMapFromFile("null");
			}
		});

		// handel exit button actions
		view.exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		view.mainPanel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				view.mainPanel.requestFocus();
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}
		});

	}
}