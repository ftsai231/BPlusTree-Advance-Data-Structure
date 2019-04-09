import java.util.*;
import java.util.Map.Entry;
import  java.util.AbstractMap.SimpleEntry;  

public class TreeNode {
	public boolean isLeaf;
	public boolean isRoot;
	public TreeNode parent;
	public TreeNode prev;
	public TreeNode next;
	public List<Entry<Integer, Double>> entries;
	public List<TreeNode> children;
	
	public TreeNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		entries = new ArrayList();
		if(!isLeaf){
			children = new ArrayList();
		}
	}
	
	public TreeNode(boolean isLeaf, boolean isRoot) {
		this(isLeaf);
		this.isRoot = isRoot;
	}
	
	public Object search(Integer key) {
		if(isLeaf) {
			for(Entry<Integer, Double> en : entries) {
				//found the target value
				if(en.getKey().compareTo(key)==0) {
					return en.getValue();
				}
			}
			return null;
		}
		else {
			if(key.compareTo(entries.get(0).getKey())<=0) {
				//smaller => go to the first node do the same thing
				return children.get(0).search(key);
			}
			else if(key.compareTo(entries.get(entries.size()-1).getKey())>=0) {
				//bigger => go to the last node do the same thing
				return children.get(children.size()-1).search(key);
			}
			else {
				for(int i=0;i<entries.size();i++) {
					if(entries.get(i).getKey().compareTo(key)<=0 && entries.get(i+1).getKey().compareTo(key)>0) {
						return children.get(i).search(key);
					}
					return children.get(i).search(key);
				}
			}
		}
		return null;
	}
	
	public List<Object> search(Integer key1, Integer key2) {
		if(isLeaf) {
			List<Object> list = new ArrayList();
			for(Entry<Integer, Double> en : entries) {
				//found the target value
				if(en.getKey().compareTo(key1)>=0 && en.getKey().compareTo(key2)<=0) {
					list.add(en.getValue());
				}
			}
			return list;
		}
		else {
			if(key1.compareTo(entries.get(0).getKey())<0) {
				//smaller => go to the first node do the same thing
				return children.get(0).search(key1, key2);
			}
			else if(key2.compareTo(entries.get(entries.size()-1).getKey())>0) {
				//bigger => go to the last node do the same thing
				return children.get(children.size()-1).search(key1, key2);
			}
			else {
				for(int i=0;i<entries.size();i++) {
					if(entries.get(i).getKey().compareTo(key1)<0 && entries.get(i+1).getKey().compareTo(key2)>0) {
						return children.get(i).search(key1, key2);
					}
					return children.get(i).search(key1, key2);
				}
			}
		}
		return null;
	}

	
	public void insertUpdate(Integer key, Double obj, BPlusTree tree) {
		if(isLeaf) {
			//the case that don't need to split
			if(contains(key) || entries.size() < tree.getOrder()) {
				insertUpdate(key, obj);
				if(parent != null) {
					//update parent node
					parent.insertUpdate(tree);
				}
			}
			
			//need to split
			else {
				//split the node to left and right
				TreeNode left = new TreeNode(true);
				TreeNode right = new TreeNode(true);
				
				//set the linked list
				if(prev != null) {
					prev.setNext(left);
					left.setPrev(prev);
				}
				if(next != null) {
					right.setNext(next);
					next.setNext(right);
				}
				if(prev==null) {
					tree.setHead(left);
				}
				
				left.setNext(right);
				right.setPrev(left);
				
				//because it split, the curr node goes up, 
				//and there's no linked list except the leaves
				prev = null;
				next = null;
				
				//the length of the two key
				int order = tree.getOrder();
				int leftSize = (order + 1) / 2 + (order + 1) % 2;
				int rightSize = (order + 1) / 2;
				
				insertUpdate(key, obj);
				for(int i=0;i<leftSize;i++) {
					left.getEntries().add(entries.get(i));
				}
				
				for(int i=0;i<rightSize;i++) {
					right.getEntries().add(entries.get(leftSize + i));
				}
				
				//if it not the root
				if(parent!=null) {
					int idx = parent.getChildren().indexOf(this);
					parent.getChildren().remove(this);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(idx, left);
					parent.getChildren().add(idx + 1, right);
					
					//only the leaves will have entries
					//otherwise, it will only have key (maybe)
					setEntries(null);
					setChildren(null);
				}
				else {
					isRoot = false;
					TreeNode parent = new TreeNode(false, true);
					tree.setRoot(parent);
					left.setParent(parent);
					right.setParent(parent);
					parent.getChildren().add(left);
					parent.getChildren().add(right);
					setEntries(null);
					setChildren(null);
					
					//update the root
					parent.insertUpdate(tree);
				}
				
				
			}
		}
		
		//if the node is not a root
		else {
			//if key is smaller than the leftmost node, 
			//search from the first node
			if(key.compareTo(entries.get(0).getKey()) <= 0) {
				children.get(0).insertUpdate(key, obj, tree);
			}
			//if key is greater than the rightmost node
			//search from the last node
			else if(key.compareTo(entries.get(entries.size()-1).getKey())>=0) {
				children.get(children.size() - 1).insertUpdate(key, obj, tree);
			}
			//the value of key is in the middle
			else {
				for(int i=0;i<entries.size();i++) {
					if(entries.get(i).getKey().compareTo(key)<=0
							&& entries.get(i+1).getKey().compareTo(key)>0) {
						children.get(i).insertUpdate(key, obj, tree);
						break;
					}
				}
			}
		}
	}
	
	
	public void insertUpdate(Integer key, Double obj) {
		Entry<Integer, Double> entry = new SimpleEntry<Integer, Double>(key, obj);
		
		//if the size of the list is 0, insert
		if(entries.size()==0) {
			entries.add(entry);
			return;
		}
		
		//otherwise, loop the while list
		for(int i=0;i<entries.size();i++) {
			//if the key exists, then update!
			if(entries.get(i).getKey().compareTo(key)==0) {
				entries.get(i).setValue(obj);
				return;
			}
			//otherwise, insert
			else if(entries.get(i).getKey().compareTo(key) > 0) {
				if(i==0) {
					entries.add(0, entry);
					return;
				}
				else {
					entries.add(i, entry);
					return;
				}
			}
		}
		//add to the end
		entries.add(entries.size(), entry);
	}
	
	public void insertUpdate(BPlusTree tree) {
		validate(this, tree);
		
		//if # of children exceed the order, split
		if(children.size()>tree.getOrder()) {
			// split into left node and right node
			TreeNode left = new TreeNode(false);
			TreeNode right = new TreeNode(false);
			
			int order = tree.getOrder();
			int leftSize = (order + 1) / 2 + (order + 1) % 2;
			int rightSize = (order + 1) / 2;
			
			//copy children nodes to the new node and update the key
			for(int i=0;i<leftSize;i++) {
				left.getChildren().add(children.get(i));
				left.getEntries().add(new SimpleEntry(children.get(i).getEntries().get(0).getKey(), null));
				children.get(i).setParent(left);
			}
			for(int i=0;i<rightSize;i++) {
				right.getChildren().add(children.get(leftSize+i));
				right.getEntries().add(new SimpleEntry(children.get(leftSize+i).getEntries().get(0).getKey(), null));
				children.get(leftSize + i).setParent(right); 
			}
			
			//if it is not root
			if(parent!=null) {
				int idx = parent.getChildren().indexOf(this);
				parent.getChildren().remove(this);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(idx, left);
				parent.getChildren().add(idx+1, right);
				setEntries(null);
				setChildren(null);
				
				//update the key in parent node
				parent.insertUpdate(tree);
				setParent(null);
				
			}
			
			//if it is root
			else {
				//create a parent node and put the element left and right as the children
				isRoot = false;
				TreeNode parent = new TreeNode(false, true);
				tree.setRoot(parent);
				left.setParent(parent);
				right.setParent(parent);
				parent.getChildren().add(left);
				parent.getChildren().add(right);
				setEntries(null);
				setChildren(null);
				
				parent.insertUpdate(tree);	
			}
		}
	}

	private static void validate(TreeNode node, BPlusTree tree) {
		// TODO Auto-generated method stub
		//if the list of node equals children list
		if(node.getEntries().size()==node.getChildren().size()) {
			for(int i=0;i<node.getEntries().size();i++) {
				Integer key = node.getChildren().get(i).getEntries().get(0).getKey();
				if(node.getEntries().get(i).getKey().compareTo(key)!=0) {
					node.getEntries().remove(i);
					node.getEntries().add(i, new SimpleEntry(key, null));
					if(!node.isRoot()) {
						validate(node.getParent(), tree);
					}
				}
			}
		}
		
		//if M > # of children > M/2 and # greater than 2
		else if(node.isRoot() && (node.getChildren().size()>=2 || node.getChildren().size()>=tree.getOrder()/2)
				 && node.getChildren().size() <= tree.getOrder() && node.getChildren().size()>=2) {
			node.getEntries().clear();
			for(int i=0;i<node.getChildren().size();i++) {
				Integer key = node.getChildren().get(i).getEntries().get(0).getKey();
				node.getEntries().add(new SimpleEntry(key, null));
				if(!node.isRoot()) {
					validate(node.getParent(), tree);
				}
			}
		}			
	}
	
	public void remove(Integer key, BPlusTree tree) {
		if(isLeaf) {
			//if it doesnt have the key, return
			if(!contains(key)) {
				return;
			}
			//if it is both leaf and root, just delete
			if(isRoot) {
				remove(key);
			}
			else {
				//if #key  > M / 2, delete
				if(entries.size()>tree.getOrder()/2 && entries.size()>2) {
					remove(key);
				}
				else {
					//if the #key < M/2 and #prev-key > M/2, borrow
					if(prev!=null && prev.getEntries().size()>tree.getOrder()/2
							&& prev.getEntries().size()>2
							&& prev.getParent()==parent) {
						int size = prev.getEntries().size();
						Entry<Integer, Double> entry = prev.getEntries().get(size - 1);
						prev.getEntries().remove(entry);
						//add to the first place
						entries.add(0, entry);
						remove(key);
					}
					
					//if #key < M/2 and #next-key > M/2
					else if(next != null && next.getEntries().size() > tree.getOrder()/2
							&& next.getEntries().size() > 2
							&& next.getParent()==parent) { 
						Entry<Integer, Double> entry = next.getEntries().get(0);
						next.getEntries().remove(entry);
						
						//add to the end
						entries.add(entry);
						remove(key);
					}
					
					//otherwise, we need to combine nodes
					else {
						if(prev!=null && (prev.getEntries().size()<=tree.getOrder()/2
								|| prev.getEntries().size()<=2) 
								&& prev.getParent()==parent) {
							int size = prev.getEntries().size() - 1;
							for(int i=size;i>=0;i--) {
								entries.add(0, prev.getEntries().get(i));
							}
							remove(key);
							prev.setParent(null);
							prev.setEntries(null);
							parent.getChildren().remove(prev);
							
							//update linked list
							if(prev.getPrev()!=null) {
								TreeNode temp = prev;
								temp.getPrev().setNext(this);
								prev = temp.getPrev();
								temp.setPrev(null);
								temp.setNext(null);
							}
							else {
								tree.setHead(this);
								prev.setNext(null);
								prev = null;
							}
						}
						
						//merge from the next node
						else if(next!=null 
								&& (next.getEntries().size()<=tree.getOrder()/2 || next.getEntries().size()<=2)
								&& next.getParent()==parent) {
							for(int i=0;i<next.getEntries().size();i++) {
								entries.add(next.getEntries().get(i));
							}
							remove(key);
							next.setParent(null);
							next.setEntries(null);
							parent.getChildren().remove(next);
							
							//update the list
							if(next.getNext() != null) {
								TreeNode temp = next;
								temp.getNext().setPrev(this);
								next = temp.getNext();
								temp.setPrev(null);
								temp.setNext(null);
							}
							else {
								next.setPrev(null);
								next = null;
							}
						}
					}
				}
				parent.removeUpdate(tree);
			}
		}
		else {
			//if key less than leftmost key, search from the first node
			
			if(key.compareTo(entries.get(0).getKey())<=0) {
				children.get(0).remove(key, tree);
			}
			
			//if key > rightmost key, search from the last node
			else if(key.compareTo(entries.get(entries.size()-1).getKey())>=0) {
				children.get(children.size()-1).remove(key, tree);
			}
			
			//otherwise, search the middle
			else {
				for(int i=0;i<entries.size();i++) {
					if(entries.get(i).getKey().compareTo(key)<=0 
							&& entries.get(i+1).getKey().compareTo(key)>0) {
						children.get(i).remove(key, tree);
						break;
					}
				}
			}
		}
	}
	
	public void remove(Integer key) {
		int idx = -1;
		for(int i=0;i<entries.size();i++) {
			if(entries.get(i).getKey().compareTo(key)==0) {
				idx = i;
				break;
			}
		}
		if(idx != -1) {
			entries.remove(idx);
		}
	}
	
	
	
	

	public void removeUpdate(BPlusTree tree) {
		validate(this, tree);
		
		//if #node < M / 2 or < 2, merge them;
		if(children.size()<tree.getOrder()/2 || children.size()<2) {
			if(isRoot) {
				//if it is root and #children > 2, fine!
				if(children.size()>=2) {
					return;
				}
				else {
					TreeNode root = children.get(0);
					tree.setRoot(root);
					root.setParent(null);
					root.setRoot(true);
					setEntries(null);
					setChildren(null);
				}
			}
			else {
				//calculate prev and next node
				int currIdx = parent.getChildren().indexOf(this);
				int prevIdx = currIdx - 1;
				int nextIdx = currIdx + 1;
				TreeNode prev = null;
				TreeNode next = null;
				if(prevIdx>=0) {
					prev = parent.getChildren().get(prevIdx);
				}
				if(nextIdx<parent.getChildren().size()) {
					next = parent.getChildren().get(nextIdx);
				}
				
				//if #prev.children > M/2 and > 2, borrow
				if(prev!=null && prev.getChildren().size() > tree.getOrder()/2
						&& prev.getChildren().size() > 2) {
					//last sub-node of the next node moving to this node
					int idx = prev.getChildren().size() - 1;
					TreeNode borrow = prev.getChildren().get(idx);
					prev.getChildren().remove(idx);
					borrow.setParent(this);
					children.add(0, borrow);
					validate(prev, tree);
					validate(this, tree);
					parent.removeUpdate(tree);	
				}
				
				//if #sub-node of next node > M / 2 and > 2, borrow
				else if(next != null
						&& next.getChildren().size()>tree.getOrder()/2
						&& next.getChildren().size()>2) {
					//first sub-node of the next node move to the last pos
					TreeNode borrow = next.getChildren().get(0);
					next.getChildren().remove(0);
					borrow.setParent(this);
					children.add(borrow);
					validate(next, tree);
					validate(this, tree);
					parent.removeUpdate(tree);
				}
				//otherwise, merge two nodes
				else {
					if(prev!=null && 
							(prev.getChildren().size()<=tree.getOrder()/2 || prev.getChildren().size()<=2)) {
						int size = prev.getChildren().size() - 1;
						for(int i=size;i>=0;i--) {
							TreeNode child = prev.getChildren().get(i);
							children.add(0, child);
							child.setParent(this);
						}
						
						prev.setChildren(null);
						prev.setEntries(null);
						prev.setParent(null);
						parent.getChildren().remove(prev);
						validate(this, tree);
						parent.removeUpdate(tree);
					}
					else if(next!=null 
							&&(next.getChildren().size()<=tree.getOrder()/2 || next.getChildren().size()<=2)) {
						for(int i=0;i<next.getChildren().size();i++) {
							TreeNode child = next.getChildren().get(i);
							children.add(child);
							child.setParent(this);
						}
						next.setChildren(null);
						next.setEntries(null);
						next.setParent(null);
						parent.getChildren().remove(next);
						validate(this, tree);
						parent.removeUpdate(tree);
					}
				}	
			}
		}
	}
	
	
	
	public void  setRoot( boolean  isRoot) {   
        this .isRoot = isRoot;  
    }  

	private boolean isRoot() {
		// TODO Auto-generated method stub
		return isRoot;
	}

	private TreeNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	private List<TreeNode> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}
	
	private void setParent(TreeNode parent) {
		// TODO Auto-generated method stub
		this.parent = parent;
	}
	
	public List<Entry<Integer, Double>> getEntries(){
		return entries;
	}
	
	public void setEntries(List<Entry<Integer, Double>> entries) {
		this.entries = entries;
	}
	
	private void setChildren(List<TreeNode> children) {
		// TODO Auto-generated method stub
		this.children = children;
	}
	
	public void setNext(TreeNode next) {
		this.next = next;
	}
	
	public TreeNode getNext() {
		return next;
	}
	
	public void setPrev(TreeNode prev) {
		this.prev = prev;
	}
	
	public TreeNode getPrev() {
		return prev;
	}

	public boolean contains(Integer key) {   
//		System.out.println(key);
        for  (Entry<Integer, Double> entry : entries) {  
            if  (entry.getKey().compareTo(key) ==  0 ) {  
                return true ;   
            }  
        }  
        return false;
    }  
}
