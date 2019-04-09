import java.util.*;

public class BPlusTree {
	public TreeNode root;
	
	public int order;
	
	//the firest node in the leaf
	public TreeNode head;
	
	public TreeNode getHead() {
		return head;
	}
	
	public void setHead(TreeNode head) {
		this.head = head;
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public Object search(Comparable key) {
		return root.search(key);
	}
	
	public List<Object> search(Comparable key1, Comparable key2) {
		return root.search(key1, key2);
	}
	
	public void remove(Comparable key) {
		root.remove(key, this);
	}
	
	public void insertUpdate(Comparable key, Object obj) {
		root.insertUpdate(key, obj, this);
	}
	
	public BPlusTree(int order) {
		if(order < 3) {
			System.out.println("The order needs to be greater than 3!");
			System.exit(0);
		}
		this.order = order;
		root=  new TreeNode(true, true);
		head = root;
	}
	
	
	
	
	
}
