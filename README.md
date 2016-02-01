TODO
---------

1. 若没有设备，不显示默认设备信息。
2. 根据手机的序列号，不重复添加相同的设备。
3. 开始时没有选中任何设备，那deviceModel是不存在的，但selectionchanged接口仍被触发了。

special：  
1. getname（）函数刚连上去时显示 xiaomi - mi2 -201...
拔下重插，getname 只显示了201...即序列号  
2. 选择设备会触发 Null argument的exception。model被销毁了？treeobj.getModel().getname() 返回了NULL。

----
2014/7/11 17:06:47   

- 以后交流文档按照这种markdown格式写，这样github能自动显示出来
- 已经修改成默认不显示信息
- 显示信息窗口在失去焦点时不会做出响应，所以你需要先点击一下，那个窗口才可以
- 已经修改成连接的手机显示，不连接的手机会消失
- 最后一种special的情况就开始时发生了一次，然后就从来没有发生过，不知道原因是什么。

----
2014/7/21 

- rcp 转换为rap,见DeviceManagerWeb
- 遇到界面刷新问题

-----
2014/7/23

- upload/install apk
- logcat
