package chat;

import java.io.*;
public class Client 
{
	static final String IPaddress = "127.0.0.1";
	
	public static void main(String[] args) 
	{
		GUI_Connect client; 
		client = new GUI_Connect(IPaddress); 
		try 
		{
			client.runClient();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
