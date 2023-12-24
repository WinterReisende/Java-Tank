package com.mycompany.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

import javafx.scene.paint.Color;

public class ClientView extends JFrame {
	public DrawingPanel mainPanel;
	public JButton connectServer, loadPreviousGame, startGame, exit, viewPlayback;
	public JLabel enterIP;
	public JTextField IPfield;
	public ClientController controller;
	// public ClientMover mover;
	public ClientModel model;
	public ClientSocket socket;
	public int playerNumber;
	public ExecutorService exec;

	public ClientView() throws InterruptedException {
		super("Client show");
		playerNumber = 1;
		getContentPane().setLayout(null);

		// make main panel where the animation will be drawn
		mainPanel = new DrawingPanel();
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 22, 750, 650);
		mainPanel.setBackground(new java.awt.Color(255, 255, 255));

		getContentPane().add(mainPanel);
		mainPanel.setFocusable(true);

		connectServer = new JButton("Connect server");
		connectServer.setBounds(10, 0, 125, 22);
		getContentPane().add(connectServer);
		connectServer.setFocusable(false);

		startGame = new JButton("Start new game");
		startGame.setBounds(140, 0, 160, 22);
		getContentPane().add(startGame);
		startGame.setFocusable(false);

		loadPreviousGame = new JButton("Load Precious Game");
		loadPreviousGame.setBounds(305, 0, 160, 22);
		getContentPane().add(loadPreviousGame);
		loadPreviousGame.setFocusable(false);

		viewPlayback = new JButton("View Playback");
		viewPlayback.setBounds(470, 0, 160, 22);
		getContentPane().add(viewPlayback);
		viewPlayback.setFocusable(false);

		exit = new JButton("Exit");
		exit.setBounds(635, 0, 90, 22);
		getContentPane().add(exit);
		exit.setFocusable(false);

		model = new ClientModel(this);
		socket = new ClientSocket(this);
		controller = new ClientController(this, model, socket);
		//socket.setController(controller);
		socket.setModel(model);
		model.setController(controller);
		model.setSocket(socket);

		// mover_2 = new ClientMover_2(this);

		// setup the main frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 130, 750, 700);
		setVisible(true);
		setResizable(false);
		//mainPanel.setModel(model);
		//((DrawingPanel)mainPanel).setModel(model);
		mainPanel.setVisible(true);
		mainPanel.setgameStart(true);
		mainPanel.repaint();
		// Thread.sleep(10000);
		mainPanel.repaint();

		exec = Executors.newCachedThreadPool();
		exec.execute(model);
		exec.execute(socket);
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
					new ClientView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}