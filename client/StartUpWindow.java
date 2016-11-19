package client;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - StartUp application for chat room client
 */
@SuppressWarnings("serial")
public class StartUpWindow extends JFrame
{
	/**
	 * Variables needed to property app start
	 */	
	private JTextField clientIdTextField;
	private JTextField serverIpTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					StartUpWindow frame = new StartUpWindow();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartUpWindow()
	{
		setTitle("Log Client");
		setType(Type.UTILITY);
		JPanel contentPane;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 223, 112);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		clientIdTextField = new JTextField();
		clientIdTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		clientIdTextField.setForeground(Color.WHITE);
		clientIdTextField.setBackground(Color.LIGHT_GRAY);
		clientIdTextField.setBounds(91, 0, 116, 20);
		clientIdTextField.setColumns(10);
		contentPane.add(clientIdTextField);
		
		JButton okBtn = new JButton("OK");	
		okBtn.setForeground(Color.LIGHT_GRAY);
		okBtn.setBackground(Color.DARK_GRAY);
		okBtn.setBounds(0, 42, 105, 31);
		contentPane.add(okBtn);
		
		JButton closeBtn = new JButton("CLOSE");	
		closeBtn.setForeground(Color.LIGHT_GRAY);
		closeBtn.setBackground(Color.DARK_GRAY);
		closeBtn.setBounds(102, 42, 105, 31);
		contentPane.add(closeBtn);
		
		JTextField txtMachineId = new JTextField();
		txtMachineId.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMachineId.setEnabled(false);
		txtMachineId.setEditable(false);
		txtMachineId.setText("Nickname:");
		txtMachineId.setForeground(Color.WHITE);
		txtMachineId.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtMachineId.setColumns(10);
		txtMachineId.setBackground(Color.GRAY);
		txtMachineId.setBounds(0, 0, 92, 20);
		contentPane.add(txtMachineId);
		
		serverIpTextField = new JTextField();
		serverIpTextField.setForeground(Color.WHITE);
		serverIpTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		serverIpTextField.setColumns(10);
		serverIpTextField.setBackground(Color.LIGHT_GRAY);
		serverIpTextField.setBounds(91, 21, 116, 20);
		contentPane.add(serverIpTextField);
		
		JTextField txtServerIp = new JTextField();
		txtServerIp.setText("Server IP:");
		txtServerIp.setHorizontalAlignment(SwingConstants.RIGHT);
		txtServerIp.setForeground(Color.WHITE);
		txtServerIp.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtServerIp.setEnabled(false);
		txtServerIp.setEditable(false);
		txtServerIp.setColumns(10);
		txtServerIp.setBackground(Color.GRAY);
		txtServerIp.setBounds(0, 21, 92, 20);
		contentPane.add(txtServerIp);
		
		/** LISTENERS */	
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				okActionPerformed();
			}
		});
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelActionPerformed();
			}
		});
		
		try
		{
			getServerIP();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "connection.properties file writing/reading failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	protected void getServerIP() throws IOException
	{
		File file = new File("connection.properties");
		Properties prop = new Properties();
		if (!file.exists())
		{
			file.createNewFile();
			prop.setProperty("serverIp", "127.0.0.1");
			prop.setProperty("clientIdentifier", "DEFAULT");
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			prop.store(bufferWritter, "SERVER IP ADRESS AND " + "CLIENT ID THAT WILL BE SHOWN TO SERVER");
			bufferWritter.close();
		}
		try (FileReader reader = new FileReader("connection.properties"))
		{
			
			prop.load(reader);
			serverIpTextField.setText(prop.getProperty("serverIp"));
			clientIdTextField.setText(prop.getProperty("clientIdentifier"));
		}
	}

	private void okActionPerformed()
	{
		if(clientIdTextField.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "Client identifier needed.", "NEEDED",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try
		{
			new Thread(new ClientThread(clientIdTextField.getText(),InetAddress.getByName(serverIpTextField.getText()))).start();
//			ChatClient clientApp = new ChatClient(clientIdTextField.getText(),InetAddress.getByName(serverIpTextField.getText()));
//			clientApp.setVisible(true);
//			clientApp.startNetworking();
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "Unknown Host", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		this.setVisible(false);	
		this.dispose();
	}
	
	private void cancelActionPerformed()
	{
		this.dispose();
	}
}
