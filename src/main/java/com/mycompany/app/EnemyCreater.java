package com.mycompany.app;

public class EnemyCreater implements Runnable {
    private ServerModel model;
    private Map map;
    private int level;
    // private int[] enemyNumber;
    private int[] enemyNumberList;
    private int[] enemyTypeList;
    private int leftEnemy;
    private int createInterval;

    public EnemyCreater(ServerModel model, Map map) {
        this.model = model;
        this.map = map;
        this.level = 0;
        // enemyNumber = new int[4];
        enemyNumberList = new int[5];
        enemyNumberList[0] = 1;
        enemyNumberList[1] = 1;
        enemyNumberList[2] = 1;
        enemyNumberList[3] = 1;
        enemyNumberList[4] = 1;
        enemyTypeList = new int[5];
        enemyTypeList[0] = 1;
        enemyTypeList[1] = 1;
        enemyTypeList[2] = 2;
        enemyTypeList[3] = 1;
        enemyTypeList[4] = 2;
        leftEnemy = 0;
        createInterval = 150;
    }

    public void run() {
        try {
            long previousTime = System.currentTimeMillis();
            while (true) {
                if (model.getGameStarted() == false) {
                    break;
                }
                System.out.println("    leftEnemy: " + leftEnemy);
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - previousTime;
                long sleep = 33 - timeDiff;
                if (sleep < 0) {
                    sleep = 0;
                }
                Thread.sleep(sleep);
                previousTime = System.currentTimeMillis();
                if (leftEnemy == 0 && level < 5) {
                    if (createInterval > 0) {
                        createInterval--;
                        continue;
                    }
                    leftEnemy = enemyNumberList[level];
                    createInterval = 100;
                    for (int i = 0; i < enemyNumberList[level]; i++) {
                        Enemy enemy = new Enemy(model, enemyTypeList[level], -1,
                                500 / (enemyNumberList[level] + 1) * (i + 1), 20);
                        model.addActor(enemy);
                    }
                    if (level < 5) {
                        level++;
                    }
                }
                if (leftEnemy == 0 && level == 5) {
                    model.setGameWin(true);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public synchronized void setLeftEnemy(int n) {
        this.leftEnemy = n;
    }

    public synchronized int getLeftEnemy() {
        return this.leftEnemy;
    }

    public synchronized void setLevel(int n) {
        this.level = n;
    }

    public synchronized int getLevel() {
        return this.level;
    }

    // 将enemycreater的全部信息存在一个String中，以便之后通过这个String来重新设置enemycreater的信息
    public String toString() {
        String s = "";
        s += level + ",";
        for (int i = 0; i < 5; i++) {
            s += enemyNumberList[i] + ",";
        }
        for (int i = 0; i < 5; i++) {
            s += enemyTypeList[i] + ",";
        }
        s += leftEnemy + ",";
        s += createInterval + ",";
        return s;
    }

    // 通过一个String来重新设置enemycreater的信息
    public void setEnemyCreater(String s) {
        String[] ss = s.split(",");
        System.out.println(ss.length);
        // 依次输出ss的每一个字符串
        for (int i = 0; i < ss.length; i++) {
            System.out.println(ss[i]);
        }
        level = Integer.parseInt(ss[0]);
        for (int i = 0; i < 5; i++) {
            enemyNumberList[i] = Integer.parseInt(ss[i + 1]);
        }
        for (int i = 0; i < 5; i++) {
            enemyTypeList[i] = Integer.parseInt(ss[i + 6]);
        }
        leftEnemy = Integer.parseInt(ss[11]);
        createInterval = Integer.parseInt(ss[12]);
    }

}
