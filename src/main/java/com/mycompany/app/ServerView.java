package com.mycompany.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

public class ServerView extends JFrame {
	public DrawingPanel mainPanel;
	public JButton saveMap, saveState, exit;
	public ServerController controller;
	public ServerModel model;
	public ServerSelector selector;
	public int playerNumber;
	public ExecutorService exec;

	public ServerView() throws InterruptedException, IOException {
		super("APP show");
		playerNumber = 1;
		getContentPane().setLayout(null);

		mainPanel = new DrawingPanel();
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 22, 679, 605);
		mainPanel.setBackground(new java.awt.Color(255, 255, 255));

		getContentPane().add(mainPanel);
		mainPanel.setFocusable(true);

		saveMap = new JButton("saveMap");
		saveMap.setBounds(150, 0, 125, 22);
		getContentPane().add(saveMap);
		saveMap.setFocusable(false);

		saveState = new JButton("saveState");
		saveState.setBounds(280, 0, 125, 22);
		getContentPane().add(saveState);
		saveState.setFocusable(false);

		exit = new JButton("Exit");
		exit.setBounds(410, 0, 125, 22);
		getContentPane().add(exit);
		exit.setFocusable(false);

		model = new ServerModel(this);
		selector = new ServerSelector(this);
		controller = new ServerController(this, model);
		selector.setModel(model);
		model.setController(controller);
		model.setSelector(selector);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1150, 130, 643, 650);
		setVisible(true);
		setResizable(false);
		mainPanel.setVisible(true);
		mainPanel.setgameStart(true);
		mainPanel.repaint();
		mainPanel.setgameStart(false);

		exec = Executors.newCachedThreadPool();
		exec.execute(model);
		exec.execute(selector);
		repaint();
		revalidate();
	}

	public synchronized void setPlayerNumber(int n) {
		this.playerNumber = n;
	}

	public static void main(String[] args) throws InterruptedException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ServerView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
