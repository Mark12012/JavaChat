package client.clientApp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.cipher.AES;
import client.cipher.DiffieHellman;

/**
 * @author 2016 GRZEGORZ PRZYTU�A ALL RIGHTS RESERVED 
 * - Networking thread for chat room client 
 */
public class ClientThread extends Thread
{
	/** Handlers */
	private ChatClient frameThread;
	
	/** Networking variables */
	Socket connection;
	private String clientName;
	private InetAddress serverAdress;
	private ObjectOutputStream oOutputStream;
	private ObjectInputStream oInputStream;
	
	/** Cipher */
	private Map<String,DiffieHellman> keyAgreement;
	
	private boolean errorOcured;
	
	public ClientThread(String clientName, InetAddress serverAdress)
	{
		this.clientName = clientName;
		this.serverAdress = serverAdress;
		frameThread = new ChatClient(clientName, this);
		frameThread.setVisible(true);
		errorOcured = false;
		keyAgreement = new HashMap<>();
	}	
	
	public void run()
	{
		connectToServer();
		sendClientId();
		while(!errorOcured)
		{
			try
			{
				String receivedMessage = getMessageFromServer();
				doActionWithMessage(receivedMessage);
			}
			catch (EOFException ex)
			{
				JOptionPane.showMessageDialog(frameThread, "Connection Failed", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				errorOcured = true;
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(frameThread, "Connection Failed", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				errorOcured = true;
			}
		}
	}
	
	public void connectToServer()
	{
		try
		{
			connection = new Socket(serverAdress, 6664);
			oOutputStream = new ObjectOutputStream(connection.getOutputStream());
			oOutputStream.flush();
			oInputStream = new ObjectInputStream(connection.getInputStream());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Connection Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
	}
	
	private void sendClientId()
	{
		try
		{
			oOutputStream.writeObject(clientName);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Sending Id Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
	}
	
	private String getMessageFromServer() throws IOException
	{
		String message = null;
		try
		{
			message = oInputStream.readObject().toString();
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Receiving Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
		
		return message;
	}
	
	private void doActionWithMessage(String message)
	{
		if(message.startsWith("<clients>"))
		{
			/** Get refreshed users list */
			List<String> splitted = Arrays.asList(message.split("[<>]+"));
			splitted = splitted.subList(2, splitted.size());
			splitted = splitted.stream().filter(i -> !i.equals(clientName)).collect(Collectors.toList());
			if(splitted.size() != frameThread.getListModel().size())
			{
				frameThread.getListModel().removeAllElements();
				for(int i = 0; i < splitted.size(); i++)
				{
						frameThread.getListModel().addElement(splitted.get(i));
				}	
			}
			
			return;
		}
		
	
		List<String> splitted = Arrays.asList(message.split("~"));
		String from = splitted.get(0);
		message = splitted.get(1);
		if(message.startsWith("<init>"))
		{
			List<String> keys = Arrays.asList(message.split("[<>]+"));
			keys = keys.subList(2, keys.size());
			DiffieHellman someoneKeyAgreement = new DiffieHellman();
			someoneKeyAgreement.setPublicVars(new BigInteger(keys.get(keys.indexOf("p") + 1)),
					new BigInteger(keys.get(keys.indexOf("g") + 1)));
			someoneKeyAgreement.randomizePrivateValue();
			someoneKeyAgreement.setReceivedValue(new BigInteger(keys.get(keys.indexOf("B") + 1)));
			someoneKeyAgreement.generateKey();
			if(frameThread.getTabbedPane().indexOfTab(from) == -1)
			{
				JPanel panel = frameThread.generatePanelForTab();
				frameThread.getTabbedPane().addTab(from, panel);
				frameThread.showMessage(from, from + " has started conversation.");
				
				try {
					oOutputStream.writeObject(clientName + "~" + from + "~" + "<backInit>" + 
							"<B><" + someoneKeyAgreement.getA() + ">");
					keyAgreement.put(from, someoneKeyAgreement);
					frameThread.showMessage(from, keyAgreement.get(from).getKey().toString());
					oOutputStream.flush();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frameThread, "Sending init backward Failed", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}	
			}
			
			return;
		}
		if(message.startsWith("<backInit>"))
		{
			List<String> key = Arrays.asList(message.split("[<>]+"));
			key = key.subList(2, key.size());
			keyAgreement.get(from).setReceivedValue(new BigInteger(key.get(key.indexOf("B") + 1)));
			keyAgreement.get(from).generateKey();
			frameThread.showMessage(from, keyAgreement.get(from).getKey().toString());
			
			return;
		}
		else if(message.startsWith("<destroy>"))
		{
			if(frameThread.getTabbedPane().indexOfTab(from) != -1)
			{
				frameThread.removeTabAndReferences(from);
				keyAgreement.remove(from);
			}
			return;
		}
		frameThread.showMessage(from, message);
	}

	public void initializeCommunication(String userNameTo) {
		DiffieHellman myKeyAgreement = new DiffieHellman();
		myKeyAgreement.generatePublicVars();
		myKeyAgreement.randomizePrivateValue();
		try
		{
			oOutputStream.writeObject(clientName + "~" + userNameTo + "~" + "<init>" + 
					"<p><" + myKeyAgreement.getP() + ">" + 
					"<g><" + myKeyAgreement.getG() + ">" +
					"<B><" + myKeyAgreement.getA() + ">");
			keyAgreement.put(userNameTo, myKeyAgreement);
			// TODO
			oOutputStream.flush();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Sending init Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
	}

	public void encryptAndSendMessage(String userNameTo, String msg) throws IOException{ 
		AES aes = new AES();
		// TODO
		try
		{
			oOutputStream.writeObject(clientName + "~" + userNameTo + "~" + msg);	
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Sending msg Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
	}

	public void destroyingCommunication(String userNameTo) {
		try
		{
			oOutputStream.writeObject(clientName + "~" + userNameTo + "~" + "<destroy>");	
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(frameThread, "Sending destroy Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			errorOcured = true;
		}
		keyAgreement.remove(userNameTo);
	}
}
