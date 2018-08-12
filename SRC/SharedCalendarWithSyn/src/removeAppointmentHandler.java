import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class removeAppointmentHandler{
	
	public boolean remove(int sequentialId){
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_Calendar = dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			
			// remove appointment from XML file
			NodeList appointmentList=doc_Calendar.getElementsByTagName("appointment");
			for (int i=0;i<appointmentList.getLength();i++){
				Node appointmentId=appointmentList.item(i).getFirstChild();
				Node appointmentIdValue=appointmentId.getFirstChild();
				if (appointmentIdValue.getNodeValue().equals(String.valueOf(sequentialId))){
					appointmentList.item(i).getParentNode().removeChild(appointmentList.item(i));
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