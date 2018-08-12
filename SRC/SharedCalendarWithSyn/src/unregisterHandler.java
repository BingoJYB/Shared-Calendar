public class unregisterHandler{
	public boolean unregister(String url){	
		// remove member from main memory
		node.memberInfo.remove(url);

		return true;
	}
}