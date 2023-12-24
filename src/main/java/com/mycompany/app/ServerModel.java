package com.mycompany.app;

import java.io.FileNotFoundException;
import java.io.IOException;

//设置两种不同的模式，分别是游戏和回看，游戏模式下，所有的操作都会被记录，回看模式下，所有的操作都会被忽略

//设置两种游戏模式的开始方式，一种是从头开始，一种是从上次保存的状态开始

//先考虑从头开始，在设置gamestart==true之后，用一个函数开启所有的线程，包括敌人生成器，玩家，子弹，敌人，地图元素等等
//在设置gamestart==true之后，所有的线程都会开始运行
//此时，只需要在每个线程的run函数中，检查gamestart的值，若为false，则退出线程，否则继续运行

//考虑如何处理指令，即玩家信息的设置

//考虑生成的指令格式，应该包含什么？
//应该包含所有的玩家信息，所有的敌人信息，所有的子弹信息，所有的地图元素信息，以及游戏是否结束的信息
//玩家信息包括玩家的位置，玩家的方向，玩家的生命值，玩家的分数，玩家的类型，只要满足绘图要求即可
//敌人信息包括敌人的位置，敌人的方向，敌人的生命值，敌人的类型，只要满足绘图要求即可
//子弹信息包括子弹的位置，子弹的方向，子弹的类型，只要满足绘图要求即可

public class ServerModel implements Runnable {
    private ServerView view;
    private ServerSelector selector;
    private ServerController controller;

    private boolean[] clientConnected;
    private boolean[] gamePrepared;
    private boolean[] wantToLoad;
    private boolean[] wantToView;
    private boolean gameStarted;
    private boolean viewStarted;
    private boolean gameWin;

    // private String[] fromUsers;
    private String fromServer;

    private boolean[] moveUp;
    private boolean[] moveDown;
    private boolean[] moveLeft;
    private boolean[] moveRight;
    private boolean[] fire;

    private boolean instructionPrepared;
    private String instruction;
    private boolean[] clientInstructionPrepared;
    private String[] clientInstruction;
    private int playerNumber;
    private int playerLivingNumber;// 检查玩家是否全部存活
    private int[] Pnid;
    private Actor[] actors;
    private Map map;
    private EnemyCreater creater;
    // 用一个文档存储整个游戏进程，每次游戏结束后，都会将整个游戏进程存储到文档中，以便进行回放
    // 文件名为savedProgress
    private java.io.File progressFile;
    private java.io.PrintWriter progressOutpuWriter;

    public ServerModel(ServerView thisview) throws IOException {
        view = thisview;
        // fromUsers = new String[5];
        fromServer = "";
        moveUp = new boolean[5];
        moveDown = new boolean[5];
        moveLeft = new boolean[5];
        moveRight = new boolean[5];
        fire = new boolean[5];

        clientInstructionPrepared = new boolean[5];
        clientInstruction = new String[5];
        playerNumber = 0;
        playerLivingNumber = 0;
        Pnid = new int[5];

        clientConnected = new boolean[5];
        gamePrepared = new boolean[5];
        wantToLoad = new boolean[5];
        wantToView = new boolean[5];

        actors = new Actor[400];
        map = new Map(this);
        for (int i = 0; i < 5; i++) {
            clientConnected[i] = false;
            gamePrepared[i] = false;
            wantToLoad[i] = false;
            wantToView[i] = false;
            Pnid[i] = -1;
            moveUp[i] = false;
            moveDown[i] = false;
            moveLeft[i] = false;
            moveRight[i] = false;
            fire[i] = false;
            clientInstructionPrepared[i] = false;
            clientInstruction[i] = "";
        }

        instructionPrepared = false;
        gameStarted = false;
        viewStarted = false;
        gameWin = false;

        this.saveProgress();
    }

    public void saveProgress() {
        // 开始时，读取上次存储的savedProgress.txt，并将其存储到preProgress.txt中
        java.io.File preProgressFile = new java.io.File("preProgress.txt");
        try (java.io.PrintWriter preProgressOutpuWriter = new java.io.PrintWriter(preProgressFile)) {
            java.io.BufferedReader preProgressBufferedReader = new java.io.BufferedReader(
                    new java.io.FileReader("savedProgress.txt"));
            String line;
            while ((line = preProgressBufferedReader.readLine()) != null) {
                preProgressOutpuWriter.println(line);
            }
            preProgressBufferedReader.close();
            preProgressOutpuWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.progressFile = new java.io.File("savedProgress.txt");
        try {
            this.progressOutpuWriter = new java.io.PrintWriter(progressFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSelector(ServerSelector thisselector) {
        selector = thisselector;
    }

    public void setController(ServerController thisControler) {
        controller = thisControler;
    }

    public synchronized void setMoveDirections(int id, boolean up, boolean down, boolean left, boolean right) {
        this.moveUp[id] = up;
        this.moveDown[id] = down;
        this.moveLeft[id] = left;
        this.moveRight[id] = right;
    }

    public void setUp(int id, boolean c) {
        this.moveUp[id] = c;
    }

    public void setDown(int id, boolean c) {
        this.moveDown[id] = c;
    }

    public void setLeft(int id, boolean c) {
        this.moveLeft[id] = c;
    }

    public void setRight(int id, boolean c) {
        this.moveRight[id] = c;
    }

    public void setFire(int id, boolean fire) {
        this.fire[id] = fire;
    }

    public void setPlayerNumber(int n) {
        this.playerNumber = n;
    }

    public int getPlayerNumber() {
        int n = this.playerNumber;
        return n;
    }

    public void setPlayerLivingNumber(int n) {
        this.playerLivingNumber = n;
    }

    public int getPlayerLivingNumber() {
        int n = this.playerLivingNumber;
        return n;
    }

    public boolean getGameStarted() {
        return this.gameStarted;
    }

    public void setGameStarted(boolean c) {
        this.gameStarted = c;
    }

    public void setClientConnected(int id, boolean c) {
        this.clientConnected[id] = c;
    }

    public synchronized int addClient() {
        int id = -1;
        for (int i = 0; i < 5; i++) {
            if (!clientConnected[i]) {
                id = i;
                setClientConnected(id, true);
                // Player player = new Player(this, id, -1, id * 50, id * 50);
                setPlayerNumber(getPlayerNumber() + 1);
                this.view.mainPanel.setPlayerNumber(getPlayerNumber());
                this.view.mainPanel.setPlayerAddress(id, id * 50, id * 50);
                break;
            }
        }
        return id;
    }

    public synchronized int deleteClient(int id) {
        if (id < 0 || id > 4) {
            return -1;
        }
        setClientConnected(id, false);
        setPlayerNumber(getPlayerNumber() - 1);
        // 这里还应该根据player是否存活决定是否要减少playerLivingNumber，不过断开连接只会让server进入死循环，所以暂时不用考虑这个问题
        this.view.mainPanel.setPlayerNumber(getPlayerNumber());
        this.view.mainPanel.deletePlayer(id);
        return 1;
    }

    public synchronized void addActor(Actor actor) {
        for (int i = 0; i < 400; i++) {
            if (actors[i] == null) {
                actors[i] = actor;
                actors[i].setId(i);
                // 检查类名，若为player、bullet或enemy，则添加到view中
                if (actor.getClass().getName().equals("com.mycompany.app.Player")) {
                    this.view.exec.execute((Player) actor);
                }
                if (actor.getClass().getName().equals("com.mycompany.app.Bullet")) {
                    this.view.exec.execute((Bullet) actor);
                }
                if (actor.getClass().getName().equals("com.mycompany.app.Enemy")) {
                    this.view.exec.execute((Enemy) actor);
                }
                break;
            }
        }
    }

    public void deleteActor(int id) {
        actors[id] = null;
    }

    public synchronized void deleteActor(Actor actor) {
        for (int i = 0; i < 400; i++) {
            if (actors[i] == actor) {
                actors[i] = null;
                break;
            }
        }
    }

    public void setActorAddress(int id, int x, int y) {
        actors[id].setX(x);
        actors[id].setY(y);
    }

    public void setActorDirection(int id, int direction) {
        actors[id].setDirection(direction);
    }

    public Actor getActor(int id) {
        if (id >= 0 && id < 400) {
            return actors[id];
        }
        return null;
    }

    // 检查是否出现任何碰撞，若有，则返回碰撞的actor，否则返回null
    public Actor checkCollison(Actor a) {
        for (int i = 0; i < 400; i++) {
            if (actors[i] != null && actors[i] != a) {
                if (actors[i].getBorder().intersects(a.getBorder())) {
                    return actors[i];
                }
            }
        }
        return null;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return this.map;
    }

    public void setMapFromFile(String filename) {
        this.map.setMapFromFile(filename);
    }

    public void writeMapToFile(String filename) {
        this.map.writeMapToFile(filename);
    }

    public void setEnemyCreater(EnemyCreater creater) {
        this.creater = creater;
    }

    public EnemyCreater getEnemyCreater() {
        return this.creater;
    }

    public void setInstructionPrepared(boolean c) {
        this.instructionPrepared = c;
    }

    public boolean getInstructionPrepared() {
        return this.instructionPrepared;
    }

    public void setInstruction(String thisinstruction) {
        this.instruction = thisinstruction;
    }

    public String getInstruction() {
        String instructioncopy = this.instruction;
        return instructioncopy;
    }

    public void setClientInstructionPrepared(int id, boolean c) {
        if (clientConnected[id]) {
            this.clientInstructionPrepared[id] = c;
        }
    }

    public void setClientInstruction(int id, String thisinstruction) {
        if (clientConnected[id]) {
            this.clientInstruction[id] = thisinstruction;
        }
    }

    public void setGamePrepared(int id, boolean c) {
        this.gamePrepared[id] = c;
    }

    public synchronized void setgameStart(boolean c) {
        if (c) {
            System.out.println("setgamestart");
        } else {
            System.out.println("setgamestop");
        }
        this.gameStarted = c;
    }

    public void setGameWin(boolean c) {
        this.gameWin = c;
    }

    public boolean getGameWin() {
        return this.gameWin;
    }

    public boolean isMoveDown(int id) {
        return this.moveDown[id];
    }

    public boolean isMoveLeft(int id) {
        return this.moveLeft[id];
    }

    public boolean isMoveRight(int id) {
        return this.moveRight[id];
    }

    public boolean isMoveUp(int id) {
        return this.moveUp[id];
    }

    public boolean isFire(int id) {
        return this.fire[id];
    }

    public int getPnid(int i) {
        return Pnid[i];
    }

    public void setPnid(int id, int n) {
        this.Pnid[id] = n;
    }

    public void run() {
        try {
            // System.out.println(" ServerModel is running");
            int preplayerNumber = 0;
            int preparedplayerNumber = 0;
            int sleepTooLong = 0;
            long previousTime = System.currentTimeMillis();
            while (true) {
                // 使用动态帧率控制进行睡眠33毫秒，同时设置sleepTooLong
                sleepTooLong++;
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
                fromServer = "";
                if (sleepTooLong > 10) {
                    fromServer = "noon;";// 如果sleepTooLong超过10次，则发送一个noon，以防止客户端长时间没有收到消息
                }
                if (playerNumber == 0) {
                    continue;
                }
                if (playerNumber != preplayerNumber) {
                    preplayerNumber = playerNumber;
                    view.mainPanel.setPlayerNumber(playerNumber);
                    fromServer = "N" + playerNumber + ";";// 人数变化，则更新人数
                }
                // 检查用户的信息情况，若都已发送，则读取信息并进行处理
                boolean allInstructionready = true;
                for (int i = 0; i < 5; i++) {
                    if (clientConnected[i]) {
                        if (!clientInstructionPrepared[i]) {
                            allInstructionready = false;
                            break;
                        }
                    }
                }
                if (!allInstructionready) {
                    if (fromServer.length() > 0) {
                        this.setInstruction(fromServer);
                        this.setInstructionPrepared(true);
                        progressOutpuWriter.println(fromServer);
                        sleepTooLong = 0;
                    }
                    continue;
                }
                this.readInstruction();
                // 若游戏未开始，则进行检查，若用户已全部准备好，则开始游戏
                if (!gameStarted && !viewStarted) {
                    // 先检查是否用户全都想要开始新游戏
                    // 再检查是否用户全都想要load或者view
                    boolean allprepared = true;
                    boolean load = true;
                    boolean view = true;
                    for (int i = 0; i < 5; i++) {
                        if (clientConnected[i]) {
                            if (!gamePrepared[i]) {
                                allprepared = false;
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < 5; i++) {
                        if (clientConnected[i]) {
                            if (!wantToLoad[i]) {
                                load = false;
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < 5; i++) {
                        if (clientConnected[i]) {
                            if (!wantToView[i]) {
                                view = false;
                                break;
                            }
                        }
                    }
                    if (!allprepared && !load && !view) {
                        if (fromServer.length() > 0) {
                            this.setInstruction(fromServer);
                            this.setInstructionPrepared(true);
                            progressOutpuWriter.println(fromServer);
                            sleepTooLong = 0;
                        }
                        continue;
                    } else if (allprepared) {
                        gameStarted = true;
                        this.setModelStart();
                        fromServer += "M" + this.map.getMapId() + ";";
                        this.view.mainPanel.setgameStart(true);
                        this.view.mainPanel.repaint();
                        fromServer += "S;";
                    } else if (load) {
                        gameStarted = true;
                        this.loadState("savedState");
                        fromServer += "M" + this.map.getMapId() + ";";
                        this.view.mainPanel.setgameStart(true);
                        this.view.mainPanel.repaint();
                        fromServer += "S;";
                    } else if (view) {
                        // 在一个循环里，读取preProgress.txt中的信息，发送给客户端，并存入savedProgress.txt中
                        java.io.BufferedReader preProgressBufferedReader = new java.io.BufferedReader(
                                new java.io.FileReader("preProgress.txt"));
                        String line;
                        long previousTime2 = System.currentTimeMillis();
                        while ((line = preProgressBufferedReader.readLine()) != null) {
                            try {
                                // 使用动态帧率控制，记录每次循环的开始时间，以及结束时间，计算时间差，然后进行sleep
                                long currentTime = System.currentTimeMillis();
                                long timeDiff = currentTime - previousTime2;
                                long sleep = 33 - timeDiff;
                                if (sleep < 0) {
                                    sleep = 0;
                                }
                                Thread.sleep(sleep);
                                previousTime2 = System.currentTimeMillis();
                            } catch (InterruptedException e) {
                                System.err.println("Thread was interrupted: " + e.getMessage());
                                this.view.mainPanel.setgameStart(false);
                                Thread.currentThread().interrupt();
                                break;
                            }
                            fromServer = line;
                            progressOutpuWriter.println(line);
                            this.setInstruction(fromServer);
                            this.setInstructionPrepared(true);
                        }
                        this.viewStarted = true;
                        this.actors = new Actor[400];
                        this.creater = null;
                        for (int i = 0; i < 5; i++) {
                            if (clientConnected[i]) {
                                this.wantToLoad[i] = false;
                                this.wantToView[i] = false;
                                this.gamePrepared[i] = false;
                                this.Pnid[i] = -1;
                            }
                        }
                        preProgressBufferedReader.close();
                    }
                    // 这里需要添加类似的逻辑，检查是否是load或者view
                }
                // 检查prepared的用户数量是否发生变化，若是，则更新preparedplayerNumber，并fromServer += "S;"
                int nowpreparedplayerNumber = 0;
                for (int i = 0; i < 5; i++) {
                    if (clientConnected[i]) {
                        if (gamePrepared[i]) {
                            nowpreparedplayerNumber++;
                        }
                    }
                }
                // 这里是一个废弃的逻辑，本意是在游戏开始后仍可自由加入玩家
                // 但由于逻辑中创建player只在setgamestart()中执行，而非addclient()中执行，所以这里的逻辑是无效的
                // 但是遵循当代码能跑时就不要乱动的原则，所以暂时保留这段代码
                if (nowpreparedplayerNumber != preparedplayerNumber) {
                    preparedplayerNumber = nowpreparedplayerNumber;
                    fromServer += "S;";
                }
                // 检查存活玩家数量是否为0，若是，则发送指令，同时停止游戏
                if (playerLivingNumber == 0) {
                    fromServer += "D;";
                    this.setgameStart(false);
                    this.setInstruction(fromServer);
                    this.setInstructionPrepared(true);
                    this.actors = new Actor[400];
                    this.creater = null;
                    for (int i = 0; i < 5; i++) {
                        if (clientConnected[i]) {
                            this.wantToLoad[i] = false;
                            this.wantToView[i] = false;
                            this.gamePrepared[i] = false;
                            this.Pnid[i] = -1;
                        }
                    }
                    progressOutpuWriter.println(fromServer);
                    progressOutpuWriter.close();
                    sleepTooLong = 0;
                    continue;
                }
                if (this.getGameWin()) {
                    this.setGameWin(false);
                    fromServer += "W;";
                    this.setgameStart(false);
                    this.setInstruction(fromServer);
                    this.setInstructionPrepared(true);
                    this.actors = new Actor[400];
                    this.creater = null;
                    for (int i = 0; i < 5; i++) {
                        if (clientConnected[i]) {
                            this.wantToLoad[i] = false;
                            this.wantToView[i] = false;
                            this.gamePrepared[i] = false;
                            this.Pnid[i] = -1;
                        }
                    }
                    progressOutpuWriter.println(fromServer);
                    progressOutpuWriter.close();
                    sleepTooLong = 0;
                    continue;
                }

                // 若游戏已开始，则进行游戏逻辑处理
                // 先考虑获得已知的地图信息并发送，再更新player状态
                // 新增一个函数，用来生成指令的信息部分
                // 考虑生成的指令格式，应该包含什么？
                // 应该包含所有的玩家信息，所有的敌人信息，所有的子弹信息，所有的地图元素信息，以及游戏是否结束的信息
                // 玩家信息包括玩家的位置，玩家的方向，玩家的生命值，玩家的分数，玩家的类型，只要满足绘图要求即可
                // 敌人信息包括敌人的位置，敌人的方向，敌人的生命值，敌人的类型，只要满足绘图要求即可
                // 子弹信息包括子弹的位置，子弹的方向，子弹的类型，只要满足绘图要求即可
                fromServer += this.getString();

                sleepTooLong = 0;
                view.mainPanel.repaint();

                for (int i = 0; i < 5; i++) {
                    if (clientConnected[i]) {
                        System.out.println("    response to client" + i);
                        Player p = (Player) (this.getActor(this.getPnid(i)));
                        p.responseInput(this.isMoveUp(i), this.isMoveDown(i), this.isMoveLeft(i), this.isMoveRight(i),
                                this.isFire(i));
                    }
                }
                this.setInstruction(fromServer);
                this.setInstructionPrepared(true);
                progressOutpuWriter.println(fromServer);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            gameStarted = false;
            view.mainPanel.setgameStart(false);
            Thread.currentThread().interrupt();
        }

    }

    private synchronized int readInstruction() {
        for (int id = 0; id < 5; id++) {
            if (this.clientConnected[id]) {
                // 使用局部变量来存储serverInstruction的副本
                String instructionCopy;
                synchronized (this) {
                    if (this.clientInstruction[id].length() == 0) {
                        return 0;
                    }
                    // 复制serverInstruction字符串以在副本上进行操作
                    instructionCopy = this.clientInstruction[id];
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

                    // 解析指令
                    if (perInstruction.substring(0, 1).equals("P")) {
                        this.setGamePrepared(id, true);// 设置玩家准备状态
                    }
                    if (perInstruction.substring(0, 1).equals("L")) {
                        this.wantToLoad[id] = true;
                    }
                    if (perInstruction.substring(0, 1).equals("V")) {
                        this.wantToView[id] = true;
                    }
                    if (perInstruction.substring(0, 1).equals("m")) {
                        String temp = perInstruction.substring(1, 2);
                        if (temp.equals("1")) {
                            this.setUp(id, true);
                        } else {
                            this.setUp(id, false);
                        }
                        temp = perInstruction.substring(2, 3);
                        if (temp.equals("1")) {
                            this.setDown(id, true);
                        } else {
                            this.setDown(id, false);
                        }
                        temp = perInstruction.substring(3, 4);
                        if (temp.equals("1")) {
                            this.setLeft(id, true);
                        } else {
                            this.setLeft(id, false);
                        }
                        temp = perInstruction.substring(4, 5);
                        if (temp.equals("1")) {
                            this.setRight(id, true);
                        } else {
                            this.setRight(id, false);
                        }
                        temp = perInstruction.substring(5, 6);
                        if (temp.equals("1")) {
                            this.setFire(id, true);
                        } else {
                            this.setFire(id, false);
                        }
                    }
                }
            }
        }
        return 1;
    }

    // 先考虑从头开始，设置gamestart==true后，用一个函数开启所有的线程，包括敌人生成器，玩家，子弹，敌人，地图元素等等
    // 然后，所有的线程都会开始运行，此时，只需要在每个线程的run函数中，检查gamestart的值，若为false，则退出线程，否则继续运行
    public synchronized void setModelStart() {
        // 首先，设置随机地图，然后初始化地图元素
        this.map.setRandomMap();
        // 对于500*600的地图，每个格子的大小为25*25
        // 先用一圈boundary，在外侧把地图围起来
        for (int i = 0; i < 20; i++) {
            Boundary boundary = new Boundary(this, -1, 12 + i * 25, -12);
            this.addActor(boundary);
        }
        for (int i = 0; i < 24; i++) {
            Boundary boundary = new Boundary(this, -1, 512, 12 + i * 25);
            this.addActor(boundary);
        }
        for (int i = 0; i < 20; i++) {
            Boundary boundary = new Boundary(this, -1, 12 + i * 25, 612);
            this.addActor(boundary);
        }
        for (int i = 0; i < 24; i++) {
            Boundary boundary = new Boundary(this, -1, -12, 12 + i * 25);
            this.addActor(boundary);
        }
        // 再根据地图，设置地图元素
        String[] mapString = this.map.getMap();
        for (int i = 0; i < 400; i++) {
            if (mapString[i].equals("##")) {
                Wall wall = new Wall(this, -1, 12 + i % 20 * 25, 12 + i / 20 * 25 + 50);
                this.addActor(wall);
            }
        }

        // 开启敌人生成器线程
        EnemyCreater creater = new EnemyCreater(this, this.map);
        this.setEnemyCreater(creater);
        view.exec.execute(this.creater);
        // 开启玩家线程
        for (int i = 0; i < 5; i++) {
            if (clientConnected[i]) {
                Player player = new Player(this, i, -1, i * 90 + 90, 570);
                this.addActor(player);
                this.setPnid(i, player.getId());
                this.setPlayerLivingNumber(this.getPlayerLivingNumber() + 1);
            }
        }
    }

    // 用于生成指令的信息部分
    public synchronized String getString() {
        String info = "";
        for (int i = 0; i < 400; i++) {
            if (this.getActor(i) != null) {
                if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Player")) {
                    Player p = (Player) this.getActor(i);
                    System.out.println("player:  p.toString()");
                    info += p.toString();
                }
                if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Enemy")) {
                    Enemy e = (Enemy) this.getActor(i);
                    System.out.println("enemy:  e.toString()");
                    info += e.toString();
                }
                if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Bullet")) {
                    Bullet b = (Bullet) this.getActor(i);
                    System.out.println("bullet:  b.toString()");
                    info += b.toString();
                }
            }
        }
        return info;
    }

    // 将整个游戏的当前状态保存下来
    public void saveState(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath + ".txt");
            java.io.PrintWriter output = new java.io.PrintWriter(file);
            output.println(this.playerNumber);
            output.println(this.playerLivingNumber);
            output.println(this.map.getMapId());
            output.println(this.creater.toString());
            String info = "";
            for (int i = 0; i < 400; i++) {
                if (this.getActor(i) != null) {
                    if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Player")) {
                        Player p = (Player) this.getActor(i);
                        System.out.println("player:  p.toString()");
                        info += p.toString();
                    }
                    if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Enemy")) {
                        Enemy e = (Enemy) this.getActor(i);
                        System.out.println("enemy:  e.toString()");
                        info += e.toString();
                    }
                    if (this.getActor(i).getClass().getName().equals("com.mycompany.app.Bullet")) {
                        Bullet b = (Bullet) this.getActor(i);
                        System.out.println("bullet:  b.toString()");
                        info += b.toString();
                    }
                }
            }
            output.println(info);
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从保存的状态中读取信息，并设置游戏状态，以便从上次保存的状态开始游戏
    public void loadState(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath + ".txt");
            java.util.Scanner input = new java.util.Scanner(file);
            int playerNumberFile = input.nextInt();
            if (this.getPlayerNumber() != playerNumberFile) {
                this.setModelStart();
            } else {
                this.playerLivingNumber = input.nextInt();
                int mapId = input.nextInt();
                this.map.setMap(mapId);
                // 首先，初始化地图元素
                // 对于500*600的地图，每个格子的大小为25*25
                // 先用一圈boundary，在外侧把地图围起来
                for (int i = 0; i < 20; i++) {
                    Boundary boundary = new Boundary(this, -1, 12 + i * 25, -12);
                    this.addActor(boundary);
                }
                for (int i = 0; i < 24; i++) {
                    Boundary boundary = new Boundary(this, -1, 512, 12 + i * 25);
                    this.addActor(boundary);
                }
                for (int i = 0; i < 20; i++) {
                    Boundary boundary = new Boundary(this, -1, 12 + i * 25, 612);
                    this.addActor(boundary);
                }
                for (int i = 0; i < 24; i++) {
                    Boundary boundary = new Boundary(this, -1, -12, 12 + i * 25);
                    this.addActor(boundary);
                }
                // 再根据地图，设置地图元素
                String[] mapString = this.map.getMap();
                for (int i = 0; i < 400; i++) {
                    if (mapString[i].equals("##")) {
                        Wall wall = new Wall(this, -1, 12 + i % 20 * 25, 12 + i / 20 * 25 + 50);
                        this.addActor(wall);
                    }
                }

                String createrInfo = input.next();
                this.creater = new EnemyCreater(this, this.map);
                this.creater.setEnemyCreater(createrInfo);
                view.exec.execute(creater);

                String info = input.next();
                int clienti = 0;
                int i = 0;
                while (i < info.length()) {
                    StringBuilder perInfo = new StringBuilder();
                    while (!info.substring(i, i + 1).equals(";")) {
                        perInfo.append(info.substring(i, i + 1));
                        i++;
                        // 如果到达字符串末尾，跳出循环
                        if (i >= info.length())
                            break;
                    }
                    i++; // 跳过分号";"

                    // 解析指令
                    if (perInfo.substring(0, 1).equals("P")) {
                        Player player = new Player(this, -1, -1, 0, 0);
                        player.setInfo(perInfo.toString());
                        this.addActor(player);
                        this.setPnid(clienti++, player.getId());
                        // this.setPlayerLivingNumber(this.getPlayerLivingNumber() + 1);
                        // this.setPnid(id, player.getId());
                    }
                    if (perInfo.substring(0, 1).equals("E")) {
                        Enemy enemy = new Enemy(this, -1, -1, 0, 0);
                        enemy.setInfo(perInfo.toString());
                        this.addActor(enemy);
                    }
                    if (perInfo.substring(0, 1).equals("B")) {
                        /*
                         * Bullet bullet = new Bullet(this, null, -1, -1, 0, 0, 0)
                         * bullet.setInfo(perInfo.toString());
                         * this.addActor(bullet);
                         */
                        ;
                        // 高情商的说法：为了减少游戏难度，不加载子弹
                        // 不考虑情商的说法：子弹要和创建者绑定，想加载子弹需要实现在字符串里记录子弹的创建者，这不符合精简的原则
                        // 低情商的说法：完成任务要求即可，没必要在这里给自己加难度
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
