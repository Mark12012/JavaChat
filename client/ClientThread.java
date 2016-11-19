package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - Networking thread for chat room client 
 */
public class ClientThread extends Thread
{
	/** Handlers */
	//private ChatClient frameHandler;
	private ChatClient clientApp;
	/** Networking variables */
	Socket connection;
	private String clientName;
	private InetAddress serverAdress;
	private ObjectOutputStream oOutputStream;
	private ObjectInputStream oInputStream;
	
	public ClientThread(String clientName, InetAddress serverAdress)
	{
		this.clientName = clientName;
		this.serverAdress = serverAdress;
		clientApp = new ChatClient(clientName, serverAdress, this);
		clientApp.setVisible(true);
		
	}	
	
	public void run()
	{
		startNetworking();
		sendClientId();
		while(true)
		{
			try
			{
				String receivedMessage = getMessageFromServer();
				doActionWithMessage(receivedMessage);
			}
			catch (EOFException ex)
			{
				JOptionPane.showMessageDialog(clientApp, "Connection Failed", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(clientApp, "Connection Failed", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void startNetworking()
	{
		try
		{
			connectToServer();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(clientApp, "Connection Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/** CONNECTION METHODS */
	private void connectToServer() throws IOException
	{
		//@SuppressWarnings("resource")
		connection = new Socket(serverAdress, 6664);
		oOutputStream = new ObjectOutputStream(connection.getOutputStream());
		oOutputStream.flush();
		oInputStream = new ObjectInputStream(connection.getInputStream());
	}
	public void endConnection(){
		try {
			oOutputStream.close();
			oInputStream.close();
			connection.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(clientApp, "Closing connection failed!", "ERROR",
					JOptionPane.ERROR_MESSAGE);

		} finally {
			clientApp.dispose();
			System.exit(1);
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
			JOptionPane.showMessageDialog(clientApp, "Sending Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void sendMessageToServer(String clientName, String message){
		try
		{
			oOutputStream.writeObject(this.clientName + "~" + clientName + "~" + message);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(clientApp, "Sending Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(clientApp, "Receiving Failed", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		return message;
	}
	
	private void doActionWithMessage(String message)
	{
		if(!message.startsWith("<clients>"))
		{
			List<String> splitted = Arrays.asList(message.split("~"));
			String from = splitted.get(0);
			message = splitted.get(1);
			clientApp.showMessage(from, message);
			return;
		}

		/** Get refreshed users list */
		List<String> splitted = Arrays.asList(message.split("[<>]+"));
		splitted = splitted.subList(2, splitted.size());
		splitted = splitted.stream().filter(i -> !i.equals(clientApp.getClientName())).collect(Collectors.toList());
		if(splitted.size() != clientApp.getListModel().size())
		{
			clientApp.clearHandlers();
			clientApp.getListModel().removeAllElements();
			clientApp.getTabbedPane().removeAll();
			for(int i =0 ; i< splitted.size();i++)
			{
					JPanel panel = clientApp.getChatPanelForTab();
					clientApp.getListModel().addElement(splitted.get(i));
					clientApp.getTabbedPane().addTab(splitted.get(i), panel);
			}	
		}
	}
}
