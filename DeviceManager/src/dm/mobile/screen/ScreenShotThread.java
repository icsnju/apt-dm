package dm.mobile.screen;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import com.android.ddmlib.RawImage;
import dm.mobile.model.MobileModel;

/*
 * get screenshot 
 */
public class ScreenShotThread implements Runnable {

	private Boolean flag = true;
	private MobileModel mModel;

	public ScreenShotThread(MobileModel mm) {
		mModel=mm;
	}
	@Override
	public void run() {
       //System.out.println("run");
		float zoom=mModel.getZoom();
		while (flag) {		
				//get rawImage
				RawImage rawImage;
				try {
					rawImage = mModel.getScreenshot();
					//System.out.println("image 1");
					
					// convert rawImage to ImageData
					PaletteData palette = new PaletteData(rawImage.getRedMask(),
							rawImage.getGreenMask(), rawImage.getBlueMask());
					
					ImageData imageData = new ImageData(rawImage.width,
							rawImage.height, rawImage.bpp, palette, 1,
							rawImage.data);
					
					int th=(int)(((float)rawImage.height)*zoom);
					
					ImageData newId;
					if(zoom!=1)
						newId=imageData.scaledTo(MobileModel.SHOT_W,th);
					else
						newId=imageData;
					mModel.updateShotView(newId);
				} catch (Exception ioe) {
					System.out.println("Unable to get frame buffer: "
							+ ioe.getMessage());
					flag=false;
					return;
				}
			}

	}

	public void stop() {
		flag = false;
	}

}
