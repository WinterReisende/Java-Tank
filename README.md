本文件夹包含游戏服务器端的实现代码。  
其主要结构如下：  
ServerView作为JFrame的派生类，实现了完整的游戏界面，同时包含一个线程池，用来调度不同的线程，此外不具备太多的逻辑功能。  
ServerController是一个单独的类，其中包含了对若干按钮和键盘操作的响应控制，没有太多的逻辑功能。  
Actor是各种游戏对象的均会继承的接口，代表了基本功能。  
Player、Enemy、Bullet、Wall、Boundary均继承了Actor。  
Map类中编码了地图的字符串表示，以便ServerModel中据此利用各种Actor生成游戏场景，同时包含一系列用来保存地图到文件以及从文件读取地图的函数。  
EnemyCreater类用来动态生成敌人  
ServerModel是Runnable的派生类，其中实现了主要的游戏逻辑：  
    - 首先，在run函数中有一个循环，用于检查游戏状态并根据客户端发送的信息来调用Player对象的响应函数进行行动，同时生成要发送给客户端的指令  
    - readInstruction函数的功能是对客户端发送的信息进行解析，从而调整记录的各种信息，形成可供解析的控制信息。  
    - setModelStart函数用来执行创建新游戏的功能。  
    - getString函数用来生成详细的指令。  
    - saveState函数用来将游戏的当前状态保存到文件，以便下次加载。  
    - loadState函数用来从文件加载游戏。  
ServerSelector类是Runnable的派生类，通过NIO的方式与客户端进行通信。  
DrawingPanel类没有作用，只是为了保证文件结构的一致性而存在的。  
总的来说，游戏服务器端通过处理从客户端发送的信息来控制游戏流程，实现开启新游戏、加载进度、回看等功能。  