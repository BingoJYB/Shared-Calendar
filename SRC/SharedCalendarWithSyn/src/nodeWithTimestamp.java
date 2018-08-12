import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class nodeWithTimestamp implements synchronization{
	private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public static final int ADD_ID = -1;	
    public static final int UNREGISTER_ID = -2;
	
	public static int timestamp = 0;
	private static Map<Integer, Integer> okMap = new HashMap<Integer, Integer>();
	private static Map<Integer, Boolean> accessingMap = new HashMap<Integer, Boolean>();
	private static Map<Integer, Boolean> wantToAccessMap = new HashMap<Integer, Boolean>();
	private static Map<Integer, List<String>> queueMap = new HashMap<Integer, List<String>>();
	
	public timestampOperation operateTimestamp=new timestampOperation();
	
	public void run(){
		// reinitialize variables on startup
		// ensures valid state if register and unregister multiple times
		timestamp=0;
		okMap.clear();
		accessingMap.clear();
		wantToAccessMap.clear();
		queueMap.clear();
		
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
					operateTimestamp.enterCriticalSection(ADD_ID);
					
					try {
						node.operateAppointment.addNewAppointment(startDateTime,durationInMinutes,header,comment);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateTimestamp.leaveCriticalSection(ADD_ID);
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
					operateTimestamp.enterCriticalSection(sequentialId);
					
					try {
						node.operateAppointment.modifyAppointment(sequentialId,startDateTime,durationInMinutes,header,comment);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateTimestamp.leaveCriticalSection(sequentialId);
				}
		
				//remove an appointment
				else if (option_online==3){
					Scanner reader_removeId=new Scanner(System.in);
					
					System.out.println("Please input the id of the appointment you want to remove");
					int sequentialId=reader_removeId.nextInt();
					
					// Enter Critical Section
					operateTimestamp.enterCriticalSection(sequentialId);
		
					try {
						node.operateAppointment.removeAppointment(sequentialId);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateTimestamp.leaveCriticalSection(sequentialId);
				}
				
				//list all appointments
				else if (option_online==4){					
					node.operateAppointment.listAppointment();
				}
		
				//unregister
				else if (option_online==5){
					// Enter Critical Section
					operateTimestamp.enterCriticalSection(UNREGISTER_ID);
					
					try {
						node.operateMember.unregister(node.node_url);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateTimestamp.leaveCriticalSection(UNREGISTER_ID);

					synAlgorithmOperation.RandA=false;
					
					break;
				}
		
				// unregister + exit
				else if (option_online==6){
					// Enter Critical Section
					operateTimestamp.enterCriticalSection(UNREGISTER_ID);
					
					try {
						node.operateMember.unregister(node.node_url);
					} catch (Exception e) {
						e.printStackTrace();
					}
						
					// Leave Critical Section
					operateTimestamp.leaveCriticalSection(UNREGISTER_ID);

					synAlgorithmOperation.RandA=false;
		
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
	
	static synchronized int getOk(int appointmentId) {
		if (!okMap.containsKey(appointmentId)) {
			okMap.put(appointmentId, 0);
		}
		
		return okMap.get(appointmentId);
	}
	
	static synchronized void setOk(int appointmentId, int okValue) {
		okMap.put(appointmentId, okValue);
	}
	
	static synchronized boolean isAccessing(int appointmentId) {
		if (!accessingMap.containsKey(appointmentId)) {
			accessingMap.put(appointmentId, false);
		}
		
		return accessingMap.get(appointmentId);
	}
	
	static synchronized void setAccessing(int appointmentId, boolean isAccessing) {
		accessingMap.put(appointmentId, isAccessing);
	}
	
	static synchronized boolean isWantToAccess(int appointmentId) {
		if (!wantToAccessMap.containsKey(appointmentId)) {
			wantToAccessMap.put(appointmentId, false);
		}
		
		return wantToAccessMap.get(appointmentId);
	}
	
	static synchronized void setWantToAccess(int appointmentId, boolean isWantToAccess) {
		wantToAccessMap.put(appointmentId, isWantToAccess);
	}
	
	static synchronized void addQueueItem(int appointmentId, String nodeUrl) {
		if (!queueMap.containsKey(appointmentId)) {
			queueMap.put(appointmentId, new ArrayList<String>());
		}
		
		queueMap.get(appointmentId).add(nodeUrl);
	}
	
	static synchronized void clearQueue(int appointmentId) {
		if (!queueMap.containsKey(appointmentId)) {
			queueMap.put(appointmentId, new ArrayList<String>());
		}
		
		queueMap.get(appointmentId).clear();
	}
	
	static synchronized List<String> getQueueCopy(int appointmentId) {
		if (!queueMap.containsKey(appointmentId)) {
			queueMap.put(appointmentId, new ArrayList<String>());
		}
		
		return new ArrayList<String>(queueMap.get(appointmentId));
	}
}