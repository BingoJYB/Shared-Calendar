import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class nodeWithToken implements synchronization{
	private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public static int oldNodeCount = 0;
	public static boolean hasToken=false;
	public static boolean accessing=false;
	public static boolean wantToAccess=false;
	public static List<String> cachedNodeList = new ArrayList<String>();
	
	public tokenOperation operateToken=new tokenOperation();

	// Add listener when nodeWithToken class is loaded
	static {
		node.memberInfo.addNodeListChangeListener(new NodeList.NodeListChangeListener() {	
			@Override
			public void nodeListChangeEvent() {
				if (synAlgorithmOperation.tokenRing) {
			        int nodeCount = node.memberInfo.size();
			
			        // Node A is the ONLY node in the network and has the token.
			        // Then a second node B joins the network.
			        // The token is passed on to B, if it is not needed by A.
			        // If there is only 1 node in the network, it is not necessary 
			        // to pass the token around. But if a second node joins the network,
			        // it becomes necessary to pass the token.
			        // This method ensures that token passing is reactivated,
			        // if a second node joins the network. 
			        if (hasToken && !wantToAccess && !accessing && oldNodeCount == 1 && nodeCount == 2) {
			            tokenOperation.passToken(node.memberInfo.toList());
			        }
			
			        oldNodeCount = nodeCount;
				}
			}
		});
	}
	
	public void run(){
		// reinitialize variables on startup
		// ensures valid state if register and unregister multiple times
		accessing=false;
		wantToAccess=false;
		cachedNodeList.clear();
		
		// oldNodeCount is set by listener
		// hasToken is set from outside
		
		while (true){
			try{
				System.out.println("");
				System.out.println("Please choose your option:");
				System.out.println("1.add a new appointment  2.modify an appointment  3.remove an appointment");
				System.out.println("4.list appointments  	 5.sign off               6.exit");
	
				Scanner reader_option_online=new Scanner(System.in);
				int option_online=reader_option_online.nextInt();
				System.out.println("");
		
				//add a new appointment
				if (option_online==1){
					Scanner reader_addDate=new Scanner(System.in);
					Scanner reader_addDuration=new Scanner(System.in);
					Scanner reader_addHeader=new Scanner(System.in);
					Scanner reader_addComment=new Scanner(System.in);
		
					Date startDateTime;
		
					String header,
						   comment;
				   
					int durationInMinutes;
			
					//input parameters of the new appointment
					System.out.println("Please input the date (format: dd/MM/yyyy HH:mm)");
					startDateTime=simpleDateFormat.parse(reader_addDate.nextLine());
					System.out.println("");
			
					System.out.println("Please input the duration (in minutes)");
					durationInMinutes=reader_addDuration.nextInt();
					System.out.println("");
			
					System.out.println("Please input the header");
					header=reader_addHeader.nextLine();
					System.out.println("");
			
					System.out.println("Please input the comment");
					comment=reader_addComment.nextLine();
			
					// Enter Critical Section
					operateToken.enterCriticalSection();
					
					try {
						node.operateAppointment.addNewAppointment(startDateTime,durationInMinutes,header,comment);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateToken.leaveCriticalSection();
				}
		
				//modify an appointment
				else if (option_online==2){
					Scanner reader_modifyId=new Scanner(System.in);
					Scanner reader_modifyDate=new Scanner(System.in);
					Scanner reader_modifyDuration=new Scanner(System.in);
					Scanner reader_modifyHeader=new Scanner(System.in);
					Scanner reader_modifyComment=new Scanner(System.in);
		
					Date startDateTime;
					
					String header,
						   comment;
				   
					int sequentialId,
						durationInMinutes;
					
					//input parameters of the new appointment
					System.out.println("Please modify an appointment");
					System.out.println("");
			
					System.out.println("Please input the id of the appointment you want to modify");
					sequentialId=reader_modifyId.nextInt();
					System.out.println("");
			
					System.out.println("Please input the date (format: dd/MM/yyyy HH:mm)");
					startDateTime=simpleDateFormat.parse(reader_modifyDate.nextLine());
					System.out.println("");
			
					System.out.println("Please input the duration (in minutes)");
					durationInMinutes=reader_modifyDuration.nextInt();
					System.out.println("");
			
					System.out.println("Please input the header");
					header=reader_modifyHeader.nextLine();
					System.out.println("");
			
					System.out.println("Please input the comment");
					comment=reader_modifyComment.nextLine();
				
					// Enter Critical Section
					operateToken.enterCriticalSection();
					
					try {
						node.operateAppointment.modifyAppointment(sequentialId,startDateTime,durationInMinutes,header,comment);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateToken.leaveCriticalSection();
				}
		
				//remove an appointment
				else if (option_online==3){
					Scanner reader_removeId=new Scanner(System.in);
					
					System.out.println("Please input the id of the appointment you want to remove");
					int sequentialId=reader_removeId.nextInt();
		
					// Enter Critical Section
					operateToken.enterCriticalSection();
					
					try {
						node.operateAppointment.removeAppointment(sequentialId);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateToken.leaveCriticalSection();
				}
				
				//list all appointments
				else if (option_online==4){
					node.operateAppointment.listAppointment();
				}
		
				// unregister
				else if (option_online==5){
					// Enter Critical Section
					operateToken.enterCriticalSection();
					
					try {
						node.operateMember.unregister(node.node_url);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					// Leave Critical Section
					operateToken.leaveCriticalSection();
					
					// if the node is the only node in the network, it hasn't passed the token
					nodeWithToken.hasToken = false;
					synAlgorithmOperation.tokenRing=false;
					
					break;
				}
		
				// unregister + exit
				else if (option_online==6){
					// Enter Critical Section
					operateToken.enterCriticalSection();
					
					try {
						node.operateMember.unregister(node.node_url);
					} catch (Exception e) {
						e.printStackTrace();
					}
		
					// Leave Critical Section
					operateToken.leaveCriticalSection();
					
					// if the node is the only node in the network, it hasn't passed the token
					nodeWithToken.hasToken = false;
					synAlgorithmOperation.tokenRing=false;
					
					System.exit(0);
				}
		
				//input the wrong symbols
				else{
					System.out.println("The symbol is unrecognized. Please choose again.");
			
					continue;
				}
			}
			//deal with the exceptions when running.
			catch(Exception e){
				System.out.println("");
				System.out.println(e.getMessage());
				System.out.println("Wrong input! Please choose again!");
				
				continue;
			}
		}
	}
}