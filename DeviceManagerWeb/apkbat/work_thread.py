import sys,time,datetime,thread,os
from com.android.monkeyrunner import MonkeyRunner as mr
from com.android.monkeyrunner import MonkeyDevice as md
from com.android.monkeyrunner import MonkeyImage as mi

BASE_DIR = os.path.abspath(os.path.dirname(sys.argv[0])) #获取当前文件夹的绝对路径
print BASE_DIR
	
deviceslist = []
devices = []
snapshot = []
f = open(os.path.join(BASE_DIR,'devices.txt'))

while True:
    line = f.readline()
    if line and line.strip() :
        deviceslist.append(line.strip())
    else:
        break;
f.close()
fc = open(os.path.join(BASE_DIR,'componentName.txt'))
complist = []
while True:
    comp = fc.readline()
    if comp:
        complist.append(comp.strip())
    else:
        break;
fc.close()
fp = open(os.path.join(BASE_DIR,'apk.txt'))
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

def thread_monkey(device):
	for i in range(0,len(apklist)):
		device.installPackage(os.path.join(BASE_DIR,"apk/"+apklist[i]))
		for j in range(0,len(complist)):
			runComponent  = complist[j]
#		package = 'com.example.android.myapplication'
#		activity = 'com.example.android.myapplication.MainActivity'
#		runComponent = package + '/' + activity
			device.startActivity(component=runComponent)
			mr.sleep(2.0)
			id = device.getProperty('build.device')
			result = device.takeSnapshot()
			result.writeToFile(os.path.join(BASE_DIR,'takeSnapshot/'+id+'_shot.png'),'png')
	
for i in range(0,len(deviceslist)):
	print 'current devices:'
	print deviceslist[i]
	devices.append(mr.waitForConnection(1.0,deviceslist[i]))
	thread.start_new_thread(thread_monkey, (devices[i], ) )

time.sleep(15)

		

    
		