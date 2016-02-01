package devicemanager.mobile.thread;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;

import devicemanager.mobile.model.MobileModel;
import devicemanager.view.MobileInfoView;

/*
 * get screenshot 
 */
public class ScreenShotThread implements Runnable {

	private IDevice device = null;
	private Boolean flag = true;
	private MobileModel mModel;
	
	public ScreenShotThread(MobileModel mm) {
		mModel=mm;
		device=mModel.getDevice();
	}
	@Override
	public void run() {
       //System.out.println("run");
		while (flag) {
			IDevice d=device;

			if(d!=null)
			{			
				//get rawImage
				RawImage rawImage;
				try {
					rawImage = device.getScreenshot();
					//System.out.println("image 1");
					
					// convert rawImage to ImageData
					PaletteData palette = new PaletteData(rawImage.getRedMask(),
							rawImage.getGreenMask(), rawImage.getBlueMask());
					
					ImageData imageData = new ImageData(rawImage.width,
							rawImage.height, rawImage.bpp, palette, 1,
							rawImage.data);
					
					ImageData newId=imageData.scaledTo(MobileInfoView.SHOT_W,MobileInfoView.SHOT_H);
					mModel.updateShotView(newId);
					//System.out.println("run");
				} catch (Exception ioe) {
					System.out.println("Unable to get frame buffer: "
							+ ioe.getMessage());
					flag=false;
					return;
				}
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		flag = false;
	}

}
