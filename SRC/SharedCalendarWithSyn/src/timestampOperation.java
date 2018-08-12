import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlrpc.XmlRpcClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class timestampOperation {
	
	public void enterCriticalSection(int appointmentId){
		try{
			nodeWithTimestamp.setOk(appointmentId, 0);
			nodeWithTimestamp.setWantToAccess(appointmentId, true);
			
			for (int i=0;i<node.memberInfo.size();i++){
				if (node.memberInfo.get(i).equals(node.node_url)) {
					int okValue = nodeWithTimestamp.getOk(appointmentId) + 1;
					nodeWithTimestamp.setOk(appointmentId, okValue);
				} else {
					XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
					
					// increase timestamp
					sendEvent();
					
					Vector<Object> params=new Vector<Object>();
					params.addElement(nodeWithTimestamp.timestamp);
					params.addElement(node.node_url);
					params.addElement(appointmentId);
					
					boolean response=(boolean) client.execute("requestAccess", params);
					
					if (response) {
						int okValue = nodeWithTimestamp.getOk(appointmentId) + 1;
						nodeWithTimestamp.setOk(appointmentId, okValue);
					}
				}
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
		
		System.out.println("Please wait..");
		System.out.println("");
		
		long startTime = System.currentTimeMillis();
        long timeout = 1000 * 60 * 2; // 2 minutes
		
        //
        // Wait for OK messages
        //
        
		while (!nodeWithTimestamp.isAccessing(appointmentId)){
			// Check status
			if (nodeWithTimestamp.getOk(appointmentId) == node.memberInfo.size()){
				nodeWithTimestamp.setWantToAccess(appointmentId, false);
				nodeWithTimestamp.setAccessing(appointmentId, true);
			}
			
			// Check timeout
			if ((System.currentTimeMillis() - startTime) > timeout) {
				System.err.println("Couldn't access critical section. Timeout waiting for responses.");
               	nodeWithTimestamp.setWantToAccess(appointmentId, false);

                break;
            }
			
			// Check status every 100 ms
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void leaveCriticalSection(int appointmentId){
		// on unregister leave all critical sections
		if (appointmentId == nodeWithTimestamp.UNREGISTER_ID) {
			List<Integer> appointmentIds = getAppointmentIdList();
			for (int id : appointmentIds) {
				leaveCriticalSectionInternal(id);
			}
		}
		
		leaveCriticalSectionInternal(appointmentId);
	}
	
	private List<Integer> getAppointmentIdList() {
		List<Integer> result = new ArrayList<Integer>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc_calendar= dBuilder.parse(node.APPOINTMENTS_FILE_PATH);
			org.w3c.dom.NodeList ids = doc_calendar.getElementsByTagName("id");
		
			for (int i=0;i<ids.getLength();i++){
				int id = Integer.parseInt(ids.item(i).getTextContent());
				result.add(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void leaveCriticalSectionInternal(int appointmentId) {
		try{			
			nodeWithTimestamp.setAccessing(appointmentId, false);
			List<String> queue = nodeWithTimestamp.getQueueCopy(appointmentId);						
			
			for (int i=0; i<queue.size(); i++){
				XmlRpcClient client=new XmlRpcClient(queue.get(i));
				
				// increase timestamp
				sendEvent();
				
				Vector<Object> params=new Vector<Object>();
				params.addElement(nodeWithTimestamp.timestamp);
				params.addElement(node.node_url);
				params.addElement(appointmentId);
				
				client.execute("grantAccess", params);
			}
			
			nodeWithTimestamp.setOk(appointmentId, 0);
			nodeWithTimestamp.clearQueue(appointmentId);
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	/**
	 * Compares two timestamps
	 * 
	 * @param timestamp
	 * @param nodeUrl
	 * 
	 * @return
	 * Returns a negative value if nodeWithTimestamp.timestamp < timestamp
	 * Returns a positive value if nodeWithTimestamp.timestamp > timestamp
	 * If nodeWithTimestamp.timestamp == timestamp the nodeUrls are compared.
	 * 
	 */
	public static int compareTimestamps(int timestamp, String nodeUrl) {
		if (nodeWithTimestamp.timestamp != timestamp)
        {
            return Integer.compare(nodeWithTimestamp.timestamp, timestamp);
        }
        else
        {
            return node.node_url.compareTo(nodeUrl);
        }
	}
	
	public static void sendEvent() {
		nodeWithTimestamp.timestamp++;
    }
	
	public static void receiveEvent(int timestamp) {
		nodeWithTimestamp.timestamp = Math.max(nodeWithTimestamp.timestamp, timestamp) + 1;
	}
}