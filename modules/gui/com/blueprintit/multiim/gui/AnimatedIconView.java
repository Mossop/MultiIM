package com.blueprintit.multiim.gui;

import javax.swing.text.IconView;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Graphics;

class AnimatedIconView extends IconView implements ImageObserver
{
	private Container container = null;
	private Rectangle bounds = null;

	public AnimatedIconView(Element e)
	{
		super(e);
		Icon icon = StyleConstants.getIcon(getAttributes());
		if (icon instanceof ImageIcon)
		{
			((ImageIcon)icon).setImageObserver(this);
		}
	}

	public void setParent(View parent)
	{
		super.setParent(parent);
		container=getContainer();
	}

	public void paint(Graphics g, Shape s)
	{
		super.paint(g,s);
		bounds=s.getBounds();
	}

	public boolean imageUpdate(Image img,int infoflags,int x,int y,int width,int height)
	{
		if (((infoflags & ImageObserver.FRAMEBITS)>0)&&(container!=null)&&(bounds!=null))
		{
			container.repaint((int)bounds.getX(),(int)bounds.getY(),(int)bounds.getWidth(),(int)bounds.getHeight());
		}
		return true;
	}
}
