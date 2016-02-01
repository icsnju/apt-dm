package dm.mobile.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;

import dm.mobile.model.MobileModel;
import dm.model.TreeModel;


public class ADBStart {
	private  TreeModel treeModel;
	private final static String ADBPATH="/Users/Tianchi/Tool/adt-bundle-mac-x86_64-20140702/sdk/platform-tools/adb";
	//"/home/cslab/Software/eclipse/sdk/platform-tools/adb"
	///Users/Tianchi/Tool/eclipse/android_sdk/platform-tools/adb
	public ADBStart(TreeModel m)
	{
		treeModel=m;
	}
	
    public  void startADB() {
    	AndroidDebugBridge.terminate();
        AndroidDebugBridge.init(true);
        
        AndroidDebugBridge debugBridge = AndroidDebugBridge.createBridge(ADBPATH, true);
        if (debugBridge == null) {
            System.err.println("Invalid ADB location.");
            System.exit(1);
        }
        

        AndroidDebugBridge.addDeviceChangeListener(new IDeviceChangeListener() {    	
            @Override
            public void deviceChanged(IDevice device, int arg1) {
               // System.out.println("device change");
            }

            @Override
            public void deviceConnected(IDevice device) {
            	System.out.println("new device connected..:");
            	if(device==null)return;
            	MobileModel mm=new MobileModel(device);
            	
            	treeModel.addModel(mm);
            }

            @Override
            public void deviceDisconnected(IDevice device) {
                System.out.println(String.format("device %s disconnected", device.getName()));
                treeModel.removeModel(device.getSerialNumber());
            }

        });
        
    }
    
}