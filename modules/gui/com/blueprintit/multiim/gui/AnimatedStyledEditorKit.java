package com.blueprintit.multiim.gui;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class AnimatedStyledEditorKit extends StyledEditorKit
{
	public ViewFactory getViewFactory()
	{
		return new AnimatedViewFactory(super.getViewFactory());
	}
}
