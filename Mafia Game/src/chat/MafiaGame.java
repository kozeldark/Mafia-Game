package chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MafiaGame //게임 구현 보조
{

	private String role[];
	private String killuser = "";
	private String saveuser = "";
	private String voteuser[];
	private Integer votecnt[];
//	private ArrayList<Integer> votecnt = new ArrayList<Integer>();
	

	//직업 배분
	public MafiaGame(String job[])
	{
		role = job;
	}

	

	public String Save(String User) 
	{
		saveuser = User;
		
		return saveuser;

	}
	
	public String Kill(String User) 
	{
		killuser = User;
		
		return killuser;
					
	}
	
	public String Kill_save(String User) 
	{
		
		if(saveuser == killuser)
			return null;
		else
			return killuser;
							
	}


	
	public int Vote(String User[])//투표 수 카운터로 오름차순 정렬 후 인덱스 0 값 리턴, 동표가 있으면 int 999 리턴
	   {
	      votecnt = new Integer[User.length];
	      
	      
	      for(int i=0; i < User.length; i++)
	      {
	         votecnt[i] = 0;
	      }
	      
	      for(int i=1; i < User.length; i++)
	      {
	         String intStr;
	         int Useridx = 0;
	         try {
	         intStr= User[i].replaceAll("[^0-9]", "");
	         
	         if(intStr != "")
	        	 Useridx = Integer.parseInt(intStr);        	 
	         
	         votecnt[Useridx] = votecnt[Useridx] + 1;
	         }catch(NullPointerException e) {
	            
	         }
	         
	      }
	      int max = votecnt[0];
	      int index=0;
	      for(int i=1; i<User.length;i++) {
	         if(max<votecnt[i]) {
	            max=votecnt[i];
	            index=i;
	         }
	      }
	      
	      Arrays.sort(votecnt, Collections.reverseOrder());
	      if(votecnt[0] == votecnt[1])
	         return 999;
	         
	      return index;
	   
	   }



	public String Winner() 
	{
		int citcnt = 0;
		int mfcnt = 0;
		int dccnt = 0;
		
		for(int i=0; i<role.length; i++ )
		{
			if(role[i] == "시민")
				citcnt++;
			else if(role[i] == "마피아")
				mfcnt++;
			else if(role[i] == "의사")
				dccnt++;
				
		}
		if (dccnt + citcnt == 0 && mfcnt != 0) 
			return "마피아가 이겼습니다";
		else if(dccnt + citcnt != 0 && mfcnt == 0)
			return "시민이 이겼습니다";
		else
			return "yet";
	
	}

}