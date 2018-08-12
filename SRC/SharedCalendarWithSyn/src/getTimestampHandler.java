import java.util.List;

public class getTimestampHandler extends timestampOperation{
	public boolean requestAccess(int timestamp, String nodeUrl, int appointmentId){
        boolean isAccessGranted = false;
        
        if (!nodeWithTimestamp.isAccessing(appointmentId) && !nodeWithTimestamp.isWantToAccess(appointmentId))
        {
            isAccessGranted = true;
        }
        else if (nodeWithTimestamp.isAccessing(appointmentId))
        {
        	nodeWithTimestamp.addQueueItem(appointmentId, nodeUrl);
        }
        else // nodeWithTimestamp.wantToAccess && !nodeWithTimestamp.accessing
        {
            if (compareTimestamps(timestamp, nodeUrl) > 0) {
                isAccessGranted = true;
            } else {
            	nodeWithTimestamp.addQueueItem(appointmentId, nodeUrl);
            }
        }
		
		// increase timestamp
		receiveEvent(timestamp);
		
		return isAccessGranted;
	}
}