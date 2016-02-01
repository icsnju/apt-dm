package devicemanager.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import devicemanager.Activator;
import devicemanager.mobile.model.MobileModel;
import devicemanager.view.ListView;

public class TreeModel {
	public static final String PHONE_SMALL_PIC = "/icons/phone_21.gif";
	private TreeParent root = new TreeParent("all:");
	private ViewContentProvider vcProvider = new ViewContentProvider();
	private ViewLabelProvider vlProvider = new ViewLabelProvider();
	private ListView view=null;

	public class TreeObject {
		private String name;
		private TreeParent parent;
		private BasicModel bsModel=null;

		public TreeObject(BasicModel m) {
			setModel(m);
			if (m != null)
				name = m.getName();
		}

		public TreeObject(String n) {
			name = n;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public BasicModel getModel() {
			return bsModel;
		}

		public void setModel(BasicModel model) {
			this.bsModel = model;
		}
	}

	public class TreeParent extends TreeObject {
		private ArrayList children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children
					.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return ((TreeObject) obj).getName();
		}

		public Image getImage(Object obj) {
			String imageKey = PHONE_SMALL_PIC;
			if (obj instanceof TreeParent) {
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(imageKey);
			} else
				return createImage(imageKey);
		}

	}

	// create image
	protected static Image createImage(String name) {
		InputStream stream = Activator.class.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
		}
		return image;
	}

	public TreeParent getRoot() {
		return root;
	}

	public void setRoot(TreeParent root) {
		this.root = root;
	}

	public ViewContentProvider getVcProvider() {
		return vcProvider;
	}

	public ViewLabelProvider getVlProvider() {
		return vlProvider;
	}

	//when new device in
	public void addModel(BasicModel dm) {
		TreeObject tobj = new TreeObject(dm);
		if(dm instanceof MobileModel)
		{
			MobileModel mm=(MobileModel)dm;
			TreeObject[] tos=root.getChildren();
			TreeParent tp=null;
			String manuName=mm.getManufacturer();

			for(TreeObject to:tos)
			{
				if(to.getName().equals(manuName))
				{
					tp=(TreeParent)to;
					tp.addChild(tobj);
					break;
				}
			}
			if(tp==null)
			{
				tp=new TreeParent(manuName);
				tp.addChild(tobj);
				root.addChild(tp);
			}	
		}
		else 
		{
			root.addChild(tobj);
		}
		if(view!=null)
			view.updateView();
	}
	
	public void removeModel(String id)
	{
		TreeObject[] pList=root.getChildren();
		for(TreeObject p:pList)
		{
			TreeParent tParent=(TreeParent)p;
			TreeObject[] children=tParent.getChildren();
			for(TreeObject child:children)
			{
				if(child.getModel().getID().equals(id))
				{
					tParent.removeChild(child);
					if(!tParent.hasChildren())
					{
						root.removeChild(tParent);
					}
					if(view!=null)view.updateView();
					return;
				}
			}
		}
	}

	public ListView getView() {
		return view;
	}

	public void setView(ListView view) {
		this.view = view;
	}

}
