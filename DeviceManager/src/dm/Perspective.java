package dm;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import dm.view.ConsoleView;
import dm.view.ListView;
import dm.view.LogcatView;
import dm.view.MobileInfoView;
/*
 *A perspective factory generates the initial page layout and visible action set for a page. 
 */
public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "DeviceManager.Perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		//left part
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
		left.addView("DeviceManagerWeb.View.ProjectExplorer");
		left.addView(ListView.ID);
		
		//bottom  
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView(LogcatView.ID);
		bottom.addView(ConsoleView.ID);
		
		//right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.70f, editorArea);
		right.addView(MobileInfoView.ID);
		
		//shortcut
		layout.addShowViewShortcut(ListView.ID);
		layout.addShowViewShortcut("DeviceManagerWeb.View.ProjectExplorer");
		layout.addShowViewShortcut(MobileInfoView.ID);
		layout.addShowViewShortcut(LogcatView.ID);
		layout.addShowViewShortcut(ConsoleView.ID);

//		folder.addPlaceholder(MobileInfoView.ID + ":0");
//		folder.addView(MobileInfoView.ID);
//		
//		layout.addStandaloneView(LogcatView.ID,  true,IPageLayout.BOTTOM, 0.75f, MobileInfoView.ID);
//		layout.getViewLayout(LogcatView.ID).setCloseable(false);
//		
//		layout.addStandaloneView(ListView.ID,  false, IPageLayout.LEFT, 0.75f, editorArea);
//		layout.getViewLayout(ListView.ID).setCloseable(false);
	}
}
