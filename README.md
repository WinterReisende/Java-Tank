本文件夹包含游戏客户端的实现代码。  
其主要结构如下：  
ClientView作为JFrame的派生类，实现了完整的游戏界面，同时包含一个线程池，用来调度不同的线程，此外不具备太多的逻辑功能。  
ClientController是一个单独的类，其中包含了对若干按钮和键盘操作的响应控制，没有太多的逻辑功能。  
Actor是各种游戏对象的均会继承的接口，代表了基本功能，主要是draw方法，用来进行绘图。  
Player、Enemy、Bullet、Wall均继承了Actor。  
Map类中编码了地图的字符串表示，以便ClientModel中据此利用各种Actor生成游戏场景。  
ClientModel是Runnable的派生类，其中实现了主要的游戏逻辑：
  - 首先，在run函数中有一个循环，用于检查游戏状态并根据键盘输入生成要发送给服务器的控制信息。  
  - readInstruction函数的功能是对服务器发送的信息进行解析，从而控制游戏中各种对象的行为，形成可供DrawingPanel进行绘图的信息。  
  - drawingList与drawingList2分别用来存储不可移动的物体和可以移动的对象。  
ClientSocket是Runnable的派生类，用来和服务器端进行通信，交换游戏信息。  
DrawingPanel类的功能是根据drawingList与drawingList2的信息进行绘制。  
  
总的来说，游戏客户端并无实际的游戏逻辑代码，他的功能是根据按钮和键盘输入与服务器端进行通信，并把游戏画面绘制到屏幕上。  
