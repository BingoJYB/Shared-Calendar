import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;


public class synAlgorithmOperation {
	public static final String tokenRingName = "TokenRing";
	public static final String ricartAgrawalaName = "RicartAgrawala";
	public static boolean tokenRing=false;
	public static boolean RandA=false;
	public static synchronization nodeRun;
	
	public void getAlgorithm(String contactnode_url){
		try{
			Vector<Integer> params=new Vector<Integer>(0);
			XmlRpcClient client=new XmlRpcClient(contactnode_url);
		
			String algorithmType=(String) client.execute("getSyncMethod",params);
			
			if (algorithmType.equals(tokenRingName)) {
				tokenRing=true;
				
				nodeRun=new nodeWithToken();
			}
			else if (algorithmType.equals(ricartAgrawalaName)) {
				RandA=true;
			
				nodeRun=new nodeWithTimestamp();
			}
		}
		catch(Exception e){
			System.err.println(e);
		}	
	}
}