## DeviceManager开发笔记
### by tianchi  
----    

###RCP  

**Application**  
Workbench().createUI()  

**entrypoint**  
实现`EntryPoint`类，主要是create`display`和创建`WorkbenchAdvisor`。只能创建一个display，它用来结合操作系统。  

**WorkbenchAdvisor**  
返回`Perspective`的ID,创建`WorkbenchWindowAdvidor`。  

**Perspective**  
透视图，界面的初始布局。

**WorkbenchWindowAdvisor**  
设置窗体大小，设置Bar，创建`ActionBarAdvisor`，设置标题，设置进度条。

**ActionBarAdvisor**  
设置`MenuBar`,`CoolBar`,`StatusLine`的各种action。  


----
###Rcp ProjectExplorer 添加  

1.详情参见 eclipse Platform Plug-in Developer Guide > Programmer's Guide > Common Navigator Framework > Step-by-step Instructions
[here](http://help.eclipse.org/indigo/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/cnf.htm)

----
###截屏点击事件的响应  
1.使用`monkeyrunner`和`chimpchat`   
2.*AdbChimpDevice* 将IDevice 转换为IChimpDevice  
3.执行命令 new TouchAction(x, y, MonkeyDevice.DOWN_AND_UP)).execute(IChimpDevice)  
4.原monkeyrunner 中包含chimpchat里的功能，现在已经被单独分离出来   
5.在DM项目中，创建IChimpDevice的AdbChimpDevice是一个阻塞过程，所以为了防止长时间的阻塞导致界面的卡主，将该过程移至ADBPrecess.java中，在获得该IDevice创建model时，将该对象作为model的属性添加进去。    
6.**Problem Solve:** view 更新后不能及时刷新的问题,Composite.`layout(true,true)`可以解决。  

---  

###操作脚本的录制    
1.`uiautomator`使用周严学长的项目封装成库来调用。  
2.


