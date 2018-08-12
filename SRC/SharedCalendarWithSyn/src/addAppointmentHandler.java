import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class addAppointmentHandler{
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
	public boolean add(int sequentialId,Date date,int durationInMinutes,String header,String comment){
		try{
			// read contents from appointments XML file and add received appointment to DOM tree 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_Calendar = dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			Node appointments=doc_Calendar.getElementsByTagName("appointments").item(0);
			
			Element appointment=doc_Calendar.createElement("appointment");
			appointments.appendChild(appointment);
			
			Element id_Appointment=doc_Calendar.createElement("id");
			id_Appointment.appendChild(doc_Calendar.createTextNode(String.valueOf(sequentialId)));
			appointment.appendChild(id_Appointment);
			
			Element date_Appointment=doc_Calendar.createElement("date");
			date_Appointment.appendChild(doc_Calendar.createTextNode(simpleDateFormat.format(date)));
			appointment.appendChild(date_Appointment);
			
			Element duration_Appointment=doc_Calendar.createElement("duration");
			duration_Appointment.appendChild(doc_Calendar.createTextNode(String.valueOf(durationInMinutes)));
			appointment.appendChild(duration_Appointment);
			
			Element header_Appointment=doc_Calendar.createElement("header");
			header_Appointment.appendChild(doc_Calendar.createTextNode(header));
			appointment.appendChild(header_Appointment);
			
			Element comment_Appointment=doc_Calendar.createElement("comment");
			comment_Appointment.appendChild(doc_Calendar.createTextNode(comment));
			appointment.appendChild(comment_Appointment);
			
			// write contents into appointments XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc_Calendar);
			StreamResult result = new StreamResult(new File(node.APPOINTMENTS_FILE_PATH));
			transformer.transform(source, result);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return true;
	}
}