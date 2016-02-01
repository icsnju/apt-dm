package dm.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.android.ddmlib.Client;

import dm.mobile.model.ClientModel;
import dm.mobile.model.MobileModel;
import dm.model.BasicModel;
import dm.model.TreeModel;

public class MobileInfoView extends ViewPart implements ISelectionListener  {					

	public static final String ID = "DeviceManagerWeb.View.MobileInfoView";
	public static final int NAME_NUM=9;
	/**
	 * The text control that's displaying the content of the email message.
	 */
	private Display display;
	private Composite parent;
	private ScrolledComposite primary;
	private Composite child;
	private Composite left;
	private Composite right;
	private Label sstLabel;
	private Table table;
	private TableItem[] items;
	private TableColumn[] columns;
	
	private TreeModel.TreeObject treeObj;      //the information of this treeObj
	private MobileModel mMobileModel;

	private int preX=-1,preY=-1;
	
	Button getClientsButton;
	Button rebootButton;
	Button installApkButton;
	Button recordButton;
	
	private boolean recordStart=false;
	
	//IShellOutputReceiver outputReceiver ;
    //private File xmlDumpFile = null; 

	public MobileInfoView(){
		super();
	}
	public void createPartControl(Composite parent) {

		// top banner	
		this.parent=parent;
		display=Display.getDefault();
		treeObj=null;
		
		
		//set the view  
		RowLayout gLayout = new RowLayout(SWT.VERTICAL);
	    gLayout.wrap = true;
	    //main view
		primary = new ScrolledComposite(parent,SWT.H_SCROLL | SWT.V_SCROLL);

		//sub main view
	    child = new Composite(primary, SWT.NONE);
	    child.setLayout(new FillLayout(SWT.HORIZONTAL));
	    
	    //left info view
		left=new Composite(child, SWT.BORDER);
		left.setLayout(gLayout);
		
		//left table init
		table = new Table(left, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		table.setHeaderVisible(true);
		String[] titles = { "名称", "参数"};
		columns=new TableColumn[2];
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			columns[loopIndex] = new TableColumn(table,SWT.NONE);
			columns[loopIndex].setText(titles[loopIndex]);
		}

		items=new TableItem[NAME_NUM];
		for (int loopIndex = 0; loopIndex < NAME_NUM; loopIndex++) {
			items[loopIndex] = new TableItem(table, SWT.NULL);
		}
		items[0].setText(0,"Name");
		items[1].setText(0,"SerialNumber");
		items[2].setText(0,"Model");
		items[3].setText(0,"Manufacturer");
		items[4].setText(0,"ApiLevel");
		items[5].setText(0,"AndroidBuildVersion");
		items[6].setText(0,"CpuAbi");
		items[7].setText(0,"IsOnline");
		items[8].setText(0,"DPI");
		
		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
		table.setBounds(25, 25, 500, 200);
		
		//left Buttons
		getClientsButton = new Button(left, SWT.PUSH);
		//Text infoText = new Text(parent, SWT.WRAP | SWT.MULTI);
		//get all clients button
		getClientsButton.setText("Clients");
		getClientsButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
				Client[] clients = mMobileModel.getDeviceClients();
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
		rebootButton = new Button(left, SWT.PUSH);
		rebootButton.setText("Reboot");
		rebootButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
				mMobileModel.rebootDevice();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
	
		
		//install apk dialog
		installApkButton = new Button(left, SWT.PUSH);
		installApkButton.setText("Install Apk");
		installApkButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {

				FileDialog fileDialog = new FileDialog(parent.getShell(),
						SWT.TITLE | SWT.MULTI);
				fileDialog.setText("Upload Apk");
				fileDialog.open();
				String[] fileNames = fileDialog.getFileNames();

				for (String fileName : fileNames) {
					System.out.println(fileNames[0]);
					if(mMobileModel==null)return;
					mMobileModel.installPackage(fileName);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
		 		
		
		//record button
		recordButton = new Button(left, SWT.PUSH);
		recordButton.setText("Record");
		recordButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
			
				if(recordStart){
					mMobileModel.stopScriptTranslate();
					recordButton.setText("Record");
					recordStart=false;
				}
				else{		//start record 
					recordStart=true;
					recordButton.setText("Stop");
					mMobileModel.startScriptRecord();
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
		
		//right part:screen shot
		right=new Composite(child, SWT.BORDER);
		right.setLayout(gLayout);
		sstLabel=new Label(right,SWT.BORDER);
		sstLabel.addMouseListener(new MouseListener() {
		
	        @Override
	        public void mouseUp(MouseEvent event) {		
	        	if(preX==-1)return;
	        	if(mMobileModel==null){
		            preX=-1;
		            preY=-1;
	        		return;
	        	}
	        	
	        	float zoom=mMobileModel.getZoom();
	        	int x=event.x;
	        	int y=event.y;
				x = (int) (((float) x) / zoom);
				y = (int) (((float) y) / zoom);
				preX = (int) (((float) preX) / zoom);
				preY = (int) (((float) preY) / zoom);
	        	//System.out.println("up: "+"x: "+x+" y:"+y);
	        	int dx=x-preX;
	        	int dy=y-preY;
				if ((Math.abs(dx) < 30) && (Math.abs(dy) < 30)) {
					// click event
					mMobileModel.executeAsyncCommand("input tap "+x+" "+y+"");
				}
				else{
					// drag event
					mMobileModel.executeAsyncCommand("input swipe "+preX+" "+preY+" "+x+" "+y+"");
				}
	      	       
	            preX=-1;
	            preY=-1;
	        }

	        @Override
	        public void mouseDown(MouseEvent event) {
	        	preX=event.x;
	        	preY=event.y;
	        	//System.out.println("down:"+"x: "+preX+" y:"+preY);
	        }

	        @Override
	        public void mouseDoubleClick(MouseEvent event) {
	        }
	    });
	
		//back button
		Button backButton = new Button(right, SWT.PUSH);
		backButton.setText("Back");
		backButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
				mMobileModel.executeAsyncCommand("input keyevent 4");
			}
			
			@Override
			public void mouseDown(MouseEvent e) {				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			
			}
		});
		
		// home button
		Button homeButton = new Button(right, SWT.PUSH);
		homeButton.setText("Home");
		homeButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
				mMobileModel.executeAsyncCommand("input keyevent 3");
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});
		
		//menu button
		Button menuButton = new Button(right, SWT.PUSH);
		menuButton.setText("Menu");
		menuButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(mMobileModel==null)return;
				mMobileModel.executeAsyncCommand("input keyevent 1");
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});
		
		//main view is scrolled
		primary.setContent(child);
		primary.setExpandHorizontal(true);
		primary.setExpandVertical(true);
		primary.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = primary.getClientArea();
				primary.setMinSize(parent.computeSize(r.width, 800));
			}
		});
			
		// register listener
		getSite().getPage().addSelectionListener(ListView.ID,
				(ISelectionListener) this);
	}

	public void setFocus() {
		primary.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//System.out.println("select change\n");

		if(!primary.isVisible())return;
		if (selection != null) {
			treeObj = (TreeModel.TreeObject) ((IStructuredSelection) selection)
					.getFirstElement();
			if (treeObj != null && !(treeObj instanceof TreeModel.TreeParent))
				if(treeObj.getModel()!= null)
				{
					//update content 
//					Control[] children = left.getChildren();
//					for (Control child : children)
//						child.dispose();
					BasicModel bm=treeObj.getModel();
							
					//update screen shot thread's device 
					if(bm instanceof MobileModel)
					{
						System.out.println("change model start");
						MobileModel tmm=(MobileModel)bm;
						if(tmm==mMobileModel)return;
						if(mMobileModel!=null)
							mMobileModel.stopShot(this);
						
						mMobileModel=tmm;
						//IDevice device=mMobileModel.getDevice();
						//该方法已经由com.android.monkeyrunner 分裂出一个 com.android.chimpchat
						//mDevice = new AdbChimpDevice(device);		//这是一个阻塞过程，会导致界面卡主
		
						showInfo(left);
						left.layout(true,true);							
						mMobileModel.startShot(this);
					}
				}
		}
	}
	
	//information layout
	public void showInfo(final Composite parent) {
		if(mMobileModel==null)return;
		items[0].setText(1,mMobileModel.getName());

		items[1].setText(1,mMobileModel.getSerialNumber());

		items[2].setText(1,mMobileModel.getModel());
		
		items[3].setText(1,mMobileModel.getManufacturer());
		
		items[4].setText(1,mMobileModel.getApi());

		items[5].setText(1,mMobileModel.getBuildVersion());

		items[6].setText(1,mMobileModel.getCpuAbi());
			
		items[7].setText(1,mMobileModel.getIsOnline());
		
		items[8].setText(1,mMobileModel.getImageW()+" * "+mMobileModel.getImageH());
		for (int loopIndex = 0; loopIndex < 2; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
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
				if(sstLabel.isDisposed())return;
				sstLabel.setImage(image);
				sstLabel.update();
				right.layout(true,true);	
			}
		});
	}
	
	public void dispose() {
		getSite().getPage().removeSelectionListener(ListView.ID,
				(ISelectionListener) this);
		if(mMobileModel!=null)
			mMobileModel.stopShot(this);
		super.dispose();
	}

}
