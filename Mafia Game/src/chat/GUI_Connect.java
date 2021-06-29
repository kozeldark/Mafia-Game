package chat;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

import javax.sound.sampled.*;


public class GUI_Connect extends JFrame 
{	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public static JButton ChatSend;
	public JButton Ready;
	private JPanel buttonPanel, TextPanel, titlePanel;
	private JLabel Gametitle;
	private JLabel Chattitle;
	private JTextArea ChatArea;
	private JTextField ChatInput;
	private JComboBox<String> Combo;
	private ArrayList<String> CurrentClient;
	private String message = "";
	private String Game_Server;
	private Socket client;
	
	File titlebgm = new File("C:\\Users\\차니\\Desktop\\gif\\title.wav"); //노래 파일 받아오기
	File birdbgm = new File("C:\\Users\\차니\\Desktop\\gif\\bird.wav");
	File nightbgm = new File("C:\\Users\\차니\\Desktop\\gif\\night.wav");
	File endbgm = new File("C:\\Users\\차니\\Desktop\\gif\\end.wav");
	File killbgm = new File("C:\\Users\\차니\\Desktop\\gif\\gun.wav");
	File diebgm = new File("C:\\Users\\차니\\Desktop\\gif\\die.wav");
	File savebgm = new File("C:\\Users\\차니\\Desktop\\gif\\save.wav");
	File votebgm = new File("C:\\Users\\차니\\Desktop\\gif\\vote.wav");
	File kickbgm = new File("C:\\Users\\차니\\Desktop\\gif\\kick.wav");
	File winbgm = new File("C:\\Users\\차니\\Desktop\\gif\\win.wav");
	
	Image img = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\title.gif").getImage(); //이미지 받아오기

	Image dieimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\youdied.gif").getImage(); //이미지 받아오기

	Image killimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\kill.gif").getImage(); //이미지 받아오기
	
	Image saveimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\save.gif").getImage(); //이미지 받아오기
	
	Image tomimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\tomoring.gif").getImage(); //이미지 받아오기
	
	Image tonimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\tonight.gif").getImage(); //이미지 받아오기
	
	Image voteimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\vote.gif").getImage(); //이미지 받아오기
	
	Image goimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\gameover.gif").getImage(); //이미지 받아오기
	
	Image winimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\win.gif").getImage(); //이미지 받아오기
	
	Image kickimg = new ImageIcon("C:\\Users\\차니\\Desktop\\gif\\kick.gif").getImage(); //이미지 받아오기
	 	 

	
	JTextArea GameDisplayArea = new JTextArea()
	{

        { setOpaque( false ) ;}
        
        public void paintComponent(Graphics g)
        {
            g.drawImage(img,0,0, 600, 600, this);       //이미지 출력
            super.paintComponent(g);
        }
            
    };
    

	
	public GUI_Connect(String host) 
	{

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Game_Server = host; 

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JPanel TextSendButton = new JPanel();
		
		Combo = new JComboBox<>();
		ChatInput = new JTextField(50);
		ChatSend = new JButton("Send");		
		Ready = new JButton("Ready");
		Ready.setPreferredSize(new Dimension(100, 100));
		TextSendButton.setLayout(new GridLayout(1, 1));

		JPanel chatpanel = new JPanel();

		chatpanel.setLayout(new FlowLayout());
		chatpanel.add(Combo);
		chatpanel.add(ChatInput);
		chatpanel.add(ChatSend);
		chatpanel.add(Ready);
		
		TextSendButton.add(chatpanel);

		buttonPanel.add(TextSendButton);
		buttonPanel.setVisible(true);
		add(buttonPanel, BorderLayout.SOUTH);

		
		ChatArea = new JTextArea();
		ChatArea.setLineWrap(true);
		//ChatArea.setEditable(false);
	
		GameDisplayArea.setLineWrap(true);
		GameDisplayArea.setEditable(false);

		TextPanel = new JPanel();

		TextPanel.setLayout(new GridLayout(1, 2));
		TextPanel.add(GameDisplayArea);
		TextPanel.add(new JScrollPane(ChatArea));
		add(TextPanel, BorderLayout.CENTER);
				

		titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(1, 2));

		Gametitle = new JLabel("Game Display");
		Chattitle = new JLabel("Chatting");

		titlePanel.add(Gametitle, BorderLayout.SOUTH);
		titlePanel.add(Chattitle, BorderLayout.SOUTH);
		add(titlePanel, BorderLayout.NORTH);

		setSize(1280, 800);
		setVisible(true);
		
		
		 try {	        
		        AudioInputStream stream = AudioSystem.getAudioInputStream(titlebgm);
		        Clip clip = AudioSystem.getClip();
		        clip.open(stream);
		        clip.start();
		        
		    } catch(Exception e) {		        
		        e.printStackTrace();
		    }
		
	
		ChatSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) 
			{
				String ChatMessage;
				ChatMessage = ChatInput.getText();
				String SendClient;
				SendClient = Combo.getItemAt(Combo.getSelectedIndex());
				sendData(ChatMessage, SendClient);
				ChatInput.setText("");
				ChatInput.requestFocus();
			}
		});
		
		Ready.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) 
			{
				String SendClient = Combo.getItemAt(Combo.getSelectedIndex());
				readysendData("ready", SendClient);
				Ready.setEnabled(false);
			}
		});

	}


	public void runClient() throws IOException {

		try { // 포트 연결
			client = new Socket(InetAddress.getByName(Game_Server), 6789);

			output = new ObjectOutputStream(client.getOutputStream());
			output.flush();
			input = new ObjectInputStream(client.getInputStream());

			processConnection();

		} 
		catch (EOFException eofException) 
		{

		} 
		catch (IOException ioException) 
		{
		} 
		finally 
		{
			output.close();
			input.close();
			client.close();
		}

	}


	private void processConnection() throws IOException {

		try {
			CurrentClient=(ArrayList<String>) input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for(String i:CurrentClient) {
			Combo.addItem(i);//처음에 현재유저들의 list를 받아와 Combobox에 추가
		}
		
		MafiaChat mc = new MafiaChat();//마피아일때만 마피아채팅방 생성하게 코드 수정
		WhoKill wk = new WhoKill();
		WhoSave ws = new WhoSave();
		WhoVote wv = new WhoVote();
		mc.ChatArea.append("[공지] 당신은 마피아 입니다. 이 채팅은 마피아 전용채팅으로 마피아끼리만 볼 수 있습니다.");
		
		do {
			try {
				message = (String) input.readObject(); // 메세지 한 줄씩 받아오기
				
				// 채팅 메세지 분류
				if (message.startsWith("User")) {
					ChatArea.append("\n" + message);
				}
			
				else if(message.startsWith("Come Client")) {
					Combo.addItem(message.replace("Come Client", "User"));			
				}//Client가 들어올때마다 Combobox에 추가
				else if(message.startsWith("Exit Client")) {
					Combo.removeItem(message.replace("Exit Client", "User"));
				}//Client가 나갈때마다 Combobox에서 삭제
				
				else if(message.startsWith("Mafia"))
				{
					mc.ChatArea.append("\n"+message.replace("Mafia",""));
				}//mafia 전용채팅방에 출력
				else if(message.startsWith("KillUser"))
				{
					wk.ChatSend.setEnabled(true);
				
				}//mafia 전용채팅방에 출력
				else if(message.startsWith("SaveUser"))
				{
					ws.ChatSend.setEnabled(true);
				
				}
				else if(message.startsWith("VoteUser"))
				{
					wv.ChatSend.setEnabled(true);
				
				}
				
				
				else if(message.startsWith("낮이 되었습니다."))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = tomimg;
					
					 try {	   
					        AudioInputStream stream = AudioSystem.getAudioInputStream(birdbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					 
				}					
				else if(message.startsWith("밤이 되었습니다."))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = tonimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(nightbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					 
					 
				}
				else if(message.contains("마피아입니다."))
				{	
					ChatArea.append(message);
					mc.setVisible(true);				
					
				}
				else if(message.contains("죽일"))
				{						
					System.out.println("k");
					wk.setVisible(true);
					wk.ChatArea.append("\n 입력후 창이 닫힙니다. 신중하게 입력해 주시기  바랍니다.");
					wk.ChatArea.append("\n User + 번호 또는 번호 형식으로 입력하시면 됩니다.(ex) User8 또는 8 )");
				}
				
				else if(message.contains("치유할"))
				{			
					System.out.println("a");
					ws.setVisible(true);
					ws.ChatArea.append("\n 입력후 창이 닫힙니다. 신중하게 입력해 주시기  바랍니다.");
					ws.ChatArea.append("\n User + 번호 또는 번호 형식으로 입력하시면 됩니다.(ex) User8 또는 8 )");
				}
				
				else if(message.contains("[사망]"))
				{		
					ChatArea.append("\n[Host] : " + message);
					img = killimg;
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(killbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					 
				}
				
				else if(message.contains("죽었습니다"))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = dieimg;
					GUI_Connect.ChatSend.setEnabled(false);
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(diebgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					 
				}
				else if(message.contains("[세이브]"))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = saveimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(savebgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
				}
				
				else if(message.contains("투표를 시작하겠습니다."))
				{	
					
					ChatArea.append("투표를 시작하겠습니다.");
					img = voteimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(votebgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					 
					 wv.setVisible(true);
					 wv.ChatSend.setEnabled(true);
					 wv.ChatArea.append("\n 입력후 창이 닫힙니다. 신중하게 입력해 주시기  바랍니다.");
					 wv.ChatArea.append("\n User + 번호 또는 번호 형식으로 입력하시면 됩니다.(ex) User8 또는 8 )");
					
				}
				else if(message.contains("[추방]")  && !(message.contains("999")))
				{
					ChatArea.append("\n[Host] : " + message);
					GUI_Connect.ChatSend.setEnabled(false);
					
					img = kickimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(kickbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
					
				}
				
				else if(message.contains("[추방]") && message.contains("999") )
				{
					GUI_Connect.ChatSend.setEnabled(false);
								
				}
				
				else if(message.contains("[패배]"))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = goimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(endbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
				}
				
				else if(message.contains("[승리]"))
				{			
					ChatArea.append("\n[Host] : " + message);
					img = winimg;
					
					 try {	        
					        AudioInputStream stream = AudioSystem.getAudioInputStream(winbgm);
					        Clip clip = AudioSystem.getClip();
					        clip.open(stream);
					        clip.start();
					        
					    } catch(Exception e) {		        
					        e.printStackTrace();
					    }
				}
				
				else { // Client가 포함되지 않은 내용은 게임 정보 출력
					ChatArea.append(message +"\n");
				}
			} 
			catch (ClassNotFoundException classNotFoundException) {
				GameDisplayArea.append("\n.");
			}

		} while (!message.equals("The server Closed."));
	}

	


	// 클라이언트에서 서버로 메세지 전송
	private void sendData(String message, String target) {
		try {
			output.writeObject("send"+"/"+target+"/"+message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
			GameDisplayArea.append("\nwriting Error\n");
		}
	}
	
	private void readysendData(String message, String target) {
		try {
			output.writeObject(message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
			GameDisplayArea.append("\nwriting Error\n");
		}
	}

	
	
	
 //죽일 사람 입력창
	public class WhoKill extends JFrame{
		private JButton ChatSend;
		private JPanel buttonPanel, TextPanel, titlePanel;
		private JLabel Chattitle;
		private JTextArea ChatArea;
		private JTextField ChatInput;

		WhoKill() {//
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 2));

			JPanel TextSendButton = new JPanel();
			
			ChatInput = new JTextField(20);
			ChatSend = new JButton("Send");	
			
			TextSendButton.setLayout(new GridLayout(4, 0));

			JPanel chatpanel = new JPanel();

			chatpanel.setLayout(new FlowLayout());
			chatpanel.add(ChatInput);
			chatpanel.add(ChatSend);
			
			TextSendButton.add(chatpanel);

			buttonPanel.add(TextSendButton);
			buttonPanel.setVisible(true);
			add(buttonPanel, BorderLayout.SOUTH);

			
			ChatArea = new JTextArea();
			ChatArea.setLineWrap(true);
			ChatArea.setEditable(false);
			
			
			TextPanel = new JPanel();

			TextPanel.setLayout(new GridLayout(1, 2));
			TextPanel.add(new JScrollPane(ChatArea));
			add(TextPanel, BorderLayout.CENTER);
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridLayout(1, 2));

			Chattitle = new JLabel("Who will you kill?");
			
			titlePanel.add(Chattitle, BorderLayout.SOUTH);
			add(titlePanel, BorderLayout.NORTH);
			
			setSize(400, 600);
			setVisible(false);
	
			ChatSend.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent event) 
				{
					String ChatMessage;
					ChatMessage = ChatInput.getText();
					int killidx =Integer.parseInt(ChatMessage.replaceAll("[^0-9]", ""));
					System.out.println(killidx +  "/" + CurrentClient.size());
					while(true)
					{
						if(killidx < 1 || killidx > CurrentClient.size())
						{
							System.out.println("다시");
							ChatArea.append("다시");
							ChatSend.setEnabled(true);
							ChatMessage = ChatInput.getText();
							killidx =Integer.parseInt(ChatMessage.replaceAll("[^0-9]", ""));
						}
						else
							break;
					}
					
					sendKilldata(ChatMessage);
					ChatInput.setText("");
					ChatInput.requestFocus();
					ChatSend.setEnabled(false);
					setVisible(false);
				}
			});
			
		
			
		}
	
	}
	
	//세이브 유저 입력 창		
	public class WhoSave extends JFrame{
		private JButton ChatSend;
		private JPanel buttonPanel, TextPanel, titlePanel;
		private JLabel Chattitle;
		private JTextArea ChatArea;
		private JTextField ChatInput;

		WhoSave() {//
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 2));

			JPanel TextSendButton = new JPanel();
			
			ChatInput = new JTextField(20);
			ChatSend = new JButton("Send");	
			
			TextSendButton.setLayout(new GridLayout(4, 0));

			JPanel chatpanel = new JPanel();

			chatpanel.setLayout(new FlowLayout());
			chatpanel.add(ChatInput);
			chatpanel.add(ChatSend);
			
			TextSendButton.add(chatpanel);

			buttonPanel.add(TextSendButton);
			buttonPanel.setVisible(true);
			add(buttonPanel, BorderLayout.SOUTH);

			
			ChatArea = new JTextArea();
			ChatArea.setLineWrap(true);
			ChatArea.setEditable(false);
			
			
			TextPanel = new JPanel();

			TextPanel.setLayout(new GridLayout(1, 2));
			TextPanel.add(new JScrollPane(ChatArea));
			add(TextPanel, BorderLayout.CENTER);
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridLayout(1, 2));

			Chattitle = new JLabel("Who will you save?");
			
			titlePanel.add(Chattitle, BorderLayout.SOUTH);
			add(titlePanel, BorderLayout.NORTH);
			
			setSize(400, 600);
			setVisible(false);
	
			ChatSend.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent event) 
				{
					String ChatMessage;
					ChatMessage = ChatInput.getText();
					sendSavedata(ChatMessage);
					ChatInput.setText("");
					ChatInput.requestFocus();
					ChatSend.setEnabled(false);
					setVisible(false);
				}
			});
			
		
			
		}
	
	}
	
	public class WhoVote extends JFrame{
		private JButton ChatSend;
		private JPanel buttonPanel, TextPanel, titlePanel;
		private JLabel Chattitle;
		private JTextArea ChatArea;
		private JTextField ChatInput;

		WhoVote() {//
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 2));

			JPanel TextSendButton = new JPanel();
			
			ChatInput = new JTextField(20);
			ChatSend = new JButton("Send");	
			
			TextSendButton.setLayout(new GridLayout(4, 0));

			JPanel chatpanel = new JPanel();

			chatpanel.setLayout(new FlowLayout());
			chatpanel.add(ChatInput);
			chatpanel.add(ChatSend);
			
			TextSendButton.add(chatpanel);

			buttonPanel.add(TextSendButton);
			buttonPanel.setVisible(true);
			add(buttonPanel, BorderLayout.SOUTH);

			
			ChatArea = new JTextArea();
			ChatArea.setLineWrap(true);
			ChatArea.setEditable(false);
			
			
			TextPanel = new JPanel();

			TextPanel.setLayout(new GridLayout(1, 2));
			TextPanel.add(new JScrollPane(ChatArea));
			add(TextPanel, BorderLayout.CENTER);
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridLayout(1, 2));

			Chattitle = new JLabel("Who will you vote?");
			
			titlePanel.add(Chattitle, BorderLayout.SOUTH);
			add(titlePanel, BorderLayout.NORTH);
			
			setSize(400, 600);
			setVisible(false);
	
			ChatSend.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent event) 
				{
					String ChatMessage;
					ChatMessage = ChatInput.getText();
					sendVotedata(ChatMessage);
					ChatInput.setText("");
					ChatInput.requestFocus();
					ChatSend.setEnabled(false);
					setVisible(false);
				}
			});
	
		}
	
	}
	
	//마피아 전용 채팅창
	public class MafiaChat extends JFrame{
		private JButton ChatSend;
		private JPanel buttonPanel, TextPanel, titlePanel;
		private JLabel Chattitle;
		private JTextArea ChatArea;
		private JTextField ChatInput;

		MafiaChat() {//
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 2));

			JPanel TextSendButton = new JPanel();
			
			ChatInput = new JTextField(20);
			ChatSend = new JButton("Send");	
			
			TextSendButton.setLayout(new GridLayout(4, 0));

			JPanel chatpanel = new JPanel();

			chatpanel.setLayout(new FlowLayout());
			chatpanel.add(ChatInput);
			chatpanel.add(ChatSend);
			
			TextSendButton.add(chatpanel);

			buttonPanel.add(TextSendButton);
			buttonPanel.setVisible(true);
			add(buttonPanel, BorderLayout.SOUTH);

			
			ChatArea = new JTextArea();
			ChatArea.setLineWrap(true);
			ChatArea.setEditable(false);
			
			
			TextPanel = new JPanel();

			TextPanel.setLayout(new GridLayout(1, 2));
			TextPanel.add(new JScrollPane(ChatArea));
			add(TextPanel, BorderLayout.CENTER);
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridLayout(1, 2));

			Chattitle = new JLabel("Mafia Chatting");
			
			titlePanel.add(Chattitle, BorderLayout.SOUTH);
			add(titlePanel, BorderLayout.NORTH);
			
			setSize(400, 600);
			setVisible(false);
	
			ChatSend.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent event) 
				{
					String ChatMessage;
					ChatMessage = ChatInput.getText();
					sendDataMafia(ChatMessage);
					ChatInput.setText("");
					ChatInput.requestFocus();
				}
			});
			
		
			
		}
	
	}
	private void sendDataMafia(String message) {
		try {
			output.writeObject("Mafia"+message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
		}
	}
	
	private void sendKilldata(String message) {
		try {
			output.writeObject("KillUser"+message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
		}
	}
	
	private void sendSavedata(String message) {
		try {
			output.writeObject("SaveUser"+message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
		}
	}
	
	private void sendVotedata(String message) {
		try {
			output.writeObject("VoteUser"+message);//server에서 message를 구분하기위해 구분자로 send 추가
			output.flush();
		} catch (IOException ioException) {
		}
	}
	

}
