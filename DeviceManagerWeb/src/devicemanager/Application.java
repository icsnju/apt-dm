package devicemanager;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements EntryPoint {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
//	public Object start(IApplicationContext context) {
//		Display display = PlatformUI.createDisplay();
//		try {
//			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
//			if (returnCode == PlatformUI.RETURN_RESTART) {
//				return IApplication.EXIT_RESTART;
//			}
//			return IApplication.EXIT_OK;
//		} finally {
//			display.dispose();
//		}
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
//	public void stop() {
//		if (!PlatformUI.isWorkbenchRunning())
//			return;
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		final Display display = workbench.getDisplay();
//		display.syncExec(new Runnable() {
//			public void run() {
//				if (!display.isDisposed())
//					workbench.close();
//			}
//		});
//	}

	@Override
	public int createUI() {
		  Display display = PlatformUI.createDisplay();
		  return PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
	}
}
