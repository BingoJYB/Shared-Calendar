import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;


public class tokenOperation {	
	public void createToken() {
		nodeWithToken.hasToken=true;
	}
	
	public void enterCriticalSection()
    {
        if (nodeWithToken.wantToAccess || nodeWithToken.accessing)
        {
            System.err.println("Node is already in critical section");

            return;
        }
        
        nodeWithToken.cachedNodeList.clear();
        nodeWithToken.wantToAccess = true;
        
        if (!nodeWithToken.hasToken) {
        	System.out.println("You have no token! Please wait...");
			System.out.println("");
        }

        long startTime = System.currentTimeMillis();
        long timeout = 1000 * 60 * 2; // 2 minutes
        while (!nodeWithToken.hasToken)
        {
            if ((System.currentTimeMillis() - startTime) > timeout)
            {
            	System.err.println("Couldn't access critical section. Timeout waiting for token.");
            	nodeWithToken.wantToAccess = false;

                return;
            }
            
            // Check status every 100 ms 
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        nodeWithToken.accessing = true;
        nodeWithToken.cachedNodeList = node.memberInfo.toList();
    }

    public void leaveCriticalSection()
    {
    	nodeWithToken.wantToAccess = false;
        nodeWithToken.accessing = false;

        if (!passToken(nodeWithToken.cachedNodeList)) {
        	System.err.println("Couldn't pass token");
        }
    }
	
	public static boolean passToken(List<String> nodeList)
    {
        if (!nodeWithToken.hasToken) {
            System.err.println("Node has no token and therefore couldn't pass it.");

            return false;
        }
        
        boolean hasPassedToken = true;

        if (nodeList.size() > 1) {
        	// Sorting the node list results in a strict node order.
        	// If you imagine that the first and the last node of the list are connected,
        	// then you get a description of the token ring.
        	Collections.sort(nodeList);

        	// Try to find node position in token ring
            int index = nodeList.indexOf(node.node_url);
            if (index == -1) {
                System.err.println("Couldn't pass token. Didn't find node URL in node list.");

                hasPassedToken = false;
            } else {
            	// Get next node URL
                String nextNodeUrl;
                if ((index + 1) == nodeList.size()) {
                    nextNodeUrl = nodeList.get(0);
                } else {
                    nextNodeUrl = nodeList.get(index + 1);
                }

                try {
                    nodeWithToken.hasToken = false;
                    
                    XmlRpcClient client = new XmlRpcClient(nextNodeUrl);
        			Vector<String> params=new Vector<String>(0);
                    boolean isSuccessful = (boolean) client.execute("receiveToken", params);

                    if (!isSuccessful) {
                        System.err.println("Couldn't pass token to " + nextNodeUrl);
                    }

                    hasPassedToken = isSuccessful;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return hasPassedToken;
    }
}