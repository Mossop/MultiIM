package com.blueprintit.multiim.gui;

import javax.swing.text.ViewFactory;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;

class AnimatedViewFactory implements ViewFactory
{
	ViewFactory parent;

	public AnimatedViewFactory(ViewFactory parent)
	{
		this.parent=parent;
	}

	public View create(Element e)
	{
		if (e.getName()==StyleConstants.IconElementName)
		{
			return new AnimatedIconView(e);
		}
		else
		{
			return parent.create(e);
		}
	}
}
