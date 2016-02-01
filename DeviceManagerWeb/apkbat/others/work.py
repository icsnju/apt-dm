import sys,time,datetime
from com.android.monkeyrunner import MonkeyRunner as mr
from com.android.monkeyrunner import MonkeyDevice as md
from com.android.monkeyrunner import MonkeyImage as mi
deviceslist = []
devices = []
snapshot = []
templist = []
f = open("C:\Users\mcl\Desktop\myproject\devices.txt")

while True:
    line = f.readline()
    if line:
        templist.append(line.strip())
    else:
        break;
f.close()
templist.pop()
for i in range(len(templist)):
    deviceslist.append(templist[i].split('\t'))
fc = open("C:\Users\mcl\Desktop\myproject\componentName.txt")
complist = []
while True:
    comp = fc.readline()
    if comp:
        complist.append(comp.strip())
    else:
        break;
fc.close()
fp = open("C:/Users/mcl/Desktop/myproject/apk.txt")
apklist = []
while True:
    apk = fp.readline()
    if apk:
        apklist.append(apk.strip())
    else:
        break;
print 'apk list :'
print apklist
print 'start componentName list :'
print complist
print 'devices list:'
print deviceslist
for i in range(1,len(deviceslist)):
    print 'current devices:'
    print deviceslist[i]
    devices.append(mr.waitForConnection(1.0,deviceslist[i][0]))
    #安装apk文件
    for j in range(len(apklist)):
        devices[i-1].installPackage('C:/Users/mcl/Desktop/myproject/apk/'+apklist[j])
    #启动activity
    for k in range(len(complist)):
        print 'current start activity:'
        print complist[k]
        devices[i-1].startActivity(component=complist[k])
        #设置延时秒数
        mr.sleep(2.0)
        #----------------
        #这里可进行一定的UI操作
        #----------------
        #mr.sleep(3.0)
        #进行截图
        snapshot.append(devices[i-1].takeSnapshot())
        print 'end snapshot'
        #创建时间字符串
        t = time.strftime("%Y-%m-%d-%X",time.localtime())
        t = t.replace(":","-")
        #保存截图
        package = complist[k].replace('/.','.')
        snapshot[0].writeToFile('C:/Users/mcl/Desktop/myproject/takeSnapshot/'+deviceslist[i][0]+'-'+t+'-'+package+'.png','png');
        snapshot.pop()
		