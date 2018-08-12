import java.util.*;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class getCalendarHandler{

	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public Vector<Hashtable<String,Object>> getCalendar(){
		Vector<Hashtable<String,Object>> params=new Vector<Hashtable<String,Object>>();
		Hashtable<String,Object> struct;
	
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			
			NodeList appointmentList=doc.getElementsByTagName("appointment");
			
			for (int i=0;i<appointmentList.getLength();i++){
				Node id=appointmentList.item(i).getChildNodes().item(0);
				Node date=appointmentList.item(i).getChildNodes().item(1);
				Node duration=appointmentList.item(i).getChildNodes().item(2);
				Node header=appointmentList.item(i).getChildNodes().item(3);
				Node comment=appointmentList.item(i).getChildNodes().item(4);
				
				String idValue=id.getChildNodes().item(0).getNodeValue();
				String dateValue=date.getChildNodes().item(0).getNodeValue();
				String durationValue=duration.getChildNodes().item(0).getNodeValue();
				String headerValue=header.getChildNodes().item(0).getNodeValue();
				String commentValue=comment.getChildNodes().item(0).getNodeValue();
				
				struct=new Hashtable<String,Object>();
				struct.put("ID",Integer.parseInt(idValue));
				struct.put("startDateTime",simpleDateFormat.parse(dateValue));
				struct.put("durationInMinutes",Integer.parseInt(durationValue));
				struct.put("header",headerValue);
				struct.put("comment",commentValue);
				
				params.addElement(struct);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return params;
	}
}