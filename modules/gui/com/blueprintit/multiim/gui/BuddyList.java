package com.blueprintit.multiim.gui;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.blueprintit.ModularApplication;
import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.Group;
import com.blueprintit.Module;
import com.blueprintit.ModuleInfo;
import com.blueprintit.multiim.IM;
import com.blueprintit.multiim.event.*;

public class BuddyList extends JFrame implements Module
{
	private IM im;
	private JTree tree;
	private Map conversations;

	public BuddyList()
	{
		super("MultiIM");
	}

	public void initialise()
	{
		im = (IM)ModularApplication.getApplication().getModule(ModularApplication.getApplication().getModuleInfo("api"));
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
		}
		conversations = new HashMap();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initialiseDisplay();
		show();
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e)
			{
				ModularApplication.getApplication().exit();
			}
		});
		im.addMessageListener(new MessageAdapter() {
			public void messageReceived(MessageEvent e)
			{
				Conversation c = (Conversation)conversations.get(e.getSource().getID());
				if (c==null)
				{
					c = new Conversation(e.getSource());
					conversations.put(e.getSource().getID(),c);
					c.messageReceived(e);
				}
			}
		});
	}

	private void initialiseMenu()
	{
		JMenuBar menubar = new JMenuBar();
		JMenu multiim = new JMenu("MultiIM");
		JMenuItem add = new JMenuItem("Add Account");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				AccountAdd adder = new AccountAdd(im);
				if (adder.isOK())
				{
					im.addService(adder.getSelectedProvider(),adder.getUsername(),adder.getPassword());
				}
			}
		});
		multiim.add(add);
		menubar.add(multiim);

		setJMenuBar(menubar);
	}

	private void initialiseDisplay()
	{
		setSize(300,400);
		initialiseMenu();
		tree =  new JTree(new BuddyListTreeModel(im));
		tree.setCellRenderer(new BuddyListTreeRenderer(im,tree));
		tree.setRowHeight(0);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent e)
    	{
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath!=null)
        {
	        Object item = selPath.getLastPathComponent();
	        if ((e.getClickCount()==1)&&(e.getButton()==e.BUTTON3))
	        {
	        	treeRightClicked(item, e);
	        }
	        else if (e.getClickCount()==2)
	        {
	        	treeDoubleClicked(item,e);
	        }
	      }
			}
		});
		JScrollPane scroller = new JScrollPane(tree);
		getContentPane().add(scroller);
	}

	public void treeDoubleClicked(Object item, MouseEvent e)
	{
		if (item instanceof Buddy)
		{
			final Buddy buddy = (Buddy)item;
			if (buddy.getGroup().getService().isConnected())
			{
				Conversation c = (Conversation)conversations.get(buddy.getID());
				if (c==null)
				{
					c = new Conversation(buddy);
					conversations.put(buddy.getID(),c);
				}
				else
				{
					c.setBuddy(buddy);
					c.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e)
						{
							conversations.remove(buddy);
						}
					});
					c.show();
				}
			}
		}
	}

	public void destroy()
	{
	}

	private void buildServiceMenu(final Service service, JPopupMenu popup)
	{
		if (service.isConnected())
		{
			JMenuItem disc = new JMenuItem("Disconnect");
			disc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					service.disconnect();
				}
			});
			popup.add(disc);
			popup.addSeparator();
			JMenu awaylist = new JMenu("Change Status");
			for (int loop=0; loop<=service.getMaxStatus(); loop++)
			{
				final int status = loop;
				JMenuItem change = new JMenuItem(service.getStatusName(loop));
				if (service.isCustomStatus(loop))
				{
					change.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							service.setAway(status,JOptionPane.showInputDialog(null,"Enter a custom status message:"));
						}
					});
				}
				else
				{
					change.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							service.setAway(status,"");
						}
					});
				}
				awaylist.add(change);
			}
			popup.add(awaylist);
		}
		else
		{
			JMenuItem disc = new JMenuItem("Connect");
			disc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					service.connect();
				}
			});
			popup.add(disc);
		}
	}

	private void buildGroupMenu(final Group group, JPopupMenu popup)
	{
		if (group.getService().isConnected())
		{
			JMenuItem addbuddy = new JMenuItem("Add a new Buddy");
			addbuddy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					group.addBuddy(JOptionPane.showInputDialog(null,"Enter the buddy's "+group.getService().getServiceProvider().getUsernameText()));
				}
			});
			popup.add(addbuddy);
		}
	}

	private void buildBuddyMenu(final Buddy buddy, JPopupMenu popup)
	{
	}

	public void treeRightClicked(Object item, MouseEvent e)
	{
		JPopupMenu popup = new JPopupMenu();
		if (item instanceof Service)
		{
			Service service = (Service)item;
			buildServiceMenu(service,popup);
		}
		else if (item instanceof Group)
		{
			Group group = (Group)item;
			buildGroupMenu(group,popup);
		}
		else if (item instanceof Buddy)
		{
			Buddy buddy = (Buddy)item;
			buildBuddyMenu(buddy,popup);
		}
		if (popup.getComponentCount()>0)
		{
			popup.show(tree,e.getX(),e.getY());
		}
	}
}
