package chat;

import java.io.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;

import chat.GUI_Connect.MafiaChat;

import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.util.Random;

public class Host extends JFrame 
{
	private JButton Start, Hostbt; // 게임 시작 버튼

	private JTextArea displayArea; //정보 출력
	private ExecutorService executor; //Player를 실행할 executor
	private ServerSocket server; //서버 소켓
	private SocketServer[] socketServer; //클라이언트 소켓
	private int counter = 1; //연결 호스트 cnt
	private int playersnum; // 사용자 수
	private int livenum;
	private MafiaGame mafiastart;
	private int allready =0;
	private String killuser = " ";
	private String saveuser = " ";
	private int intStr;
	private int votecnt = 1;
	private boolean night;
	private String voteuser[]= new String[20];

	
	ArrayList<String> currentClient = new ArrayList<>();
	
	public Host()//호스트 프레임 설정 및 출력 
	{

		JFrame j1 = new JFrame();	
		JPanel jp = new JPanel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		socketServer = new SocketServer[100];
		executor = Executors.newFixedThreadPool(20); //최대 클라이언트를 20개로 설정한다.
		
		Start = new JButton("Game Start");
		Start.setEnabled(false);
		Start.setPreferredSize(new Dimension(400, 50));
		Start.setBackground(Color.red);
			
		Hostbt = new JButton(" HOST ");
		Hostbt.setEnabled(false);
		Hostbt.setPreferredSize(new Dimension(400, 50));
		
		jp.setLayout(new FlowLayout());
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		displayArea.setPreferredSize(new Dimension(350, 500));
		jp.add(Hostbt);
		jp.add(new JScrollPane(displayArea));	
		jp.add(Start);
		
		j1.setContentPane(jp);
		
		j1.setSize(400, 650);
		j1.setVisible(true);

	}

	
	public void ServerOpen() 
	{
		try {
			server = new ServerSocket(6789); // 포트 6789로 설정
			displayArea.append("                                                  WHO IS?\n");
			currentClient.add("모두");
			while (true) {
				try {
					// 서버가 종료될때까지 클라이언트의 접속을 기다리고 소켓 실행.
					socketServer[counter] = new SocketServer(counter);
					socketServer[counter].waitForConnection();
					executor.execute(socketServer[counter]);
					counter++;
				} catch (EOFException eofException) {
					displayArea.append("\n서버 종료");
				}
			}
		} catch (IOException e) {
		}
	}


	private class SocketServer implements Runnable//소켓 연결
	{
		private ObjectOutputStream output;
		private ObjectInputStream input;
		private Socket connection;
		private int ClientNumber;


		public SocketServer(int counter)//생성자 
		{
			ClientNumber = counter;
		
		}
		
		public void run()
		{

			try {
				try {

					output = new ObjectOutputStream(connection.getOutputStream());
					sendCurrentClients(ClientNumber);
					output.flush();

					currentClient.add("User"+Integer.toString(ClientNumber));
					input = new ObjectInputStream(connection.getInputStream());

					String message = "User " + ClientNumber + "가 게임에 참여했습니다.";
					//sendData(message); 
					try 
					{			
						for (int i = 1; i <= counter; i++) 
						{	
							if(i==ClientNumber)
								continue;
							socketServer[i].sendData("Come Client"+ClientNumber);//ComboBox에 추가하기 위해 Client number를 보내준다
						}
					} 								
					catch (NullPointerException e) 
					{
					}
						
					do 
					{									
						try 
						{	
							
							try 
							{	
								if(message.startsWith("send/모두")) 
								{//client로부터 받은 메세지 중 모두에게 보내는 메세지
									for (int i = 1; i <= counter; i++) 
									{		
										socketServer[i].sendData("User" + ClientNumber + " : " + message.replace("send/모두/", ""));
									}					
								}
								else if(message.startsWith("ready")) 
								{//client로부터 받은 메세지 중 모두에게 보내는 메세지
									allready++;		
									if(allready == counter - 1 && allready >= 5)
									{
										Start.setEnabled(true);
										Start.addActionListener(new ActionListener() 
										{
											public void actionPerformed(ActionEvent event) 
											{
												Start.setBackground(Color.green);
												displayArea.append("Game Start!!!!!!!");
												JobSet();
											}
										});
									}
								}
								else if(message.startsWith("send/User")) 
								{//Client로 받은 메세지 중 귓속말
									String[] msg = message.split("/");
									try {
				                           socketServer[Integer.parseInt(msg[1].replace("User",""))].sendData("User"+ClientNumber+"으로부터 귓속말 : "+msg[2]);
				                           socketServer[ClientNumber].sendData(msg[1]+"에게 귓속말 : "+msg[2]);
				                           }
									catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
									{
									}
								}
								else if(message.startsWith("Mafia")) {//마피아전용채팅방에 보내는 메세지								
									for(int i=1; i<counter; i++) {//마피아에게만 보내도록 코드수정
									socketServer[i].sendInfo("MafiaUser" + ClientNumber + " : " + message);
									}
								}
								
								else if(message.startsWith("Kill"))//마피아가 누구를 죽일지 who kill 채팅방에 보내는 메세지
								{
									killuser = message.replace("Kill", "");
									for(int i=1; i<counter; i++) {
										socketServer[i].sendData("KillUser" + ClientNumber + " : " + message);
										}
								}
								
								else if(message.startsWith("Save"))//의사가 누구를 치유할지 who save 채팅방에 보내는 메세지
								{
									saveuser = message.replace("Save","");
									for(int i=1; i<counter; i++) {
										socketServer[i].sendData("SaveUser" + ClientNumber + " : " + message);
										}
								}
								
								else if(message.startsWith("Vote"))//유저가 누구를 투표할지 who vote 채팅방에 보내는 메세지
								{	
									voteuser[votecnt] = message.replace("VoteUser","");
									votecnt++;
								}
								
								else {//그 외에 서버가 보내는 메세지
									for (int i = 1; i <= counter; i++) 
									{		
										socketServer[i].sendData("User" + ClientNumber + " : " + message);
									}
								}
							} 								
							catch (NullPointerException e) 
							{
							}							
							message = (String) input.readObject(); // 메세지 받아오기
						} 
						catch (ClassNotFoundException c) 
						{

						}

					} while (!message.equals("The server Closed."));
				} 
				catch (EOFException f) 
				{
					displayArea.append("\nClient" + ClientNumber + " 접속 종료");
				} 
				finally 
				{
					closeConnection();//연결 종료
				}
			} 
			catch (IOException e) 
			{
			}
		}

		
		private void JobSet()//직업 배분 및 게임 실행
		{	
			
			String role[] = new String[counter + 1];
				
			String msg = null;
			
				playersnum = counter - 1;		
				livenum = counter;
				int[] mfidx = new int[(int)(playersnum / 5) + 1];
				int mfidxcnt =1;
				
				while(mfidxcnt == (int)(playersnum / 5) )
				{
					mfidx[mfidxcnt] = 	 (playersnum - 1) + 1 ;
					mfidxcnt++;
				}
				
				int[] dcidx = new int[(int)(playersnum / 5) + 1];
				int dcidxcnt =1;
				
				while(dcidxcnt == (int)(playersnum / 5) )
				{
					dcidx[dcidxcnt] =  (int)Math.random() * (playersnum) + 1 ;
					dcidxcnt++;	
				}
				
				for (int i = 1; i < dcidx.length; i++) 
				{			
					for (int j = 1; j < mfidx.length; j++) 
					{			
						if(dcidx[i] == mfidx[j])
						{
							while(dcidx[i] == mfidx[j])
							{
								dcidx[i] =  (int)Math.random() * (playersnum) + 1 ;
							}
						}
					}
				}

				
				//직업 분배
				for (int i = 1; i < counter; i++) 
				{		
					role[i] = "시민";
				}

				for (int i = 1; i < mfidx.length; i++) 
				{			
					role[mfidx[i]] = "마피아";
				}
				for (int i = 1; i < dcidx.length; i++) 
				{			
					role[dcidx[i]] = "의사";
				}
				//직업공지			
				for (int i = 1; i < counter; i++) 
				{			
					msg = role[i];
					socketServer[i].sendData("\n[Host](으)로부터 귓속말 : 당신의 직업은 " + msg + "입니다.\n");
				}
				
				String role2[] = role;
			mafiastart = new MafiaGame(role);
			
			for (int i = 1; i < counter; i++) 
			{		
				socketServer[i].sendInfo("마피아 게임에 오신 걸 환영합니다.\n");
				socketServer[i].sendInfo("낮이 되었습니다.\n");
				socketServer[i].sendInfo("유저들끼리 서로 확인할 시간을 15초를 드리겠습니다.\n");
			}
			
			sleep(15000);//유저들끼리 서로 인지할 시간 15초
			
			while(mafiastart.Winner() == "yet")//아직 승자가 결정이 안났으면
			{
				night= true;
				for (int i = 1; i < counter; i++) 
				{					
					socketServer[i].sendInfo("밤이 되었습니다.\n");
				}
				
				
				for (int i = 1; i < counter; i++) 
				{		
					if(role[i] == "마피아")
					{
						socketServer[i].sendInfo("KillUser" + "죽일 유저의 이름을 입력하세요.\n");	
						socketServer[i].sendInfo("죽일 유저의 이름을 입력하세요.\n");	
					}				
				}
				
				
				for (int i = 1; i < counter; i++) 
				{	
					if(role[i] == "의사")
					{
						socketServer[i].sendInfo("SaveUser" + "치유할 유저의 이름을 입력하세요.\n");	
						socketServer[i].sendInfo("치유할 유저의 이름을 입력하세요.\n");
					}
					
				}
				
				for (int i = 1; i < counter; i++) 
				{		
					socketServer[i].sendInfo("모두 대기하여주시기 바랍니다.\n");
				}
				
				
				boolean dcexist = false;
				
				for (int i = 1; i < counter; i++) 
				{			
					if(role[i] =="의사")
						dcexist = true;
				}
				
				while(killuser.equals(" ") || saveuser.equals(" "))
				{
					sleep(2000);
					//마피아와 의사가 입력할때까지 대기			
					
					if(!dcexist && !killuser.equals(" "))
						break;		
				}

				try {
					intStr = Integer.parseInt((killuser.replaceAll("[^0-9]", "")));
				}catch(NumberFormatException e) {
					
				}
				
				sleep(2000);
				
				for (int i = 1; i < counter; i++) 
				{		
					if(killuser.equals(saveuser))
					{
						socketServer[i].sendInfo("[세이브] : 마피아가  시민을 살해하려했으나 의사의 치료로 살았습니다." );
					}
					else 
					{				
						if(i==intStr)
						{
							socketServer[intStr].sendInfo("당신은 마피아에게 죽었습니다.\n");
							livenum--;	
						}
						if(i!=intStr)
							socketServer[i].sendInfo("[사망] : User" + intStr + "가 마피아의 공격으로 사망했습니다.");
						role[intStr] = "사망";
	
					}
				}
				
				sleep(3000);
				
				killuser=" ";
				saveuser=" ";
				night= false;
				
				sleep(2000);
				
				for (int i = 1; i < counter; i++) 
				{		
					socketServer[i].sendInfo("낮이 되었습니다.");
				}
				
				sleep(2000);
				
				if(mafiastart.Winner() != "yet")
					break;
				
				for (int i = 1; i < counter; i++) 
				{		
					if(role[i] !="사망" && role[i] !="추방")
						socketServer[i].sendInfo("투표를 시작하겠습니다.");
				}
				

				
				sleep(2000);
				
				boolean voteval = false;
				
				int voteresult = 0;
				while(voteval == false)
				{
									
					if(votecnt == livenum)			
					{
						voteresult = mafiastart.Vote(voteuser);
						
						if(voteresult == 999)
						{
							for (int i = 1; i < counter; i++) 
							{						
								socketServer[i].sendInfo("동표로 인해 무결되었습니다.");
								voteval = true;
							}
						}
							
						else
						{
							for (int i = 1; i < counter; i++) 
							{	
								if(i == voteresult)
								{									
									role[i] = "추방";
									livenum--;
									voteval = true;
								}
							
							}
						}
					}
					sleep(200);
				}
				
				for (int i = 1; i < counter; i++) 
				{
					socketServer[i].sendInfo("[추방] : User" + voteresult + "가 투표 결과로 인해 추방되었습니다.");
				}
				
				votecnt= 1 ;
				for (int i = 1; i < counter; i++) 
				{		
					voteuser[i] = null;
				}
				
				sleep(2000);
				
			}
			
			sleep(2000);
			
			for (int i = 1; i < counter; i++) 
			{					
				
				if(mafiastart.Winner() == "마피아가 이겼습니다")
				{				
					socketServer[i].sendInfo("마피아가 이겼습니다.\n");
					if(role2[i] == "마피아")
						socketServer[i].sendInfo("[승리]");
					else
						socketServer[i].sendInfo("[패배]");
				}
				else if(mafiastart.Winner() == "시민이 이겼습니다")
				{
					socketServer[i].sendInfo("시민이 이겼습니다.\n");
					
					if(role2[i] == "의사" && role2[i] == "시민")
						socketServer[i].sendInfo("[승리]");
					else
						socketServer[i].sendInfo("[패배]");
				}
			}
			
			sleep(20000);
			
		}

		//포트와의 연결 및 정보 출력
		private void waitForConnection() throws IOException {
			connection = server.accept();
			displayArea.append("\nUser " + ClientNumber + " connected by [" + connection.getInetAddress().getHostName() +"]");
		}

		
		//소켓 연결 종료
		private void closeConnection() {
			displayArea.append("\nUser " + ClientNumber + "연결 종료" + "\n");
			try 
			{			
				for (int i = 1; i <= counter; i++) 
				{	
					if(i==ClientNumber)
						continue;
					socketServer[i].sendData("Exit Client"+ClientNumber);//나갈때 ClientNumber를 Combobox업데이트를 위해 Client로 보내준다.
				}
			} 								
			catch (NullPointerException e) 
			{
			}
			try {
				
				output.close();
				input.close();
				connection.close();
			} catch (IOException e) {
			}
		}

		
		private void sendData(String message) //클라이언트와의 메세지 전달(밤에는 채팅못하게 하는 일반 채팅창)
		{
			try 
			{
				if(night==false && message!="") {
				output.writeObject(message);
				output.flush();
				}
			} 
			catch (IOException ioException) 
			{
			}
		}
		private void sendInfo(String message) //클라이언트와의 메세지 전달(밤에도 채팅할 수 있는 특수 채팅창)
		{
			try 
			{
				output.writeObject(message);
				output.flush();
			} 
			catch (IOException ioException) 
			{
			}
		}
	
		private void sendObject(Object obj)
		{
			try {
				output.writeObject(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private void sendCurrentClients(int Clientnumber) 
		{
			socketServer[Clientnumber].sendObject(currentClient);
		}
		
		private void sleep(int sleeptime) {
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		

	}


}
