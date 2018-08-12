public class getOKHandler {
	public boolean grantAccess(int timestamp, String nodeUrl, int appointmentId){
		int okValue = nodeWithTimestamp.getOk(appointmentId) + 1;
		nodeWithTimestamp.setOk(appointmentId, okValue);

        timestampOperation.receiveEvent(timestamp);

        return true;
	}
}