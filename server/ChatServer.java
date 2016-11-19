package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

/**
 * @author 2016 GRZEGORZ PRZYTU�A ALL RIGHTS RESERVED 
 * - Swing application for chat room server
 */
@SuppressWarnings("serial")
public class ChatServer extends JFrame
{
	/** Networking */
	private ServerSocket server;
	
	/** Swing */
	private JTextArea logTextArea;
	private DefaultListModel<String> listModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				ChatServer serverApp = new ChatServer();
				serverApp.setVisible(true);
				serverApp.startServer();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatServer()
	{
		setTitle("CHAT-ROOM SERVER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 645, 343);
		
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		logTextArea = new JTextArea();
		logTextArea.setBackground(SystemColor.inactiveCaptionBorder);
		logTextArea.setEnabled(false);
		logTextArea.setEditable(false);
		logTextArea.setLineWrap(true);
		((DefaultCaret)logTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
		contentPane.add(logScrollPane, BorderLayout.CENTER);

		listModel = new DefaultListModel<>();
		JList<String> listOfUsers = new JList<>(listModel);
		listOfUsers.setBackground(SystemColor.inactiveCaptionBorder);
		listOfUsers.setBounds(422, 10, 197, 283);
		
		JScrollPane listScrollPane = new JScrollPane(listOfUsers);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setBorder(BorderFactory.createTitledBorder("List of users"));
		listScrollPane.setPreferredSize(new Dimension(200, 0));
		contentPane.add(listScrollPane, BorderLayout.EAST);
	}

	public void startServer()
	{
		sysOut("---> Server start");
		Map<String,ObjectOutputStream> clientsMap = new HashMap<>();
		
		try
		{
			server = new ServerSocket(6664, 20);
		}
		catch (IOException ex)
		{
			sysOut(ex.getMessage() + "---> Server gone down, creating sever socket failed");
		}

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					Socket connection = null;
					try
					{
						connection = server.accept();
					}
					catch (IOException ex)
					{
						sysOut(ex.getMessage() + "---> Accepting clients failed");
					}
					sysOut("---> New Connection with: " + connection);
					new Thread(new ServerThread(connection,logTextArea,listModel,clientsMap )).start();
				}
			}
		}).start();
	}

	private void sysOut(String msg)
	{
		DateFormat dateFormat = new SimpleDateFormat("#yyyy/MM/dd HH:mm:ss#");
		Calendar cal = Calendar.getInstance();

		logTextArea.append(dateFormat.format(cal.getTime()) + " : " + msg + "\n");
	}

}