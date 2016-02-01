package devicemanager.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import devicemanager.mobile.model.MobileModel;
import devicemanager.model.TreeModel;
import devicemanager.model.TreeModel.TreeObject;
import devicemanager.view.ListView;

public class LogcatView extends ViewPart implements ISelectionListener {

	public static final String ID = "DeviceManagerWeb.View.LogcatView";
	private Text logText;
	private Display display;
	private Composite parent;
	private TreeObject treeObj;
	private MobileModel mMobileModel = null;
	private String logString = "";

	private ServerPushSession pushSession;

	public LogcatView() {
		super();
		pushSession = new ServerPushSession(); // long polling
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		logText = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		logText.setEditable(false);
		display = Display.getDefault();
		getSite().getPage().addSelectionListener(ListView.ID,
				(ISelectionListener) this);
		pushSession.start();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void updateText(final String log) {
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				logString = log + "--------------------------\n\n" + logString;
				logText.setText(logString);
				// logText.setSelection(Integer.MAX_VALUE);
				// logText.setSelection(logText.getText().length());
				logText.update();
				// System.out.println(" Log Update OK");
			}
		});

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection != null) {
			treeObj = (TreeModel.TreeObject) ((IStructuredSelection) selection)
					.getFirstElement();
			if (treeObj != null && !(treeObj instanceof TreeModel.TreeParent))
				if (treeObj.getModel() != null
						&& treeObj.getModel() instanceof MobileModel) {
					if (mMobileModel != null)
						mMobileModel.stopLog(this);
					//System.out.println("log stop");
					mMobileModel = (MobileModel) treeObj.getModel();
					logString = mMobileModel.getName() + " Log:\n";
					logText.setText(logString);
					mMobileModel.startLog(this);
				}
		}

	}

	public void dispose() {
		pushSession.stop();
		super.dispose();
	}

}
