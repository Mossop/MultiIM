package com.blueprintit.multiim.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;
import javax.swing.text.Position;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import com.blueprintit.multiim.Buddy;
import com.blueprintit.multiim.event.MessageEvent;
import com.blueprintit.multiim.event.MessageListener;
import javax.sound.sampled.*;

public class Conversation extends JFrame implements MessageListener
{
	private Buddy buddy;
	private MutableAttributeSet defaultattrs;
	private StyledDocument messagelog;
	private JTextPane messagebox;
	private JEditorPane typing;
	private JLabel statusbar;
	private StringBuffer fullbody;

	public Conversation(Buddy buddy)
	{
		super("Conversation with "+buddy.getName());
		defaultattrs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(defaultattrs,"Arial");
		StyleConstants.setFontSize(defaultattrs,13);
		fullbody=new StringBuffer();
		this.buddy=buddy;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initialiseDisplay();
		show();
		buddy.addMessageListener(this);
	}

	public void setBuddy(Buddy b)
	{
		buddy=b;
	}

	private void sendMessage()
	{
		try
		{
			MutableAttributeSet namestyle = new SimpleAttributeSet(defaultattrs);
			StyleConstants.setBold(namestyle,true);
			messagelog.insertString(messagelog.getLength(),buddy.getGroup().getService().getName()+": ",namestyle);
			MutableAttributeSet messagestyle = new SimpleAttributeSet(defaultattrs);
			buddy.getGroup().getService().documentInsert(messagelog,messagebox,messagelog.createPosition(messagelog.getLength()),typing.getText()+"\n",messagestyle);
			typing.requestFocusInWindow();
		}
		catch (Exception ex)
		{
		}
		buddy.sendMessage(typing.getText());
		typing.setText("");
	}

	private void initialiseDisplay()
	{
		setSize(600,400);
		getContentPane().setLayout(new BorderLayout(2,2));

		typing = new JEditorPane() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				super.paintComponent(g2);
			}
		};
		typing.setEditorKit(new StyledEditorKit());
		Keymap keymap = typing.addKeymap(null,typing.getKeymap());
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		KeyStroke altenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,InputEvent.ALT_MASK);
		keymap.removeKeyStrokeBinding(enter);
		keymap.addActionForKeyStroke(enter,new AbstractAction("Send Message") {
			public void actionPerformed(ActionEvent e)
			{
				sendMessage();
			}
		});
		keymap.addActionForKeyStroke(altenter,new TextAction("Newline") {
			public void actionPerformed(ActionEvent e)
			{
				getTextComponent(e).replaceSelection("\n");
			}
		});
		typing.setKeymap(keymap);
		JPanel typearea = new JPanel(new BorderLayout(2,2));
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e)
			{
				sendMessage();
			}
		});
		typearea.add(new JScrollPane(typing),BorderLayout.CENTER);
		typearea.add(send,BorderLayout.EAST);

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(new StyledEditorKit.BoldAction());
		toolbar.add(new StyledEditorKit.ItalicAction());
		toolbar.add(new StyledEditorKit.UnderlineAction());
		typearea.add(toolbar,BorderLayout.NORTH);

		messagebox = new JTextPane() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				super.paintComponent(g2);
			}
		};
		messagebox.setEditorKit(new AnimatedStyledEditorKit());
		messagelog = (StyledDocument)messagebox.getDocument();

		messagebox.setEditable(false);
		JScrollPane scrollable = new JScrollPane(messagebox);

		statusbar = new JLabel(" ");
		statusbar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,scrollable,typearea);
		main.setResizeWeight(1);
		main.setDividerLocation(0.7);
		getContentPane().add(main,BorderLayout.CENTER);
		getContentPane().add(statusbar,BorderLayout.SOUTH);
		typing.requestFocusInWindow();
	}

	public void messageReceived(MessageEvent e)
	{
		/*try
		{
			URL sound = getClass().getResource("message.wav");
			AudioInputStream is = AudioSystem.getAudioInputStream(sound);
			Clip clip = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class,is.getFormat()));
			clip.open(is);
			clip.start();
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
		}*/
		try
		{
			MutableAttributeSet namestyle = new SimpleAttributeSet(defaultattrs);
			StyleConstants.setBold(namestyle,true);
			StyleConstants.setForeground(namestyle,Color.blue);
			messagelog.insertString(messagelog.getLength(),e.getSource().getName()+": ",namestyle);
			String[] text = e.getMessageText();
			AttributeSet[] attrs = e.getMessageAttributes();
			Position endpos = messagelog.createPosition(messagelog.getLength());
			for (int loop=0; loop<text.length; loop++)
			{
				buddy.getGroup().getService().documentInsert(messagelog,messagebox,endpos,text[loop],attrs[loop]);
			}
			messagelog.insertString(endpos.getOffset(),"\n",namestyle);
			requestFocus();
		}
		catch (Exception ex)
		{
		}
	}

	public void messageTypingStarted(MessageEvent e)
	{
		statusbar.setText(buddy.getName()+" is typing a message");
	}

	public void messageTypingFinished(MessageEvent e)
	{
		statusbar.setText(" ");
	}
}
