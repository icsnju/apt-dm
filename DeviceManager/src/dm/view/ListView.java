package dm.view;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import dm.Activator;
import dm.action.InstallAppAction;
import dm.model.TreeModel;


public class ListView extends ViewPart {
	public static final String ID = "DeviceManagerWeb.View.ListView";

	private TreeViewer viewer;
	private Display display;
	
	private MenuManager fMenuMgr; 
	private Menu fMenu;
	private Composite parent;
	
    /**
     * We will set up a dummy model to initialize tree hierarchy. In real
     * code, you will connect to a real model and expose its hierarchy.
     */
//    private TreeObject createDummyModel() {
//        TreeObject to1 = new TreeObject("Galaxy S5 G9008");
//        TreeObject to2 = new TreeObject("Galaxy Note3 N9009");
//        TreeObject to3 = new TreeObject("Galaxy S4 I9508");
//        TreeParent p1 = new TreeParent("SAMSUNG");
//        p1.addChild(to1);
//        p1.addChild(to2);
//        p1.addChild(to3);
//
//        TreeObject to4 = new TreeObject("Glory3X pro");
//        TreeParent p2 = new TreeParent("Huawei");
//        p2.addChild(to4);
//
//        TreeParent root = new TreeParent("");
//        root.addChild(p1);
//        root.addChild(p2);
//        return root;
//    }

	/**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
	public void createPartControl(Composite parent) {
		this.parent=parent;

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(Activator.treeModel.getVcProvider());
		viewer.setLabelProvider(Activator.treeModel.getVlProvider());
		viewer.setInput(Activator.treeModel.getRoot());
		Activator.treeModel.setView(this);

		getSite().setSelectionProvider(viewer);
		display=Display.getDefault();
		
		hookContextMenu();
	}
	
	
	//contextMenu
	private void hookContextMenu() { 
        fMenuMgr = new MenuManager("#PopupMenu"); 
        fMenuMgr.setRemoveAllWhenShown(true); 
        
        fMenuMgr.addMenuListener(new IMenuListener() { 
            public void menuAboutToShow(IMenuManager manager) {  
				if (viewer.getSelection().isEmpty()) {
					return;
				}
				
				TreeModel.TreeObject treeObj = (TreeModel.TreeObject) ((IStructuredSelection) viewer.getSelection())
    					.getFirstElement();
				fillContextMenu(manager,treeObj);
			}
        }); 
        fMenu = fMenuMgr.createContextMenu(viewer.getControl()); 
      
        viewer.getControl().setMenu(fMenu); 
        getSite().registerContextMenu(fMenuMgr, viewer);              
    }    
	
	protected void fillContextMenu(IMenuManager manager,TreeModel.TreeObject to){
		manager.add(new InstallAppAction(parent,to));
	}
	
	
	public void updateView() {
		while (display == null || display.isDisposed()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				viewer.refresh();
				System.out.println("Update OK");
			}
		});

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//viewer.getControl().setFocus();
		 viewer.getTree().setFocus(); 
	}
	
	public void dispose() {
		super.dispose();
	}
}