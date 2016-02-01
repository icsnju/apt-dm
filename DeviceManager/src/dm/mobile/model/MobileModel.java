package dm.mobile.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.graphics.ImageData;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.ddmlib.logcat.LogCatReceiverTask;
import com.zhouyan.script.ScriptTranslate;

import dm.mobile.screen.ScreenShotThread;
import dm.model.BasicModel;
import dm.view.LogcatView;
import dm.view.MobileInfoView;

/*
 * every value has get and set functions
 */
public class MobileModel extends BasicModel {

	public static final int SHOT_W=350;			//screen shot width
	private String serialNumber = "";			// * serial number for identifying a device
	private String manufacturer = "";			// producer
	private String model = "";					// model
	private String api="";
	private String buildVersion="";
	private String cpuAbi="";
	private String isOnline="";
	
	private IDevice device;

	private LogCatListener lcl; 				// get logcat
	private LogCatReceiverTask logTask;
	private Thread mThread;

	private Thread ssThread;		//screen shot thread
	private ScreenShotThread sst;

	private HashSet<LogcatView> logcatViews;
	private HashSet<MobileInfoView> infoViews;

	private int imageW;
	private int imageH;
	float zoom;
	
	private  ExecutorService executor = Executors.newCachedThreadPool();
	
  	private ScriptTranslate sTranslate;		//script record thread
  	private Thread stThread;	
	
	public MobileModel(IDevice d) {
		setDevice(d);
		// a model has more than one view 
		logcatViews = new HashSet<LogcatView>();
		infoViews = new HashSet<MobileInfoView>();
		lcl = new LogCatListener() {
			@Override
			public void log(List<LogCatMessage> msgList) {
				String mLog = "Called with messages list length "
						+ msgList.size() + "\n";
				for (LogCatMessage msg : msgList) {
					mLog = mLog + msg.toString() + "\n";
				}
				// update all viewer
				synchronized (logcatViews) {
					for (Iterator<LogcatView> it = logcatViews.iterator(); it
							.hasNext();) {
						LogcatView lv = it.next();
						lv.updateText(mLog);
					}
				}
			}
		};
		
		RawImage rawImage;
		try {
			rawImage = device.getScreenshot();
			imageW=rawImage.width;
			imageH=rawImage.height;
			zoom=((float)SHOT_W)/((float)imageW);
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			zoom=1;
			e.printStackTrace();
		}	
	}

	//screen shot and resolution
	public float getZoom() {
		return zoom;
	}
	
	public int getImageW() {
		return imageW;
	}
	
	public int getImageH() {
		return imageH;
	}
	
	//basic information
	@Override
	public String getName() {
		return getModel() + " [" + getSerialNumber() + "]";
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getModel() {
		return model;
	}

	private void setModel(String model) {
		if (model != null)
			this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	private void setManufacturer(String manufacturer) {
		if (manufacturer != null)
			this.manufacturer = manufacturer;
	}

	@Override
	public String getID() {
		return serialNumber;
	}
	
	public String getApi() {
		return api;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public String getCpuAbi() {
		return cpuAbi;
	}

	public String getIsOnline() {
		return isOnline;
	}

	private void setDevice(IDevice device) {
		this.device = device;
		try {
			setModel(device.getPropertyCacheOrSync(IDevice.PROP_DEVICE_MODEL));
			setManufacturer(device.getPropertyCacheOrSync(IDevice.PROP_DEVICE_MANUFACTURER));
			api=device.getPropertyCacheOrSync(IDevice.PROP_BUILD_API_LEVEL);
			buildVersion=device.getPropertyCacheOrSync(IDevice.PROP_BUILD_VERSION);
			cpuAbi=device.getPropertyCacheOrSync(IDevice.PROP_DEVICE_CPU_ABI);
			isOnline=device.isOnline()?"yes":"no";
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e) {
			System.out.println("adb get model wrong!");
			e.printStackTrace();
		}
		serialNumber = device.getSerialNumber();
		
		sTranslate=new ScriptTranslate(device);
	}

	// logcat thread
	public void startLog(LogcatView v) {
		boolean isEpt;
		synchronized (logcatViews) {

			isEpt = logcatViews.isEmpty();
			logcatViews.add(v);
		}

		if (isEpt) {
			logTask = new LogCatReceiverTask(device);
			logTask.addLogCatListener(lcl);
			mThread = new Thread(logTask);
			mThread.start();
			System.out.println("start log");
		}

	}

	public void stopLog(LogcatView v) {
		boolean isEpt;
		synchronized (logcatViews) {
			logcatViews.remove(v);
			isEpt = logcatViews.isEmpty();

		}
		if (isEpt) {
			logTask.stop();
		}

	}

	// screenshot thread
	public void startShot(MobileInfoView v) {
		boolean isEpt;
		// System.out.println("shot 1");
		synchronized (infoViews) {
			isEpt = infoViews.isEmpty();
			infoViews.add(v);
		}
		if (isEpt) {
			// start screenshot thread
			sst = new ScreenShotThread(this);
			ssThread = new Thread(sst);
			ssThread.start();
		}

	}

	public void stopShot(MobileInfoView v) {
		boolean isEpt;
		synchronized (infoViews) {
			infoViews.remove(v);
			isEpt = infoViews.isEmpty();
		}
		if (isEpt) {
			sst.stop();
		}
	}

	public void updateShotView(ImageData id) {
		// update all viewer
		synchronized (infoViews) {
			for (Iterator<MobileInfoView> it = infoViews.iterator(); it
					.hasNext();) {
				MobileInfoView iv = it.next();
				iv.updateShotView(id);
			}
		}
	}
	
	//view execute shell command 
    public void executeAsyncCommand(final String command) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                    try {
						device.executeShellCommand(command, null);
					} catch (TimeoutException | AdbCommandRejectedException
							| ShellCommandUnresponsiveException | IOException e) {
						e.printStackTrace();
					}
            }
        });
    }
    
    //get device clients  
    public Client[] getDeviceClients(){
    	return device.getClients();
    }
    
    //device reboot  
    public void rebootDevice(){
		try {
			device.reboot(serialNumber);
		} catch (TimeoutException | AdbCommandRejectedException
				| IOException e1) {
			e1.printStackTrace();
		}
    }
    
    //install apk
    public void installPackage(String path){
    	try {
			device.installPackage(path,true);
		} catch (InstallException e) {
			e.printStackTrace();
		}
    }
    
    //start script record  
    public void startScriptRecord(){
    	stThread=new Thread(sTranslate);
    	stThread.start();
    }
    
    public void stopScriptTranslate(){
    	sTranslate.stop();
    	stThread.interrupt();
    }
    
    //get image  
    public RawImage getScreenshot(){
    	RawImage image=null;
    	try {
			image= device.getScreenshot();
		} catch (TimeoutException | AdbCommandRejectedException | IOException e) {
			e.printStackTrace();
		}
    	return image;
    }
}
