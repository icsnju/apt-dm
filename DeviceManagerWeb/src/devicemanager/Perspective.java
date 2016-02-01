package devicemanager;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import devicemanager.view.ConsoleView;
import devicemanager.view.ListView;
import devicemanager.view.MobileInfoView;
import devicemanager.view.LogcatView;
import devicemanager.view.MyProjectExplorer;
/*
 *A perspective factory generates the initial page layout and visible action set for a page. 
 */
public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "DeviceManagerWeb.Perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		//left part
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
		left.addView(ListView.ID);
		left.addView(MyProjectExplorer.ID);
		
		//bottom  
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView(LogcatView.ID);
		bottom.addView(ConsoleView.ID);
		
		//right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.70f, editorArea);
		right.addView(MobileInfoView.ID);
		
		//shortcut
		layout.addShowViewShortcut(ListView.ID);
		layout.addShowViewShortcut(MyProjectExplorer.ID);
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
