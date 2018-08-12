import java.util.*;

import org.apache.xmlrpc.*;


public class memberOpration{
	
	public void getMembers(String contactnode_url){
		try{		
			Vector<Integer> params=new Vector<Integer>(0);
			XmlRpcClient client=new XmlRpcClient(contactnode_url);
		
			// getNodes()
			Vector<String> memberInfo=(Vector<String>) client.execute("getNodes",params);
			
			// write the nodes ip from another node into main memory
			for (int i=0;i<memberInfo.size();i++)
				node.memberInfo.add(memberInfo.get(i));
		}
		catch(Exception e){
			System.out.println("");
			System.out.println("The contact node is unavailable!");
		}
	}
	
	public void register(String node_url){
		try{
			node.memberInfo.add(node_url);
			
			Vector<String> nodeUrlParam=new Vector<String>();
			nodeUrlParam.addElement(node_url);
				
			for (int i=0;i<node.memberInfo.size()-1;i++){
				XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
				client.execute("register", nodeUrlParam);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void unregister(String node_url){
		try{
			Vector<String> nodeUrlParam=new Vector<String>();
			nodeUrlParam.addElement(node_url);
			
			for (int i=node.memberInfo.size()-1; i>=0; i--){
				if (node.node_url.equals(node.memberInfo.get(i))) {
					// remove self directly
					node.memberInfo.remove(node.memberInfo.get(i));
				} else {
					XmlRpcClient client=new XmlRpcClient(node.memberInfo.get(i));
					client.execute("unregister",nodeUrlParam);
				}
			}
		
			// remove node addresses from main memory
			node.memberInfo.clear();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
}