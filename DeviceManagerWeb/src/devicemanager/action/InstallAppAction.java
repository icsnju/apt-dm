package devicemanager.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import devicemanager.mobile.model.MobileModel;
import devicemanager.model.BasicModel;
import devicemanager.model.TreeModel;
import devicemanager.model.TreeModel.TreeObject;
import devicemanager.model.TreeModel.TreeParent;

public class InstallAppAction extends Action{

	/**
	 *  batch install-apk-actions
	 */
	private static final long serialVersionUID = 1L;
	private static final String APK_NAME_PATH="/Users/Tianchi/code/eclipse_workspace/workspace/phonetestsystem/DeviceManagerWeb/apkbat/apk.txt";
	private static final String DEVICES_PATH="/Users/Tianchi/code/eclipse_workspace/workspace/phonetestsystem/DeviceManagerWeb/apkbat/devices.txt";
	private static final String APK_PATH="/Users/Tianchi/code/eclipse_workspace/workspace/phonetestsystem/DeviceManagerWeb/apkbat/apk/";
	private static final String COM_PATH="/Users/Tianchi/code/eclipse_workspace/workspace/phonetestsystem/DeviceManagerWeb/apkbat/apk/componentName.txt";
	
	Composite parent;
	TreeModel.TreeObject treeParent;
	public InstallAppAction(Composite p,TreeModel.TreeObject tp) {
		parent=p;
		treeParent=tp;
		setText("install App");
	}
	
	public void run() {
		System.out.println("install app action");
		FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.TITLE | SWT.MULTI );
		fileDialog.setText( "Upload Apk" );
		fileDialog.open();
		
		//write apk name
		try {
			FileWriter ApkWriter=new FileWriter(APK_NAME_PATH);
			String[] fileNames = fileDialog.getFileNames();
			for (String fileName : fileNames) {
				System.out.println(fileName);
				String[] subNames=fileName.split("/");
				String name=subNames[subNames.length-1];
				System.out.println(name);
				ApkWriter.write(name+'\n');
				
				//move apk file
				File src=new File(fileName);
				File dir=new File(APK_PATH+name);
				if(!dir.exists())
				{
					dir.createNewFile();
				}

				nioTransferCopy(src,dir);
			}
			ApkWriter.close();
		} catch (IOException e) {
			System.out.println("APK_NAME file open error");
			//e.printStackTrace();
		}
		
		//write devices id
		try {
			FileWriter devicesWriter=new FileWriter(DEVICES_PATH);
			traceTree(treeParent,devicesWriter);
			devicesWriter.close();
		} catch (IOException e) {
			System.out.println("devices id file open error");
			//e.printStackTrace();
		}
		
		//write componentName
		try {
			FileWriter comWriter = new FileWriter(COM_PATH);
			InputDialog dlg = new InputDialog(parent.getShell(), "Component",
					"Enter the component name", "",
					new NameValidator());
			while (dlg.open() == Window.OK) {
				System.out.println(dlg.getValue());
				comWriter.write(dlg.getValue()+"\n");
			}
			comWriter.close();
		} catch (IOException e1) {
			System.out.println("com open error");
			//e1.printStackTrace();
		}
		
		//run monkeyrunner
		try {
			Runtime.getRuntime().exec("/Users/Tianchi/Tool/adt-bundle-mac-x86_64-20140702/sdk/tools/monkeyrunner /Users/Tianchi/code/eclipse_workspace/workspace/phonetestsystem/DeviceManagerWeb/apkbat/work_thread.py");
		} catch (IOException e) {
			System.out.println("run monkeyrunner error");
			//e.printStackTrace();
		}
		
	}

	//find all devices
	private void traceTree(TreeObject tp, FileWriter fw) throws IOException {
		if(tp instanceof TreeParent)
		{
			TreeObject[] children=((TreeParent) tp).getChildren();
			for(TreeObject child: children)
				traceTree(child, fw);
		}
		else {
			BasicModel bm=tp.getModel();
			if(bm instanceof MobileModel)
			{
				MobileModel mm=(MobileModel)bm;
				fw.write(mm.getSerialNumber()+"\n");
			}
		}
	}
	
	//copy apk file
    private static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
				inStream.close();
				in.close();
				outStream.close();
				out.close();
			} catch (IOException e) {
				System.out.println("close stream error");
				//e.printStackTrace();
			}
    
        }
    }
    
    class NameValidator implements IInputValidator {
    	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
    	   * Validates the String. Returns null for no error, or an error message
    	   * 
    	   * @param newText the String to validate
    	   * @return String
    	   */
    	  public String isValid(String newText) {
    	  
    	    // Input must be OK
    	    return null;
    	  }
    	}
}
