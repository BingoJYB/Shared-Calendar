import java.util.ArrayList;
import java.util.List;

public class NodeList {
	private List<String> nodes = new ArrayList<String>();
	private List<NodeListChangeListener> listeners = new ArrayList<NodeListChangeListener>();	
	
	public synchronized int size() {
		return nodes.size();
	}
	
	public synchronized String get(int index) {
		return nodes.get(index);
	}
	
	public synchronized void add(String node) {
		nodes.add(node);
		fireNodeListChanged();
	}
	
	public synchronized void remove(String node) {
		nodes.remove(node);
		fireNodeListChanged();
	}
	
	public synchronized void clear() {
		nodes.clear();
		fireNodeListChanged();
	}
	
	public synchronized List<String> toList() {
		List<String> nodeList = new ArrayList<String>(nodes);
		
		return nodeList;
	}
	
	// List Change Event Support
	public interface NodeListChangeListener {
		public void nodeListChangeEvent();
	}
	
	public void addNodeListChangeListener(NodeListChangeListener listener) {
    	listeners.add(listener);
    }

    public void removeNodeListChangeListener(NodeListChangeListener listener) {
    	listeners.remove(listener);
    }
	
	private void fireNodeListChanged() {
		for (NodeListChangeListener listener : listeners) {
			listener.nodeListChangeEvent();
		}
	}
}
