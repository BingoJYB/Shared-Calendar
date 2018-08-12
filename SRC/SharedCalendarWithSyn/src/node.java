import java.util.*;
import java.net.URI;

import org.apache.xmlrpc.*;

import java.io.FileInputStream;

public class node{
	public static final String APPOINTMENTS_FILE_PATH="calendar.xml";
	public static final String CONFIG_FILE_PATH="config.ini";
	public static NodeList memberInfo = new NodeList();
	public static memberOpration operateMember=new memberOpration();
	public static appointmentOperation operateAppointment=new appointmentOperation();
	public static synAlgorithmOperation operateSynAlgorithm=new synAlgorithmOperation();
	public static String node_url;	
	
	public static void main(String args[]){
		System.out.println("Welcome to use the shared calendar tool!");
		System.out.println("");
		
		try{
			//set node_url
			Properties url=new Properties();
			url.load(new FileInputStream("config.ini"));
			node_url=url.getProperty("node_url");
			int port=URI.create(node_url).getPort();
			System.out.println("Your url is "+node_url);
			System.out.println("");
		
			//whether join in the network
			while (true){
				try{
					System.out.println("Do you want to join in? (Y/N)");
				
					Scanner reader_option_join=new Scanner(System.in);
					String option_join=reader_option_join.nextLine();
				
					//the node is "offline"
					if (option_join.equals("N")){
						while (true){
							System.out.println("");
							System.out.println("Please choose your option:");
							System.out.println("1.list appointments  2.go to join in  3.exit");
							
							Scanner reader_option_alone=new Scanner(System.in);
							int option_alone=reader_option_alone.nextInt();
							System.out.println("");
						
							//list all appointments when "offline"
							if (option_alone==1){
								operateAppointment.listAppointment();
							}
							
							//Go back to join in other node
							else if (option_alone==2)
								break;
						
							//exit
							else if (option_alone==3)
								System.exit(0);
						
							//input the wrong symbols
							else{
								System.out.println("The symbol is unrecognized. Please choose again.");
								
								continue;
							}
						}
					}
					
					//the node wants to join in other node
					else if (option_join.equals("Y")){
						//server start
						WebServer server=new WebServer(port);
						server.addHandler("$default", new defaultHandler());
						server.start();
		
						//set contactnode_url
						String contactnode_url=url.getProperty("contactnode_url");
						System.out.println("");
						System.out.println("The contact node url is "+contactnode_url);
						
						//getNodes
						operateMember.getMembers(contactnode_url);
				
						//getCalendar
						operateAppointment.getAppointments(contactnode_url);
						
						//getSynAlgorithm
						if (!(synAlgorithmOperation.tokenRing==true||synAlgorithmOperation.RandA==true))
							operateSynAlgorithm.getAlgorithm(contactnode_url);
					
						//register
						operateMember.register(node_url);
						
						//run the algorithm
						synAlgorithmOperation.nodeRun.run();
						
						//server shut down
						server.shutdown();
					}
				
					//input the wrong symbols
					else{
						System.out.println("");
						System.out.println("The symbol is unrecognized. Please choose again.");
						System.out.println("");
							
						continue;
					}					
				}
				//deal with the exceptions when running.
				catch(Exception e){
				System.out.println("");
				System.out.println("Wrong node ip address!");
				System.out.println("");
			
				continue;
				}
			}
		}
		catch(Exception e){
			System.exit(0);
		}
	}
}