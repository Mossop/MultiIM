package com.blueprintit.multiim.gui;

import javax.swing.tree.TreeCellRenderer;
import java.awt.image.ImageObserver;
import java.awt.Component;
import java.awt.Image;
import java.awt.Dimension;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Color;
import com.blueprintit.multiim.Service;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.Group;
import com.blueprintit.multiim.IM;

public class BuddyListTreeRenderer implements TreeCellRenderer
{
	private IM im;
	private JTree tree;

	public BuddyListTreeRenderer(IM im, JTree tree)
	{
		this.tree=tree;
		this.im=im;
	}

	public Component getTreeCellRendererComponent(final JTree tree, Object value, boolean selected,
		 boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Color background;
		Color text;
		Icon icon = null;
		if (selected)
		{
			background=UIManager.getColor("Tree.selectionBackground");
			text=UIManager.getColor("Tree.selectionForeground");
		}
		else
		{
			background=UIManager.getColor("tree.textBackground");
			text=UIManager.getColor("Tree.textForeground");
		}
		String title = null;
		String status = null;
		if (value==im)
		{
			title="Services";
		}
		else if (value instanceof Service)
		{
			Service service = (Service)value;
			title=service.getName();
			status=service.getStatusMessage();
			icon=service.getIcon();
		}
		else if (value instanceof Group)
		{
			Group group = (Group)value;
			title=group.getName();
			icon=group.getIcon();
		}
		else if (value instanceof Buddy)
		{
			Buddy buddy = (Buddy)value;
			title=buddy.getName();
			status=buddy.getStatusMessage();
			icon=buddy.getIcon();
		}
		else
		{
			title=value.toString();
		}
		JPanel mainpanel = new JPanel();
		mainpanel.setBackground(background);
		JPanel textpanel;
		if (icon==null)
		{
			if (leaf)
			{
				icon = UIManager.getIcon("Tree.leafIcon");
			}
			else if (expanded)
			{
				icon = UIManager.getIcon("Tree.openIcon");
			}
			else
			{
				icon = UIManager.getIcon("Tree.closedIcon");
			}
		}
		JLabel image = new JLabel(icon);
		mainpanel.add(image);
		textpanel = new JPanel();
		mainpanel.add(textpanel);
		textpanel.setBackground(background);
		if (title!=null)
		{
			JLabel textlabel = new JLabel(title);
			textlabel.setForeground(text);
			textpanel.add(textlabel);
		}
		if (status!=null)
		{
			JLabel textlabel = new JLabel(status);
			textlabel.setForeground(UIManager.getColor("TextField.inactiveForeground"));
			textpanel.add(textlabel);
		}
		textpanel.setLayout(new BoxLayout(textpanel,BoxLayout.Y_AXIS));
		if (icon instanceof ImageIcon)
		{
			((ImageIcon)icon).setImageObserver(new ImageObserver() {
				public boolean imageUpdate(Image img,int infoflags,int x,int y,int width,int height)
				{
					if ((infoflags & ImageObserver.FRAMEBITS)>0)
					{
						tree.repaint();
					}
					return true;
				}
			});
		}
		return mainpanel;
	}
}
