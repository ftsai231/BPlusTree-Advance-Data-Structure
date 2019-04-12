import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Execute {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		File reader = new File("./" + args[0]);
		BufferedReader br = new BufferedReader(new FileReader(reader));
		File writer = new File("./output_file.txt");
//		BufferedWriter wr = new BufferedWriter(new FileWriter(writer));
		String str = null;
		int m = 0;
		BPlusTree tree = null;
		str = br.readLine();
		str = str.substring(str.indexOf("(")+1, str.indexOf(")"));
		m = Integer.parseInt(str.trim());
		tree = new BPlusTree(m);
		try(BufferedWriter wr = new BufferedWriter(new FileWriter(writer))){
			while((str = br.readLine()) != null) {
				
				//search(key)
				if(str.startsWith("Search") && !str.contains(",")) {
					Integer key;
					String str2 = "";
					for(int i=0;i<str.length();i++) {
						if(Character.isDigit(str.charAt(i))) {
							str2 += str.charAt(i);
						}
					}
					key = Integer.parseInt(str2.trim());
					System.out.println(tree.search(key));
					wr.write(String.valueOf(tree.search(key)));
					wr.newLine();
					
				}
				
				//Insert(key, value)
				else if(str.startsWith("Insert")) {
					System.out.println("Insert");
					Integer key;
					double value;
					String str3 = str.substring(str.indexOf("(")+1,str.indexOf(",")); 
	    			key = Integer.parseInt(str3.trim());
	    			String strVal = str.substring(str.indexOf(",")+1,str.indexOf(")")).trim();
	    			value = Double.parseDouble(strVal);
	    			tree.insertUpdate(key, value);

				}
				
				//Delete(key);
				else if(str.startsWith("Delete")) {
					System.out.println("Delete");
					String strDelete = str.substring(str.indexOf("(")+1,str.indexOf(")"));
					Integer key = Integer.parseInt(strDelete);
//					System.out.println(key);
					tree.remove(key);
				}
				
				//Search(key1, key2)
				else {
					Integer key1, key2;
					String str4 = str.substring(str.indexOf("(")+1,str.indexOf(","));
					String str5 = str.substring(str.indexOf(",")+1,str.indexOf(")"));
					key1 = Integer.parseInt(str4.trim());
					key2 = Integer.parseInt(str5.trim());
					List<Object> list = tree.search(key1, key2);
					System.out.println(list);
					wr.write(list.toString());
					wr.newLine();
					
				}
			}
		}
		
	}

}
