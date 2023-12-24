package com.mycompany.app;

import java.net.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ClientModel implements Runnable {
	private ClientView view;
	private ClientController controller;
	private ClientSocket csocket;

	private String fromUser;

	// client status
	private boolean serverConnected;
	private boolean gameprepared;
	private boolean gameLoad;
	private boolean viewPlayback;
	private boolean gameStarted;

	// textures
	private BufferedImage[] textures;
	private Actor[] drawingList;
	private Actor[] drawingList2;
	private Map map;

	private boolean moveUp;
	private boolean moveDown;
	private boolean moveLeft;
	private boolean moveRight;
	private boolean fire;

	private boolean instructionPrepared;
	private String instruction;
	private boolean serverInstructionPrepared;
	private String serverInstruction;
	private int playerNumber;

	public class ImageLoader {
		public BufferedImage loadImage(String path) {
			BufferedImage image = null;
			try (InputStream is = getClass().getResourceAsStream(path)) {
				image = ImageIO.read(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return image;
		}
	}

	public ClientModel(ClientView thisview) {
		view = thisview;

		instruction = "";
		this.setInstructionPrepared(false);
		playerNumber = 1;
		serverConnected = false;
		gameprepared = false;
		gameLoad = false;
		viewPlayback = false;
		gameStarted = false;
		drawingList = new Actor[400];
		drawingList2 = new Actor[100];
		textures = new BufferedImage[88];
		for (int i = 1; i < 89; i++) {
			try (InputStream is = getClass().getResourceAsStream("/images/" + i + ".jpg")) {
				textures[i - 1] = ImageIO.read(is);
				MediaTracker tracker = new MediaTracker(view.mainPanel);
				tracker.addImage(textures[i - 1], 0);

				try {
					tracker.waitForAll();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map = new Map(this);
		view.mainPanel.setModel(this);
	}

	// 一系列进行基础设置的函数
	public void setController(ClientController thisControler) {
		controller = thisControler;
	}

	public void setSocket(ClientSocket thissocket) {
		csocket = thissocket;
	}

	public void setMoveDirections(boolean up, boolean down, boolean left, boolean right) {
		this.moveUp = up;
		this.moveDown = down;
		this.moveLeft = left;
		this.moveRight = right;
	}

	public void setFire(boolean fire) {
		this.fire = fire;
	}

	public void setPlayerNumber(int n) {
		this.playerNumber = n;
	}

	public void setServerConnected(boolean c) {
		this.serverConnected = c;
	}

	public void setGameStarted(boolean c) {
		this.gameStarted = c;
	}

	public BufferedImage[] getTextures() {
		return this.textures;
	}

	public Actor[] getDrawingList() {
		return this.drawingList;
	}

	public Actor[] getDrawingList2() {
		return this.drawingList2;
	}

	public BufferedImage getTexture(int i) {
		return this.textures[i];
	}

	public Map getMap() {
		return this.map;
	}

	public void addActor(Actor actor) {
		for (int i = 0; i < drawingList.length; i++)
			if (drawingList[i] == null) {
				drawingList[i] = actor;
				break;
			}
	}

	public void setDrawingList2New() {
		drawingList2 = new Actor[100];
	}

	public void addActor2(Actor actor) {
		for (int i = 0; i < drawingList2.length; i++)
			if (drawingList2[i] == null) {
				drawingList2[i] = actor;
				break;
			}
	}

	public void removeActor(Actor actor) {
		for (int i = 0; i < drawingList.length; i++)
			if (drawingList[i] == actor) {
				drawingList[i] = null;
				break;
			}
	}

	public void removeActor2(Actor actor) {
		for (int i = 0; i < drawingList2.length; i++)
			if (drawingList2[i] == actor) {
				drawingList2[i] = null;
				break;
			}
	}

	// 获得drawingList中的Actor
	public Actor getActor(int i) {
		if (i >= 0 && i < drawingList.length && drawingList[i] != null)
			return drawingList[i];
		else
			return null;

	}

	// 获得drawingList2中的Actor
	public Actor getActor2(int i) {
		if (i >= 0 && i < drawingList2.length && drawingList2[i] != null)
			return drawingList2[i];
		else
			return null;

	}

	public void run() {
		try {
			long previousTime = System.currentTimeMillis();
			while (true) {
				try {
					// 使用动态帧率控制，记录每次循环的开始时间，以及结束时间，计算时间差，然后进行sleep
					long currentTime = System.currentTimeMillis();
					long timeDiff = currentTime - previousTime;
					long sleep = 33 - timeDiff;
					if (sleep < 0) {
						sleep = 0;
					}
					Thread.sleep(sleep);
					previousTime = System.currentTimeMillis();

				} catch (InterruptedException e) {
					System.err.println("Thread was interrupted: " + e.getMessage());
					view.mainPanel.setgameStart(false);
					Thread.currentThread().interrupt();
					break;
				}
				if (!serverConnected) {
					continue;
				}
				if (!serverInstructionPrepared) {
					continue;
				}
				if (this.readInstruction() == 0) {
					this.setServerInstructionPrepared(false);
					continue;
				}
				this.setServerInstructionPrepared(false);

				fromUser = "";
				// 分别处理不同的指令
				if (gameprepared && !gameStarted) {
					fromUser = "P;";
					this.instruction = fromUser;
					this.setInstructionPrepared(true);
					this.setServerInstructionPrepared(false);
					continue;
				}
				if (gameLoad && !gameStarted) {
					fromUser += "L;";
					this.instruction = fromUser;
					this.setInstructionPrepared(true);
					this.setServerInstructionPrepared(false);
					continue;
				}
				if (viewPlayback && !gameStarted) {
					fromUser += "V;";
					this.instruction = fromUser;
					this.setInstructionPrepared(true);
					this.setServerInstructionPrepared(false);
					continue;
				}

				if (gameStarted) {
					this.view.mainPanel.repaint();
				}

				fromUser = "";
				fromUser += "m";
				if (moveUp)
					fromUser += "1";
				else
					fromUser += "0";
				if (moveDown)
					fromUser += "1";
				else
					fromUser += "0";
				if (moveLeft)
					fromUser += "1";
				else
					fromUser += "0";
				if (moveRight)
					fromUser += "1";
				else
					fromUser += "0";
				if (fire)
					fromUser += "1";
				else
					fromUser += "0";
				fromUser += ";";

				this.instruction = fromUser;
				this.setInstructionPrepared(true);

				view.mainPanel.repaint();

				if (!view.mainPanel.hasFocus()) {
					moveLeft = false;
					moveUp = false;
					moveDown = false;
					moveRight = false;
					fire = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			serverConnected = false;
			gameStarted = false;
			view.mainPanel.setgameStart(false);

			if (csocket != null) {
				csocket.closeConnection();
			}

		} finally {
			if (csocket != null) {
				try {
					csocket.closeConnection(); // Replace with actual close method
				} catch (Exception ex) {
					System.err.println("Error closing socket: " + ex.getMessage());
				}
			}
		}
	}

	// 一些和指令有关的函数
	public void setInstructionPrepared(boolean c) {
		this.instructionPrepared = c;
	}

	public boolean getInstructionPrepared() {
		return this.instructionPrepared;
	}

	public String getInstruction() {
		String instructioncopy = this.instruction;
		return instructioncopy;
	}

	public void setServerInstructionPrepared(boolean c) {
		this.serverInstructionPrepared = c;
	}

	public void setServerInstruction(String thisinstruction) {
		this.serverInstruction = thisinstruction;
	}

	public boolean getServerInstructionPrepared() {
		return this.serverInstructionPrepared;
	}

	public String getServerInstruction() {
		String instructioncopy = this.serverInstruction;
		return instructioncopy;
	}

	public void setGamePrepared(boolean c) {
		this.gameprepared = c;
	}

	public void setGameLoad(boolean c) {
		this.gameLoad = c;
	}

	public void setViewPlayback(boolean c) {
		this.viewPlayback = c;
	}

	public boolean isMoveDown() {
		return this.moveDown;
	}

	public boolean isMoveLeft() {
		return this.moveLeft;
	}

	public boolean isMoveRight() {
		return this.moveRight;
	}

	public boolean isMoveUp() {
		return this.moveUp;
	}

	private synchronized int readInstruction() {
		// 使用局部变量来存储serverInstruction的副本
		this.setDrawingList2New();
		String instructionCopy;
		synchronized (this) {
			if (this.serverInstruction.length() == 0) {
				return 0;
			}
			// 复制serverInstruction字符串以在副本上进行操作
			instructionCopy = this.serverInstruction;
		}

		// 使用副本进行解析
		int i = 0;
		while (i < instructionCopy.length()) {
			StringBuilder perInstruction = new StringBuilder();
			while (!instructionCopy.substring(i, i + 1).equals(";")) {
				perInstruction.append(instructionCopy.substring(i, i + 1));
				i++;
				// 如果到达字符串末尾，跳出循环
				if (i >= instructionCopy.length())
					break;
			}
			i++;

			if (perInstruction.substring(0, 1).equals("S")) {
				this.view.mainPanel.setState(0);
				this.gameStarted = true;// 开始游戏
				this.view.mainPanel.setgameStart(true);
			}
			if (perInstruction.substring(0, 1).equals("N")) {
				this.setPlayerNumber(Integer.parseInt(perInstruction.substring(1, 2)));
				view.setPlayerNumber(this.playerNumber);
				// 设置游戏人数
			}
			if (perInstruction.substring(0, 1).equals("D")) {
				this.view.mainPanel.setState(2);// 游戏失败
				this.view.repaint();
				this.drawingList = new Actor[400];
				this.gameStarted = false;
				this.gameprepared = false;
				this.gameLoad = false;
				this.viewPlayback = false;
			}
			if (perInstruction.substring(0, 1).equals("W")) {
				this.view.mainPanel.setState(1);// 游戏胜利
				this.view.repaint();
				this.drawingList = new Actor[400];
				this.gameStarted = false;
				this.gameprepared = false;
				this.gameLoad = false;
				this.viewPlayback = false;
			}

			// 设置地图
			if (perInstruction.substring(0, 1).equals("M")) {
				int mapNum = Integer.parseInt(perInstruction.substring(1, 2));
				map.setMap(mapNum);
				String[] mapString = this.map.getMap();
				for (int wi = 0; wi < 400; wi++) {
					if (mapString[wi].equals("##")) {
						Wall wall = new Wall(12 + wi % 20 * 25, 12 + wi / 20 * 25 + 50, this);
						this.addActor(wall);
					}
				}
			}
			if (perInstruction.substring(0, 1).equals("P")) {
				System.out.println(perInstruction);
				// 更新玩家信息
				int i_id = 0;
				int xPos = 0;
				int yPos = 0;
				int direction = 0;
				int health = 0;
				int score = 0;
				String temp = "";
				int j = 1;
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				i_id = Integer.parseInt(temp);
				// get x position
				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				xPos = Integer.parseInt(temp);

				// get y position
				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				direction = Integer.parseInt(temp);

				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				health = Integer.parseInt(temp);

				temp = "";
				while (j < perInstruction.length()) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				score = Integer.parseInt(temp);

				Player player = new Player(i_id, xPos, yPos, direction, health, score, this);
				this.addActor2(player);
			}

			if (perInstruction.substring(0, 1).equals("E")) {
				System.out.println(perInstruction);
				// 更新敌人信息
				int i_id = 0;
				int xPos = 0;
				int yPos = 0;
				int direction = 0;
				int health = 0;
				String temp = "";
				int j = 1;
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				i_id = Integer.parseInt(temp);
				// get x position
				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				xPos = Integer.parseInt(temp);

				// get y position
				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				direction = Integer.parseInt(temp);

				temp = "";
				while (j < perInstruction.length()) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				health = Integer.parseInt(temp);

				Enemy enemy = new Enemy(i_id, xPos, yPos, direction, health, this);
				this.addActor2(enemy);
			}

			if (perInstruction.substring(0, 1).equals("B")) {
				// 更新子弹信息
				int xPos = 0;
				int yPos = 0;
				int direction = 0;
				String temp = "";
				int j = 1;
				// get x position
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				xPos = Integer.parseInt(temp);

				// get y position
				temp = "";
				while (!perInstruction.substring(j, j + 1).equals(",")) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				yPos = Integer.parseInt(temp);

				temp = "";
				while (j < perInstruction.length()) {
					temp += perInstruction.substring(j, j + 1);
					j++;
				}
				j++;
				direction = Integer.parseInt(temp);

				Bullet bullet = new Bullet(xPos, yPos, direction, this);
				this.addActor2(bullet);
			}
		}
		return 1;
	}
}
