package devicemanager.view;

import java.io.IOException;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import devicemanager.mobile.model.ClientModel;
import devicemanager.mobile.model.MobileModel;
import devicemanager.model.BasicModel;
import devicemanager.model.TreeModel;

public class MobileInfoView extends ViewPart implements ISelectionListener  {					

	public static final String ID = "DeviceManagerWeb.View.MobileInfoView";
	public static final int SHOT_W=300;
	public static final int SHOT_H=500;
	
	/**
	 * The text control that's displaying the content of the email message.
	 */
	private Display display;
	private Composite parent;
	private Composite banner;
	private Composite left;
	private Composite right;
	private Label sstLabel;

	private ServerPushSession pushSession;
	
	private TreeModel.TreeObject treeObj;      //the information of this treeObj
	private MobileModel mMobileModel;

	public void createPartControl(Composite parent) {

		// top banner	
		this.parent=parent;
		display=Display.getDefault();
		
		banner = new Composite(parent, SWT.MULTI |SWT.H_SCROLL | SWT.V_SCROLL);
//		banner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL,
//				GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 20;
		layout.marginWidth = 30;
		layout.numColumns = 2;
		
		banner.setLayout(layout);
		left=new Composite(banner, SWT.BORDER);
		layout=new GridLayout();
		layout.numColumns=1;
		left.setLayout(layout);
		
		right=new Composite(banner, SWT.BORDER);;
		right.setLayout(new FillLayout());
		sstLabel=new Label(right,SWT.H_SCROLL );
		sstLabel.setSize(SHOT_W,SHOT_H);

		// setup bold font
		Font boldFont = JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT);

		Label l = new Label(left, SWT.WRAP);
		l.setText("Welcome Device Manager!");
		l.setFont(boldFont);
		treeObj=null;

		pushSession=new ServerPushSession();
		pushSession.start();
		// register listener
		getSite().getPage().addSelectionListener(ListView.ID,
				(ISelectionListener) this);
	}

	public void setFocus() {
		banner.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//System.out.println("select change\n");
//		if(!getSite().getPage().isPartVisible(this))			//if i am not visible ,i will not change
//			return;
		if(!banner.isVisible())return;
		if (selection != null) {
			treeObj = (TreeModel.TreeObject) ((IStructuredSelection) selection)
					.getFirstElement();
			if (treeObj != null && !(treeObj instanceof TreeModel.TreeParent))
				if(treeObj.getModel()!= null)
				{
					//update content 
					Control[] children = left.getChildren();
					for (Control child : children)
						child.dispose();
					BasicModel bm=treeObj.getModel();
							
					//update screen shot thread's device 
					if(bm instanceof MobileModel)
					{
						System.out.println("change model start");
						if(mMobileModel!=null)
							mMobileModel.stopShot(this);
						
						mMobileModel=(MobileModel)bm;
						showInfo(left,mMobileModel);
						left.layout(true,true);	
						
						mMobileModel.startShot(this);
						System.out.println("change model stop");	
					}
				}
		}
	}
	
	//information layout
	public void showInfo(final Composite parent,final MobileModel mm) {

		Font boldFont = JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT);
		
		final IDevice device=mm.getDevice();

		Composite view = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		view.setLayout(layout);

		Label l = new Label(view, SWT.WRAP);
		l.setText("Name: ");
		l.setFont(boldFont);
		l = new Label(view, SWT.WRAP);
		l.setText(mm.getName());
		l = new Label(view, SWT.WRAP);
		l.setText("SerialNumber: ");
		l.setFont(boldFont);
		l = new Label(view, SWT.WRAP);
		l.setText(mm.getSerialNumber());

		l = new Label(view, SWT.WRAP);
		l.setText("Model: ");
		l.setFont(boldFont);
		l = new Label(view, SWT.WRAP);
		l.setText(mm.getModel());
		
		l = new Label(view, SWT.WRAP);
		l.setText("Manufacturer: ");
		l.setFont(boldFont);
		l = new Label(view, SWT.WRAP);
		l.setText(mm.getManufacturer());
		try {

			l = new Label(view, SWT.WRAP);
			l.setText("ApiLevel: ");
			l.setFont(boldFont);
			l = new Label(view, SWT.WRAP);
			l.setText(device
					.getPropertyCacheOrSync(IDevice.PROP_BUILD_API_LEVEL));

			l = new Label(view, SWT.WRAP);
			l.setText("AndroidBuidVersion: ");
			l.setFont(boldFont);
			l = new Label(view, SWT.WRAP);
			l.setText(device.getPropertyCacheOrSync(IDevice.PROP_BUILD_VERSION));

			l = new Label(view, SWT.WRAP);
			l.setText("CpuAbi: ");
			l.setFont(boldFont);
			l = new Label(view, SWT.WRAP);
			l.setText(device
					.getPropertyCacheOrSync(IDevice.PROP_DEVICE_CPU_ABI));
			
			l = new Label(view, SWT.WRAP);
			l.setText("isOnline: ");
			l.setFont(boldFont);
			l = new Label(view, SWT.WRAP);
			l.setText(device.isOnline()?"yes":"no");
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException | IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			System.out.println("adb get info wrong!");
		}

		Button getClientsButton = new Button(parent, SWT.PUSH);
		//Text infoText = new Text(parent, SWT.WRAP | SWT.MULTI);
		
		//get all clients button
		getClientsButton.setText("Clients");
		getClientsButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseUp(MouseEvent e) {
				Client[] clients = device.getClients();
				//Display cDisplay=new Display();
				Shell cShell=new Shell();
				cShell.setBounds(300, 100, 500, 500);
				cShell.setText("Clients");
				//cShell.setLayout(layout);
				TreeModel cTreeModel=new TreeModel();
				for (Client client : clients) {
					//System.out.println(client.getClientData().getClientDescription());
					ClientModel clientModel=new ClientModel(client);
					cTreeModel.addModel(clientModel);
				}
				TreeViewer viewer = new TreeViewer(cShell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				viewer.setContentProvider(cTreeModel.getVcProvider());
				viewer.setLabelProvider(cTreeModel.getVlProvider());
				viewer.setInput(cTreeModel.getRoot());
				cShell.open();
				cShell.setLayout(new FillLayout());
				cShell.layout();

			}

		});
		
		//reboot button
		Button rebootButton = new Button(parent, SWT.PUSH);
		rebootButton.setText("Reboot");
		rebootButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					device.reboot(mm.getSerialNumber());
				} catch (TimeoutException | AdbCommandRejectedException
						| IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
	
		//install apk dialog
		Button installApkButton = new Button(parent, SWT.PUSH);
		installApkButton.setText("Install Apk");
		installApkButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				 FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.TITLE | SWT.MULTI );
				  fileDialog.setText( "Upload Apk" );
				  fileDialog.open();
				  String[] fileNames = fileDialog.getFileNames();
				for (String fileName : fileNames) {
					System.out.println(fileNames[0]);

					try {
						String ir = device.installPackage(fileName, true);
						if (ir == null)
							System.out.println("install successfully!");
					} catch (InstallException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("install unsuccessfully..");
					}
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
		 
	}

	public void updateShotView(final ImageData id)
	{
		
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				//System.out.println("shot view");
				Image image=new Image(display,id);
				sstLabel.setImage(image);
				sstLabel.update();
			}
		});
	}
	
	public void dispose() {
		getSite().getPage().removeSelectionListener(ListView.ID,
				(ISelectionListener) this);
		
		pushSession.stop();
		super.dispose();
	}

}
