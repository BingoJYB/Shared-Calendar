import java.util.*;
import java.io.*;

import org.apache.xmlrpc.*;

import java.text.SimpleDateFormat;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class appointmentOperation{
	private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	public void getAppointments(String contactnode_url){
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_calendar= dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			Node appointments=doc_calendar.getElementsByTagName("appointments").item(0);
		
			Vector<Integer> params=new Vector<Integer>(0);
			XmlRpcClient client=new XmlRpcClient(contactnode_url);
		
			// getCalendar()
			Vector appointmentInfo=(Vector) client.execute("getCalendar",params);
				
			// write the appointments from another node into xml
			appointments.getParentNode().removeChild(appointments);
			
			doc_calendar.appendChild(doc_calendar.createElement("appointments"));
			appointments=doc_calendar.getElementsByTagName("appointments").item(0);
						
			Node appointment;
			for (int i=0;i<appointmentInfo.size();i++){
				appointment=doc_calendar.createElement("appointment");
				appointments.appendChild(appointment);
					
				Element id_Appointment=doc_calendar.createElement("id");
				Object id_Appointment_value=((Hashtable) appointmentInfo.elementAt(i)).get("ID");
				id_Appointment.appendChild(doc_calendar.createTextNode(String.valueOf(id_Appointment_value)));
				appointment.appendChild(id_Appointment);
				
				Element date_Appointment=doc_calendar.createElement("date");
				Object date_Appointment_value=((Hashtable) appointmentInfo.elementAt(i)).get("startDateTime");
				date_Appointment.appendChild(doc_calendar.createTextNode(simpleDateFormat.format(date_Appointment_value)));
				appointment.appendChild(date_Appointment);
				
				Element duration_Appointment=doc_calendar.createElement("duration");
				Object duration_Appointment_value=((Hashtable) appointmentInfo.elementAt(i)).get("durationInMinutes");
				duration_Appointment.appendChild(doc_calendar.createTextNode(String.valueOf(duration_Appointment_value)));
				appointment.appendChild(duration_Appointment);
				
				Element header_Appointment=doc_calendar.createElement("header");
				Object header_Appointment_value=((Hashtable) appointmentInfo.elementAt(i)).get("header");
				header_Appointment.appendChild(doc_calendar.createTextNode((String) header_Appointment_value));
				appointment.appendChild(header_Appointment);
				
				Element comment_Appointment=doc_calendar.createElement("comment");
				Object comment_Appointment_value=((Hashtable) appointmentInfo.elementAt(i)).get("comment");
				comment_Appointment.appendChild(doc_calendar.createTextNode((String) comment_Appointment_value));
				appointment.appendChild(comment_Appointment);
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc_calendar);
			StreamResult result = new StreamResult(new File(node.APPOINTMENTS_FILE_PATH));
			transformer.transform(source, result);
		}
		catch(Exception e){
			System.out.println("You create the calendar network.");
			System.out.println("");
			
			while (true){
				System.out.println("Please select one syncronization algorithm.");
				System.out.println("1.Token Ring  2.Ricard&Agrawala");
			
				Scanner reader_algorithm=new Scanner(System.in);
				int algorithm=reader_algorithm.nextInt();
				
				if (algorithm==1){
					synAlgorithmOperation.tokenRing=true;
					
					synAlgorithmOperation.nodeRun=new nodeWithToken();
					
					(new tokenOperation()).createToken();
					
					System.out.println("");
					System.out.println("You choose the token ring algorithm.");
					System.out.println("Now token has been created!");
					
					break;
				}
				
				else if (algorithm==2){
					synAlgorithmOperation.RandA=true;
					
					synAlgorithmOperation.nodeRun=new nodeWithTimestamp();
					
					System.out.println("");
					System.out.println("You choose the Ricard&Agrawala algorithm.");
					
					break;
				}
			}
		}
	}

	public void addNewAppointment(Date startDateTime,int durationInMinutes,String header,String comment){
		try{
			int sequentialId=0;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_calendar= dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
		
			int maxId=0;
			NodeList appointmentIdList=doc_calendar.getElementsByTagName("id");
			for (int i=0; i<appointmentIdList.getLength(); i++) {
				int nodeId=Integer.parseInt(appointmentIdList.item(i).getFirstChild().getNodeValue());
				maxId=Math.max(maxId, nodeId);
			}
			sequentialId=maxId+1;
		
			Vector<Object> newAppointment=new Vector<Object>();
			newAppointment.addElement(sequentialId);
			newAppointment.addElement(startDateTime);
			newAppointment.addElement(durationInMinutes);
			newAppointment.addElement(header);
			newAppointment.addElement(comment);
		
			for (int i=0;i<node.memberInfo.size();i++){
				XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
				client.execute("add",newAppointment);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void modifyAppointment(int sequentialId,Date startDateTime,int durationInMinutes,String header,String comment){
		try{
			Vector<Object> modifiedAppointment=new Vector<Object>();
			modifiedAppointment.addElement(sequentialId);
			modifiedAppointment.addElement(startDateTime);
			modifiedAppointment.addElement(durationInMinutes);
			modifiedAppointment.addElement(header);
			modifiedAppointment.addElement(comment);
		
			for (int i=0;i<node.memberInfo.size();i++){
				XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
				client.execute("modify",modifiedAppointment);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void listAppointment(){
		try{
			int totalAppointment=0;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_calendar= dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			totalAppointment=doc_calendar.getElementsByTagName("id").getLength();
		
			String appointmentMenu[]={"ID","Date","Duration(min)","Header","Comment"};
			
			System.out.printf("%-3s",appointmentMenu[0]);
			System.out.printf(" %-18s",appointmentMenu[1]);
			System.out.printf("%-15s",appointmentMenu[2]);
			System.out.printf("%-15s",appointmentMenu[3]);
			System.out.printf("%s%n",appointmentMenu[4]);
		
			for (int i=0;i<totalAppointment;i++){
			Element appointment_m=(Element) doc_calendar.getElementsByTagName("appointment").item(i);
			Element id=(Element) appointment_m.getElementsByTagName("id").item(0);
			Element date=(Element) appointment_m.getElementsByTagName("date").item(0);
			Element duration=(Element) appointment_m.getElementsByTagName("duration").item(0);
			Element header=(Element) appointment_m.getElementsByTagName("header").item(0);
			Element comment=(Element) appointment_m.getElementsByTagName("comment").item(0);
		
			String idValue=id.getChildNodes().item(0).getNodeValue();
			String dateValue=date.getChildNodes().item(0).getNodeValue();
			String durationValue=duration.getChildNodes().item(0).getNodeValue();
			String headerValue=header.getChildNodes().item(0).getNodeValue();
			String commentValue=comment.getChildNodes().item(0).getNodeValue();
		
			System.out.printf("%-4s",idValue);
			System.out.printf("%-18s",dateValue);
			System.out.printf("%-14s",durationValue);
			System.out.printf(" %-15s",headerValue);
			System.out.printf("%s%n",commentValue);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void removeAppointment(int sequentialId){
		try{
			Vector<Integer> removedAppointmentParams=new Vector<Integer>();
			removedAppointmentParams.addElement(sequentialId);
			
			for (int i=0;i<node.memberInfo.size();i++){
				XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
				client.execute("remove",removedAppointmentParams);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
}