package devicemanager.mobile.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.ImageData;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.ddmlib.logcat.LogCatReceiverTask;

import devicemanager.mobile.thread.ScreenShotThread;
import devicemanager.model.BasicModel;
import devicemanager.view.LogcatView;
import devicemanager.view.MobileInfoView;

/*
 * every value has get and set functions
 */
public class MobileModel extends BasicModel {
	// private String name="";//device name
	private String serialNumber = "";// * serial number for identifying a device
	// private String apiLevel="";//android api level e.g. "16"
	// private String buildVersion="";//android build version e.g. "4.1.1"
	// private String cpuAbi="";//cpu version e.g. "armeabi-v7a"
	private String manufacturer = "";// producer e.g. "Xiao mi"
	private String model = "";// model e.g. "MI 2"
	private IDevice device;

	private LogCatListener lcl; // get logcat
	private LogCatReceiverTask logTask;
	private Thread mThread;

	private Thread ssThread;
	private ScreenShotThread sst;

	private HashSet<LogcatView> logcatViews;
	private HashSet<MobileInfoView> infoViews;

	public MobileModel(IDevice d) {
		setDevice(d);
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
				// System.out.println("logcat 1");
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
	}

	@Override
	public String getName() {
		return getModel() + " [" + getSerialNumber() + "]";
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		if (serialNumber != null)
			this.serialNumber = serialNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		if (model != null)
			this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		if (manufacturer != null)
			this.manufacturer = manufacturer;
	}

	@Override
	public String getID() {
		return serialNumber;
	}

	public IDevice getDevice() {
		return device;
	}

	public void setDevice(IDevice device) {
		this.device = device;
		try {
			setModel(device.getPropertyCacheOrSync(IDevice.PROP_DEVICE_MODEL));
			setManufacturer(device
					.getPropertyCacheOrSync(IDevice.PROP_DEVICE_MANUFACTURER));
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("adb get model wrong!");
		}
		serialNumber = device.getSerialNumber();
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

}
