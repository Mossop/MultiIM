package com.blueprintit.multiim.gui;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.Group;
import com.blueprintit.multiim.IM;
import com.blueprintit.multiim.event.*;

public class BuddyListTreeModel implements TreeModel,IMListener,ServiceListener,GroupListener,BuddyListener
{
	private IM im;
	private Map lists;
  private List listeners = new ArrayList();
	private Comparator comparator;

	private final int INSERT = 0;
	private final int UPDATE = 1;
	private final int DELETE = 2;

	private abstract class BasicComparator implements Comparator
	{
		protected String getID(Object o)
		{
			if (o instanceof Buddy)
			{
				return ((Buddy)o).getID();
			}
			else if (o instanceof Group)
			{
				return ((Group)o).getID();
			}
			else if (o instanceof Service)
			{
				return ((Service)o).getID();
			}
			else
			{
				return "";
			}
		}

		protected String getName(Object o)
		{
			if (o instanceof Buddy)
			{
				return ((Buddy)o).getName();
			}
			else if (o instanceof Group)
			{
				return ((Group)o).getName();
			}
			else if (o instanceof Service)
			{
				return ((Service)o).getName();
			}
			else
			{
				return "";
			}
		}
	}

	private class AlphabeticIDComparator extends BasicComparator
	{
		public int compare(Object o1, Object o2)
		{
			return getID(o1).compareTo(getID(o2));
		}
	}

	private class AlphabeticNameComparator extends BasicComparator
	{
		public int compare(Object o1, Object o2)
		{
			return getName(o1).compareTo(getName(o2));
		}
	}

	public BuddyListTreeModel(IM im)
	{
		this.im=im;
		comparator = new AlphabeticNameComparator();
		lists = new HashMap();
		im.addIMListener(this);
		im.addServiceListener(this);
		im.addGroupListener(this);
		im.addBuddyListener(this);
		addIM(im);
	}

	private void addIM(IM im)
	{
		List list = new ArrayList();
		lists.put(im,list);
		Iterator loop = im.getServices();
		while (loop.hasNext())
		{
			addService((Service)loop.next());
		}
	}

	private void addService(Service s)
	{
		nodeAdded(s);
		Iterator loop = s.getGroups();
		while (loop.hasNext())
		{
			addGroup((Group)loop.next());
		}
	}

	private void addGroup(Group g)
	{
		nodeAdded(g);
		Iterator loop = g.getBuddies();
		while (loop.hasNext())
		{
			addBuddy((Buddy)loop.next());
		}
	}

	private void addBuddy(Buddy b)
	{
		nodeAdded(b);
	}

	private void nodeAdded(Object node)
	{
		if (!isLeaf(node))
		{
			List list = new ArrayList();
			lists.put(node,list);
		}
		Object parent = getParent(node);
		List list = (List)lists.get(parent);
		int[] indices = new int[] {addToList(list,node)};
		Object[] childs = new Object[] {node};
		deliverEvent(new TreeModelEvent(this,getPath(parent),indices,childs),INSERT);
	}

	private int addToList(List l, Object o)
	{
		int pos = Collections.binarySearch(l,o,comparator);
		if (pos>=0)
		{
			l.add(pos,o);
		}
		else
		{
			pos=-(pos+1);
			l.add(pos,o);
		}
		return pos;
	}

	private void changeComparator(Comparator c)
	{
		synchronized(lists)
		{
			Iterator loop = lists.values().iterator();
			while (loop.hasNext())
			{
				List list = ((List)loop.next());
				Collections.sort(list,c);
			}
			comparator=c;
		}
	}

	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}

	private void deliverEvent(TreeModelEvent e,int type)
	{
		Iterator loop;
		synchronized(listeners)
		{
			loop = (new ArrayList(listeners)).iterator();
		}
		TreeModelListener l;
		while (loop.hasNext())
		{
			l = (TreeModelListener)loop.next();
			if (type==INSERT)
			{
				l.treeNodesInserted(e);
			}
			else if (type==DELETE)
			{
				l.treeNodesRemoved(e);
			}
			else if (type==UPDATE)
			{
				l.treeNodesChanged(e);
			}
		}
	}

	public Object getRoot()
	{
		return im;
	}

	public Object getChild(Object parent, int pos)
	{
		synchronized(lists)
		{
			List list = (List)lists.get(parent);
			return list.get(pos);
		}
	}

	public int getChildCount(Object parent)
	{
		synchronized(lists)
		{
			List list = (List)lists.get(parent);
			return list.size();
		}
	}

	public int getIndexOfChild(Object parent, Object child)
	{
		synchronized(lists)
		{
			List list = (List)lists.get(parent);
			return list.indexOf(child);
		}
	}

	public boolean isLeaf(Object child)
	{
		return (child instanceof Buddy);
	}

	private TreePath getPath(Object parent)
	{
		if (parent==im)
		{
			return new TreePath(im);
		}
		else if (parent instanceof Service)
		{
			Service service = (Service)parent;
			Object[] path = new Object[2];
			path[0]=im;
			path[1]=service;
			return new TreePath(path);
		}
		else if (parent instanceof Group)
		{
			Group group = (Group)parent;
			Object[] path = new Object[3];
			path[0]=im;
			path[1]=group.getService();
			path[2]=group;
			return new TreePath(path);
		}
		else if (parent instanceof Buddy)
		{
			Buddy buddy = (Buddy)parent;
			Object[] path = new Object[4];
			path[0]=im;
			path[1]=buddy.getGroup().getService();
			path[2]=buddy.getGroup();
			path[3]=buddy;
			return new TreePath(path);
		}
		else
		{
			return null;
		}
	}

	private Object getParent(Object child)
	{
		if (child==im)
		{
			return null;
		}
		else if (child instanceof Service)
		{
			return im;
		}
		else if (child instanceof Group)
		{
			Group group = (Group)child;
			return group.getService();
		}
		else if (child instanceof Buddy)
		{
			Buddy buddy = (Buddy)child;
			return buddy.getGroup();
		}
		else
		{
			return null;
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue)
	{
	}

	private TreeModelEvent constructEvent(Object node)
	{
		Object parent = getParent(node);
		List list = (List)lists.get(parent);
		int[] indices = new int[1];
		Object[] childs = new Object[] {node};
		indices[0] = list.indexOf(node);
		return new TreeModelEvent(this,getPath(parent),indices,childs);
	}

	private void nodeChanged(Object node)
	{
		deliverEvent(constructEvent(node),UPDATE);
	}

	private void nodeRemoved(Object node)
	{
		Object parent = getParent(node);
		List list = (List)lists.get(parent);
		int[] indices = new int[] {list.indexOf(node)};
		Object[] childs = new Object[] {node};
		list.remove(node);
		lists.remove(node);
		deliverEvent(new TreeModelEvent(this,getPath(parent),indices,childs),DELETE);
	}

	public void serviceProviderAdded(IMEvent e)
	{
	}

	public void serviceProviderRemoved(IMEvent e)
	{
	}

	public void serviceConnected(ServiceEvent e)
	{
	}

	public void serviceDisconnected(ServiceEvent e)
	{
	}

	public void serviceAdded(IMEvent e)
	{
		addService(e.getService());
	}

	public void serviceRemoved(IMEvent e)
	{
		nodeRemoved(e.getService());
	}

	public void serviceStatusChanged(ServiceEvent e)
	{
		nodeChanged(e.getSource());
	}

	public void groupAdded(ServiceEvent e)
	{
		addGroup(e.getGroup());
	}

	public void groupRemoved(ServiceEvent e)
	{
		nodeRemoved(e.getGroup());
	}

	public void buddyAdded(GroupEvent e)
	{
		addBuddy(e.getBuddy());
	}

	public void buddyRemoved(GroupEvent e)
	{
		nodeRemoved(e.getBuddy());
	}

	public void buddyConnected(BuddyEvent e)
	{
	}

	public void buddyDisconnected(BuddyEvent e)
	{
	}

	public void buddyStatusChanged(BuddyEvent e)
	{
		nodeChanged(e.getSource());
	}
}
