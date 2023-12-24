package com.mycompany.app;

import java.awt.event.*;

public class ClientController {

	private ClientView view;
	private ClientModel model;
	private ClientSocket socket;

	public ClientController(ClientView thisview, ClientModel thismodel, ClientSocket thissocket) {
		view = thisview;
		model = thismodel;
		socket = thissocket;

		view.connectServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				socket.setConnecting(true);
			}
		});

		view.exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.exit(0);
			}
		});

		view.startGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				model.setGamePrepared(true);
			}
		});

		view.loadPreviousGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				model.setGameLoad(true);
				// view.mainPanel.repaint();
			}
		});

		view.viewPlayback.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				model.setViewPlayback(true);
				// view.mainPanel.repaint();
			}
		});

		view.mainPanel.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					model.setMoveDirections(true, false, false, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					model.setMoveDirections(false, true, false, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					model.setMoveDirections(false, false, true, false);
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					model.setMoveDirections(false, false, false, true);
				}

				if (e.getKeyChar() == 's') {
					model.setFire(true);
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					model.setMoveDirections(false, model.isMoveDown(), model.isMoveLeft(), model.isMoveRight()); // 假设isMoveDown等方法存在
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					model.setMoveDirections(model.isMoveUp(), false, model.isMoveLeft(), model.isMoveRight());
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					model.setMoveDirections(model.isMoveUp(), model.isMoveDown(), false, model.isMoveRight());
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					model.setMoveDirections(model.isMoveUp(), model.isMoveDown(), model.isMoveLeft(), false);
				}
				if (e.getKeyChar() == 's') {
					model.setFire(false);
				}
			}
		});
	}

	public void showmovew() {
		while (true) {
			try {
				Thread.sleep(33);
				view.mainPanel.repaint();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
