import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class defaultHandler {
	private getNodesHandler getNodesHandler=new getNodesHandler();
	private getCalendarHandler getCalendarHandler=new getCalendarHandler();
	private registerHandler registerHandler=new registerHandler();
	private addAppointmentHandler addAppointmentHandler=new addAppointmentHandler();
	private modifyAppointmentHandler modifyAppointmentHandler=new modifyAppointmentHandler();
	private removeAppointmentHandler removeAppointmentHandler=new removeAppointmentHandler();
	private unregisterHandler unregisterHandler=new unregisterHandler();
	private getTokenHandler getTokenHandler=new getTokenHandler();
	private getTimestampHandler getTimestampHandler=new getTimestampHandler();
	private getOKHandler getOKHandler=new getOKHandler();
	private getAlgorithmHandler getAlgorithmHandler=new getAlgorithmHandler();

	public synchronized Vector<String> getNodes() {
		return getNodesHandler.getNodes();
	}
	
	public synchronized Vector<Hashtable<String,Object>> getCalendar() {
		return getCalendarHandler.getCalendar();
	}
	
	public synchronized boolean register(String url) {
		return registerHandler.register(url);
	}
	
	public synchronized boolean add(int sequentialId,Date date,int durationInMinutes,String header,String comment){
		return addAppointmentHandler.add(sequentialId, date, durationInMinutes, header, comment);
	}
	
	public synchronized boolean modify(int sequentialId,Date date,int durationInMinutes,String header,String comment){
		return modifyAppointmentHandler.modify(sequentialId, date, durationInMinutes, header, comment);
	}
	
	public synchronized boolean remove(int sequentialId) {
		return removeAppointmentHandler.remove(sequentialId);
	}
	
	public synchronized boolean unregister(String url){
		return unregisterHandler.unregister(url);
	}
	
	public synchronized boolean receiveToken(){
		return getTokenHandler.receiveToken();
	}
	
	public synchronized boolean requestAccess(int timestamp, String nodeUrl, int appointmentId) {
		return getTimestampHandler.requestAccess(timestamp, nodeUrl, appointmentId);
	}
	
	public synchronized boolean grantAccess(int timestamp, String nodeUrl, int appointmentId){
		return getOKHandler.grantAccess(timestamp, nodeUrl, appointmentId);
	}
	
	public synchronized String getSyncMethod(){
		return getAlgorithmHandler.getSyncMethod();
	}
}