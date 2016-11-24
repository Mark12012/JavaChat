package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class ChatClient extends JFrame
{
	ClientThread client;
	
	private String clientName;
	
	/** Swing variables */
	private DefaultListModel<String> listModel;
	private JTabbedPane tabbedPane;
	public JPopupMenu popup;
	JList<String> listOfUsers;
	
	/** Tab handlers */
	private Map<Integer,JTextArea> handlers;


	/**
	 * Create the frame.
	 */
	public ChatClient(String clientName, InetAddress serverAdress, ClientThread client)
	{
		this();
		this.clientName = clientName;
		this.client = client;
		//this.serverAdress = serverAdress;
		handlers = new HashMap<>();
		setTitle("CHAT CLIENT - " + clientName);
	}
	public ChatClient()
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				client.endConnection();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 645, 343);
		
		JPanel contentPane = new JPanel();
		contentPane.setBackground(SystemColor.control);
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(SystemColor.control);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		listModel = new DefaultListModel<>();
		
		listOfUsers = new JList<>(listModel);
		listOfUsers.setBackground(SystemColor.control);
		listOfUsers.setBounds(422, 10, 197, 283);
		
		JScrollPane listScrollPane = new JScrollPane(listOfUsers);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setBorder(BorderFactory.createTitledBorder("Users online"));
		listScrollPane.setPreferredSize(new Dimension(200,0));
		contentPane.add(listScrollPane, BorderLayout.EAST);	
		
		/** POPUP */
	    popup = new JPopupMenu();
	    String[] names = new String[2];
	    ActionListener menuListener = new ActionListener() {
	      public void actionPerformed(ActionEvent event) {
	    	  if(names[0] == null)
	    		  names[0] = listOfUsers.getSelectedValue();
	    	  else
	    		  if(names[1] == null)
	    		  {
		    		  names[1] = listOfUsers.getSelectedValue();
		    		  client.addToChatroom(names[0], names[1]);
	    		  }
	        
	      }
	    };
	    JMenuItem item;
	    popup.add(item = new JMenuItem("Create chatroom"));
	    item.setHorizontalTextPosition(JMenuItem.RIGHT);
	    item.addActionListener(menuListener);
	    
	    listOfUsers.addMouseListener(new MousePopupListener());
	}
	
	
	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
	      checkPopup(e);
	    }

	    public void mouseClicked(MouseEvent e) {
	      checkPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	      checkPopup(e);
	    }

	    private void checkPopup(MouseEvent e) {
	      if (e.isPopupTrigger()) {
	    	listOfUsers.setSelectedIndex(listOfUsers.locationToIndex(e.getPoint()));
	    	if(listOfUsers.getSelectedIndex() != -1)
	    		popup.show(e.getComponent(), e.getX(), e.getY());
	      }
	    }
	  }

	
	public JPanel getChatPanelForTab()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		
		JTextArea chatArea = new JTextArea();
		((DefaultCaret)chatArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatArea.setEditable(false);
		handlers.put(handlers.size(), chatArea);
		JScrollPane jspForChat = new JScrollPane(chatArea);
		jspForChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspForChat.setBorder(BorderFactory.createTitledBorder("Conversation with " + handlers.size()));
		panel.add(jspForChat,BorderLayout.CENTER);
		
		JPanel outputPanel = new JPanel(new BorderLayout()) ;
		JTextField outputTextField = new JTextField();
		outputPanel.add(outputTextField,BorderLayout.CENTER);
		JButton sendBtn = new JButton("SEND");
		outputPanel.add(sendBtn,BorderLayout.EAST);
		panel.add(outputPanel,BorderLayout.SOUTH);
		
		
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ms = outputTextField.getText();
				AES aes = new AES();
				client.sendMessageToServer(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()), aes.encrypt(ms, null));
				chatArea.append("[" + clientName + "]" +" : " 
						+ ms + "\n" );
				outputTextField.setText("");
			}
		});
		outputTextField.addKeyListener(new KeyAdapter() {
			  public void keyPressed(KeyEvent e) {
			    if (e.getKeyCode()==KeyEvent.VK_ENTER){
				    sendBtn.doClick();
			    }
			  }
		});
		
		return panel;
	}
	
	public void showMessage(String from, String message)
	{
		handlers.get(tabbedPane.indexOfTab(from)).append("[" + from + "]" + " : " + message + "\n");
	}
	
	public void clearHandlers()
	{
		handlers = new HashMap<>();
	}
	
	/** GETTERS */
	public String getClientName()
	{
		return clientName;
	}
	public DefaultListModel<String> getListModel()
	{
		return listModel;
	}
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}
	public Map<Integer, JTextArea> getHandlers()
	{
		return handlers;
	}
}
