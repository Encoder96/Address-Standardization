package AStandard;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class BK {
	String val;
	HashMap<Integer, BK> child;
	public BK() {
		val = "";
		child = new HashMap();
	}
	public BK(String s) {
		val = s;
		child = new HashMap();
	}
}

class BKTree {
	private BK root;
	private EditDistance ed;
	
	ArrayList<String> arr = new ArrayList();
	public BKTree() {
		root = new BK();
		ed = new EditDistance();
	}

	public void insert(String s) {
		if(root.val == "")
			root.val = s;
		else {
			BK tmp = root;			
			int dist = ed.edit(tmp.val, s);

			while(tmp.child.size()>0 && tmp.child.containsKey(dist)) {
				tmp = tmp.child.get(dist);
				dist = ed.edit(tmp.val, s);
			}
			tmp.child.put(dist, new BK(s));
		}
	}
	public void getList(BK root, ArrayList<String> arr, String s) {
		int dist = ed.edit(root.val, s);
		if(dist <= 3)
			arr.add(root.val);
		int l_limit = Math.max(0, dist-4);
		int u_limit = dist+4;
		for(int i=l_limit; i<=u_limit; i++) {
			if(root.child.containsKey(i))
				getList(root.child.get(i), arr, s);
		}
	}

	public ArrayList<String> find(String s) {
		getList(root, arr, s);
		return arr;
	}
}

class EditDistance {
	int dp[][];
	public EditDistance() {
		dp = new int[300][300];
	}
	public int edit(String s1, String s2) {
		int l1=s1.length(), l2=s2.length();
		for(int i=0; i<=l1; i++)
			dp[i][0] = i;
		for(int j=0; j<=l2; j++)
			dp[0][j] = j;

		for(int i=1; i<=l1; i++) {
			for(int j=1; j<=l2; j++){
				if(s1.charAt(i-1) != s2.charAt(j-1))
					dp[i][j] = Math.min(dp[i-1][j], Math.min(dp[i][j-1], dp[i-1][j-1])) + 1;
				else
					dp[i][j] = dp[i-1][j-1]; 
			}
		}
		return dp[l1][l2];
	}
}

class Continental<T, V> {
	HashMap<T, ArrayList<V>> gmap;
	public Continental() {
		gmap = new HashMap<T, ArrayList<V>>();
	}

	public void insert(T state, ArrayList<V> city) {
		gmap.put(state, city);
	}
}

public class Address {
	private static Continental c = new Continental();
	private static BKTree bk = new BKTree();

	public static ArrayList<String> addressStandardization(String city, String state) {
		city = city.replaceAll("[^A-Za-z//s+//-]", "");
		city = city.toLowerCase();
		ArrayList<String> arr = (ArrayList<String>)c.gmap.get(state);
		for(String i : arr) 
			bk.insert((String)i);
		ArrayList<String> matches = bk.find(city);
		return matches;
	}

	public static void main(String[] args) throws FileNotFoundException, ParseException, IOException{
		ArrayList<String> tmp = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);		
		String city = sc.nextLine();
		String state = sc.next();	
		state = state.toUpperCase();
		
		File file = new File("F:\\spring\\State.json");
		FileReader fr = new FileReader(file);
		JSONParser jp = new JSONParser();
		JSONObject jb = (JSONObject)jp.parse(fr);

		for(Object obj : jb.keySet()) {
			String s = (String)obj;
			s = s.toUpperCase();
			if(s.contentEquals(state)) {			
				ArrayList<String> arr = (ArrayList<String>)jb.get((String)obj);
				int n = arr.size();
				for(int i=0; i<n; i++)
					arr.set(i, arr.get(i).toLowerCase());
				c.insert((String)obj, arr);
				break;
			}
		}
//
//
//
//		if(state.equals("NY")) {
//			tmp.add("beacon");tmp.add("albania");tmp.add("fulton");		
//			c.insert("NY", tmp);
//		}
//		else
//		if(state == "NM") {
//			tmp.add("santa fe");tmp.add("taos");tmp.add("roswell");		
//			c.insert("NM", tmp);		
//		}
		System.out.println(addressStandardization(city, state));		
	}
}
