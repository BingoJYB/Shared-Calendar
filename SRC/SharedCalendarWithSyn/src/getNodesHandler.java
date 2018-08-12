import java.util.*;

public class getNodesHandler{

	public Vector <String> getNodes(){
		Vector <String> params=new Vector <String>();
	
		for (int i=0;i<node.memberInfo.size();i++)
			params.addElement(node.memberInfo.get(i));

		return params;
	}
}