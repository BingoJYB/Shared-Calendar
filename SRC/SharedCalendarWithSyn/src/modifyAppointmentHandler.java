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
import org.w3c.dom.NodeList;

public class modifyAppointmentHandler{
	public boolean modify(int sequentialId,Date date,int durationInMinutes,String header,String comment){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_Calendar = dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			Node appointments=doc_Calendar.getElementsByTagName("appointments").item(0);
			
			Element newAppointment=doc_Calendar.createElement("appointment");
			
			Element newId_Appointment=doc_Calendar.createElement("id");
			newId_Appointment.appendChild(doc_Calendar.createTextNode(String.valueOf(sequentialId)));
			newAppointment.appendChild(newId_Appointment);
			
			Element newDate_Appointment=doc_Calendar.createElement("date");
			newDate_Appointment.appendChild(doc_Calendar.createTextNode(simpleDateFormat.format(date)));
			newAppointment.appendChild(newDate_Appointment);
			
			Element newDuration_Appointment=doc_Calendar.createElement("duration");
			newDuration_Appointment.appendChild(doc_Calendar.createTextNode(String.valueOf(durationInMinutes)));
			newAppointment.appendChild(newDuration_Appointment);
			
			Element newHeader_Appointment=doc_Calendar.createElement("header");
			newHeader_Appointment.appendChild(doc_Calendar.createTextNode(header));
			newAppointment.appendChild(newHeader_Appointment);
			
			Element newComment_Appointment=doc_Calendar.createElement("comment");
			newComment_Appointment.appendChild(doc_Calendar.createTextNode(comment));
			newAppointment.appendChild(newComment_Appointment);
			
			NodeList appointmentList=doc_Calendar.getElementsByTagName("appointment");
			for (int i=0;i<appointmentList.getLength();i++){
				Node appointmentId=appointmentList.item(i).getFirstChild();
				Node appointmentIdValue=appointmentId.getFirstChild();
				if (appointmentIdValue.getNodeValue().equals(String.valueOf(sequentialId))){
					appointments.replaceChild(newAppointment,appointmentList.item(i));
					break;
				}
			}
			// write the content into xml file
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