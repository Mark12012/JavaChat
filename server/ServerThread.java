package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - Networking thread for chat room server 
 */
public class ServerThread extends Thread
{
	private Socket connection;
	private ObjectInputStream oInputStream;
	private ObjectOutputStream oOutputStream;
	private JTextArea logTextArea;
	private Map<String,ObjectOutputStream> clientsMap;
	private DefaultListModel<String> listModel;
	private String clientName;
	private boolean clientConnected;

	public ServerThread(Socket connection,JTextArea logTextArea,DefaultListModel<String> listModel,
			Map<String,ObjectOutputStream> clientsMap)
	{
		this.connection = connection;
		this.logTextArea = logTextArea;
		this.clientsMap = clientsMap;
		this.listModel = listModel;
		clientConnected = true;
	}

	public void run()
	{
		try
		{
			openStreams();
			getClientId();
		}
		catch (IOException e)
		{
			sysOut("Starting new connection (streams and getting nickname) failed.");
		}

		/** Demon which sending regularly connected clients list */
		startListRefreshingDemon();
		
		while (clientConnected)
		{
			String receivedMessage;
			try
			{
				receivedMessage = getMessageFromClient();
				if (receivedMessage != null)
					generateResponse(receivedMessage);
			}
			catch (EOFException ex)
			{
				sysOut(ex.getMessage() + "---> Client " + clientName  + " disconnected." +
						"(" + connection + ")");
				listModel.removeElement(clientName);
				clientConnected = false;
			}
			catch (IOException ex)
			{
				sysOut(ex.getMessage() + "---> Client " + clientName  + " disconnected." +
						"(" + connection + ")");
				listModel.removeElement(clientName);
				clientConnected = false;
			}
		}
	}
	
	private void openStreams() throws IOException
	{
		oInputStream = new ObjectInputStream(connection.getInputStream());
		oOutputStream = new ObjectOutputStream(connection.getOutputStream());
		oOutputStream.flush();
	}
	
	private void getClientId() throws IOException
	{
		try
		{
			clientName = oInputStream.readObject().toString();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		listModel.addElement(clientName);
		clientsMap.put(clientName, oOutputStream);
	}

	private void startListRefreshingDemon()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (clientConnected)
				{
						try
						{
							sendAllClients();
						}
						catch (IOException ex)
						{
							sysOut("Sending users list failed.");
						}
						
						try
						{
							Thread.sleep(3000);
						}
						catch (InterruptedException e)
						{
							sysOut("Sending users demon interrupted.");
						}
				}
			}
		}).start();
	}

	private void sendAllClients() throws IOException
	{
		StringBuilder sb = new StringBuilder("<clients>");
		for (int i = 0; i < listModel.size(); i++)
			sb.append("<" + listModel.get(i) + ">");

		sendMessage(sb.toString());
	}

	private void generateResponse(String receivedMessage) throws IOException
	{
		if (receivedMessage.equals(clientName))
			return;
		
		List<String> splitted = Arrays.asList(receivedMessage.split("~"));
		sendMessageToDestination(splitted.get(0),splitted.get(1),splitted.get(2));
	}

	private void sendMessageToDestination(String from, String to, String msg) throws IOException
	{
		clientsMap.get(to).writeObject(from +"~" + msg);
	}

	

	private String getMessageFromClient() throws IOException
	{
		String message = null;
		try
		{
			message = oInputStream.readObject().toString();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return message;
	}
	
	private void sendMessage(String respondMsg) throws IOException
	{
		oOutputStream.writeObject(respondMsg);
		oOutputStream.flush();
	}

	private void sysOut(String msg)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		logTextArea.append(dateFormat.format(cal.getTime()) + ": " + msg + "\n");
	}
}
