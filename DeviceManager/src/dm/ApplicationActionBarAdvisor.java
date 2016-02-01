package dm;

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import dm.editor.DocEditor;
import dm.editor.DocEditorInput;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private Action aboutAction;

	private IWorkbenchAction importAction;
	private IWorkbenchAction exportAction;
	private IWorkbenchAction newAction;

	private IWorkbenchAction redoAction;
	private IWorkbenchAction undoAction;

	public IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;
	private Action newEditorAction;

	private MenuManager showViewMenuMgr;

	// private IWorkbenchAction newWindowAction;
	// private OpenViewAction openViewAction;
	// private Action messagePopupAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);

		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);

		newAction = ActionFactory.NEW.create(window);
		register(newAction);

		saveAction = ActionFactory.SAVE.create(window);
		register(saveAction);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		register(saveAllAction);

		redoAction = ActionFactory.REDO.create(window);
		register(redoAction);
		undoAction = ActionFactory.UNDO.create(window);
		register(undoAction);

		aboutAction = new Action() {
			public void run() {
				Shell shell = window.getShell();
				Bundle bundle = Platform.getBundle(PlatformUI.PLUGIN_ID);
				Dictionary headers = bundle.getHeaders();
				Object version = headers.get(Constants.BUNDLE_VERSION);
				MessageDialog.openInformation(shell, "Device Manager",
						"version " + version+"\n by TC \n tianchi_liu@yeah.net");
			}
		};
		aboutAction.setText("About");
		aboutAction.setId("devicemanager.about");
		// aboutAction.setImageDescriptor(helpActionImage);
		register(aboutAction);

		newEditorAction = new Action() {
			public void run() {
				try {
					window.getActivePage()
							.openEditor(
									new DocEditorInput(
											ApplicationActionBarAdvisor.this),
									DocEditor.ID, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		};
		newEditorAction.setText("Open new editor");
		newEditorAction.setId("org.eclipse.rap.demo.neweditor");
		newEditorAction.setImageDescriptor(window.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		register(newEditorAction);

		showViewMenuMgr = new MenuManager("Show View", "showView");
		IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST
				.create(window);
		showViewMenuMgr.add(showViewMenu);
		// newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
		// register(newWindowAction);

		// openViewAction = new OpenViewAction(window, "New View",
		// MobileInfoView.ID);
		// register(openViewAction);s

		// messagePopupAction = new MessagePopupAction("Open Message", window);
		// register(messagePopupAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {

		// Add a group marker indicating where action set menus will appear.
		MenuManager fileMenu = new MenuManager("File",
				IWorkbenchActionConstants.M_FILE);
		MenuManager windowMenu = new MenuManager("Window",
				IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("Help",
				IWorkbenchActionConstants.M_HELP);
		MenuManager editMenu = new MenuManager("Edit",
				IWorkbenchActionConstants.M_EDIT);

		fileMenu.add(newAction);
		fileMenu.add(importAction);
		fileMenu.add(exportAction);
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		windowMenu.add(showViewMenuMgr);
		menuBar.add(windowMenu);

		editMenu.add(redoAction);
		editMenu.add(undoAction);		
		editMenu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
		menuBar.add(editMenu);
		
		helpMenu.add(new Separator("about"));
		helpMenu.add(aboutAction);
		menuBar.add(helpMenu);
		// menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		// menuBar.add(helpMenu);
		//
		// // File
		// fileMenu.add(newWindowAction);
		// fileMenu.add(new Separator());
		// // fileMenu.add(messagePopupAction);
		// fileMenu.add(openViewAction);
		// fileMenu.add(new Separator());
		// fileMenu.add(exitAction);
		//
		// // Help
		// //helpMenu.add(aboutAction);
	}

	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main"));
		toolbar.add(aboutAction);
		toolbar.add(exitAction);
		toolbar.add(newEditorAction);
		toolbar.add(saveAction);
		toolbar.add(saveAllAction);
		// toolbar.add(messagePopupAction);
	}
}
