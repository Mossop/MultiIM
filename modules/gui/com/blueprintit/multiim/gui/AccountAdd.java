package com.blueprintit.multiim.gui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.util.Iterator;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import com.blueprintit.multiim.IM;
import com.blueprintit.multiim.ServiceProvider;

public class AccountAdd extends JDialog
{
	private JLabel userlabel;
	private JLabel passlabel;
	private JTextField username;
	private JPasswordField password;
	private JComboBox service;
	private IM im;
	private boolean ok;

	public AccountAdd(IM im)
	{
		super();
		ok=false;
		this.im=im;
		initialiseDisplay();
	}

	public boolean isOK()
	{
		return ok;
	}

	public String getUsername()
	{
		return username.getText();
	}

	public String getPassword()
	{
		return new String(password.getPassword());
	}

	public ServiceProvider getSelectedProvider()
	{
		return (ServiceProvider)service.getSelectedItem();
	}

	public void initialiseDisplay()
	{
		setTitle("Add an Account");
		getContentPane().setLayout(new GridBagLayout());
		userlabel = new JLabel("Username:");
		passlabel = new JLabel("Password:");
		username = new JTextField(10);
		password = new JPasswordField(10);

		Insets insets = new Insets(2,2,2,2);

		GridBagConstraints constraints = new GridBagConstraints();

		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.anchor=GridBagConstraints.EAST;
		constraints.fill=GridBagConstraints.NONE;
		getContentPane().add(new JLabel("Service:"),constraints);

		Iterator loop = im.getServiceProviders();
		Vector sps = new Vector();
		while (loop.hasNext())
		{
			sps.add(loop.next());
		}
		service = new JComboBox(sps);
		service.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ServiceProvider sp = (ServiceProvider)service.getSelectedItem();
				userlabel.setText(sp.getUsernameText());
				passlabel.setText(sp.getPasswordText());
			}
		});
		service.setSelectedIndex(0);
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=1;
		constraints.gridy=0;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		getContentPane().add(service,constraints);

		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.anchor=GridBagConstraints.EAST;
		constraints.fill=GridBagConstraints.NONE;
		getContentPane().add(userlabel,constraints);
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.anchor=GridBagConstraints.EAST;
		constraints.fill=GridBagConstraints.NONE;
		getContentPane().add(passlabel,constraints);
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=1;
		constraints.gridy=1;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		getContentPane().add(username,constraints);
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=1;
		constraints.gridy=2;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		getContentPane().add(password,constraints);

		JButton okbtn = new JButton("Ok");
		okbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ok=true;
				setVisible(false);
			}
		});
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=0;
		constraints.gridy=3;
		constraints.anchor=GridBagConstraints.CENTER;
		constraints.fill=GridBagConstraints.NONE;
		getContentPane().add(okbtn,constraints);

		JButton cancelbtn = new JButton("Cancel");
		cancelbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		constraints = new GridBagConstraints();
		constraints.insets=insets;
		constraints.gridx=1;
		constraints.gridy=3;
		constraints.anchor=GridBagConstraints.CENTER;
		constraints.fill=GridBagConstraints.NONE;
		getContentPane().add(cancelbtn,constraints);

		setModal(true);
		setSize(250,150);
		setResizable(false);
		setVisible(true);
	}
}
