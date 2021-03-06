//Isaac Zhuan Jian Lee
//45526249
package pageRankOnTrees;
import java.util.*;
import java.io.*;

class WebPage {
	protected String title;
	protected Vector<WebPage> links; 	// A list of links.
	
	public WebPage() { links= null; }

	public WebPage(String el) {
		title= el; 
		links= new Vector<WebPage>();
	}
	
	public WebPage(String el, Vector<WebPage> ln) {
		title = el;
		links= ln;
	}
	public void visit() { System.out.print(title + " "); }
	public String toString() { return "" + title; }
}

class CentillionIndexing { // This defines the databases for computing importances and ranking.
							// Each address should appear exactly once in the importance and baseImportance maps.
	Map<WebPage, Double> importance= new HashMap<WebPage, Double>(); // Importance.
	Map<WebPage, Double> baseImportance= new HashMap<WebPage, Double>(); // Base importance (initially from input file).

	CentillionIndexing() {
		importance= new HashMap<WebPage, Double>();
		baseImportance= new HashMap<WebPage, Double>();
	}

	Double importanceLookUp(WebPage s ) { // Returns the calculated importance assigned to s, if s is in importance.
		if (importance.get(s)!=null) return importance.get(s);
		else return -1.0;
	}
	
	Double baseLookUp(WebPage s ) { // Returns the base importance according to baseImportance. 
		if (baseImportance.get(s)!=null) return baseImportance.get(s);
		else return -1.0;
	}

	void changeBaseImportance(WebPage q, Double d) { //TODO
		// Postcondition: The baseImportance at q is updated to d.
		if (baseImportance.containsKey(q)) {	
			baseImportance.replace(q, d);		//It will replace or overwrite the old data with the new data
		}
		
		return;	//Otherwise, it will return void
	}

	WebPage getAddress(String tle) { // Returns the address of the webpage with title tle stored in baseImportance.
		Set<WebPage> keys= baseImportance.keySet();
		Iterator<WebPage> addresses= keys.iterator();
		WebPage myAddress= null;

	for (;addresses.hasNext();) {
		myAddress= addresses.next();
		if (myAddress.title.equals(tle)) break;
	}  
	if (myAddress== null) return null;
	if (myAddress.equals(tle)) return myAddress;
	return null;
	} 
}

public class WorldWideWeb { // This contains the functionality for setting up the Web.
	protected  WebPage root= null;	 // The "real" World Wide Web is a tree.
	protected  CentillionIndexing pages; // The Centillion database used for ranking.
	
	public WorldWideWeb() { pages= new CentillionIndexing(); }

	public void setWorldWideWeb(String treeFile) { // This initialises the tree root, and pages.baseImportance.
		pages= new CentillionIndexing(); // Sets everything to empty.
		Vector<Vector<String>> vv= new Vector<Vector<String>>();
		String line;
		String[] aLine;
		Vector<String> vLine;

		try { // Reads the text file for tree links and base importances.
			BufferedReader in= new BufferedReader (new FileReader(treeFile));
			while (true) {
				line= in.readLine();
				if (line == null) break;
				aLine= line.split(";");
				vLine= new Vector<String>();
				for (int i= 0; i<aLine.length; i++) vLine.add(aLine[i].trim());
				vv.add(vLine);
			}
		}	catch(FileNotFoundException ee) { System.out.println("File not found"); }
			catch(IOException e) { e.printStackTrace(); }

		root= makeTree(vv);		 // Calls MakeTree with the given data: makes the tree and sets the base importances.
	}

	public	WebPage makeTree(Vector<Vector<String>>p ) {
		// Pre-condition:	Assumes that we are given a list of : title; base importance; followed by its links per line;
		//					Moreover the first item in the list is the root;
		//					Except for the root title, all other titles appear at the beginning of their line =after= they
		//					have appeared as a reference to some other title.
		//					This simplification means that we can build a tree starting from the top and adding new links at
		//					leaves.
		// Post-condition: Returns the root of an explicit tree; adds the base importances to the current pages.baseImportances structure.

		// Add the root and its list of links
		WebPage tt= null;
		for (int i= 0; i< p.size(); i++) { // For each Vector in p...
			// Get the data for making a new node
			String xKey= (p.elementAt(i)).elementAt(0); // Format for the input is title, importance, links...
			String prePage= (p.elementAt(i)).elementAt(1);
			Double iPage= Double.parseDouble((p.elementAt(i)).elementAt(1));
			Vector<String> linkNames= p.elementAt(i);
			linkNames.remove(xKey);
			linkNames.remove(prePage);
			  
			// Now convert the vector of strings to a vector of General nodes
			Vector<WebPage> linkNodes= new Vector<WebPage>();
			for (int j= 0; j<linkNames.size(); j++) {
				WebPage gn= new WebPage(linkNames.elementAt(j)); // Here we know the title linkNames.elementAt(j) and its address -> add to the hashmap at this point
				linkNodes.addElement(gn);
			}
			// Find x in the tree			  
			if (tt==null) { tt= new WebPage(xKey); tt.links= linkNodes; pages.baseImportance.put(tt, iPage); }
			else {
				WebPage found= findInTree(tt, xKey);
				if (found!=null) { // If it's not there then discard, otherwise assign the link vectors.
					found.links= linkNodes;
					pages.baseImportance.put(found, iPage);
				}
			}
		}
		return tt;
	}

	public WebPage findInTree(WebPage t, String k) { // Returns the descendant of t with title k.
		if (t==null) return null;
		else {
			WebPage n= t;
			WebPage m= null;
			if (k.equals(n.title)) return n;
			else { 
				if (n.links!=null) {
					for (int i= 0; i< (n.links).size(); i++) {
						m= findInTree(n.links.elementAt(i), k); // Search the rest of the tree.
						if (m!=null) break;
				}
			}
		}
		return m;
		}
	}

	public int numNodes(WebPage t) {// TODO 
	// Precondition: None.
	// Postcondition:	Returns the total number of nodes in the tree t.
		if(t != null) {	//If t is null, it will return 0 (Otherwise, it will proceed to the for loop)
			int sum = 0;
			for(WebPage nodes: t.links) {
				sum += numNodes(nodes);
				}
			return sum + 1;	//This will return the total number of nodes including the root
		}
		return 0;
	}
	
	public int wwwSize() {
		return numNodes(root);
	}
	
	public WebPage removeNode(WebPage t, String k) { // TODO
	// Precondition: k is not null.
	// Postcondition: Returns the tree which is the same as t except that the node whose title is k is removed (as well as its subtree).
		if (t != null) {
			WebPage p = t; WebPage q = t;	//Initializing the variables
			
			if (!(k.equals(p.title))) {		//When k is not equal to p.title and if p.links are not null		
				if (p.links == null) {
				return null;	
				}
				
				else {
					for (int i = 0; i < (p.links).size(); i++) {
						q = removeNode(p.links.elementAt(i), k);	//Remove the element k
							if (q == null) {
								p.links.remove(p.links.elementAt(i));
								break;
						}
					}
				}		
				return t;
			}		
			return null;
		}
		return null;
	}
	
	public void reduceTree(String k) {
		root= removeNode(root,k);
	}
	
	public void callRanks (WebPage t, WebPage k) {	//Helper method use for calculateImportances
		if (!(k.title.equals(t.title))) {
			pages.importance.put(k, pages.baseImportance.get(k) + pages.importance.get(t)/t.links.size());	//The formula from Assignment 1
		}
		
		for (int i = 0; i < (k.links).size(); i++){
			if(t.links != null) {
				callRanks(k, k.links.elementAt(i));	//Recursive method
			}
		}
	}
	
	public void calculateImportances(WebPage t) { // TODO 
		// Precondition:	The tree model of the World Wide Web t has been initialised,
		//				 	AND the CentillionIndexing variable pages.baseImportances has been initialised with base importances
		// Postcondition: 	Set the CentillionIndexing variable pages.importances to take account of both the tree t and 
		//				 	the base importances given in pages.baseImportances according to the formula explained in the Assignment One Specs.
		if(t != null) {
			pages.importance.put(t, pages.baseImportance.get(t));
			callRanks(t,t); //Using callRanks helper method
		}
		
		return;
	}

	public void calculateTreeImportances() { // Calls the method calculateRanks(WebPage t) to calculate the rankings of the WebPages.
		calculateImportances(root);
	}


////////Below are the specification stubs for additional credit.



	public WebPage changeReference(WebPage r, WebPage q, WebPage s) {//TODO
		// Precondition: WebPage r in the current tree references WebPage q	 (i.e. r.links contains q)
		// Postcondition: WebPage r removes its reference to q; WebPage s adds a reference to q (Otherwise, it will return null)
		if(q != null) {	//Since WebPage r will always refer to q so therefore, we have to do a null check
			removeNode(r,q.title);	//Once WebPage q is not null, WebPage r will be removed
			s.links.add(q);	//It will add with WebPage q
		}
		
		return null;
	}

	void updateImportances(WebPage q, Double d) { //TODO
		// Postcondition:	Updates the pages.baseImportance of page q to be d AND
		//					Recalculates pages.importance based on this change. 
		//					Your implementation should be as efficient as possible.
		if (pages.baseImportance.containsKey(q)) {
			pages.baseImportance.replace(q, d); //Update baseImportance of q to d
		}
		
		calculateTreeImportances(); //Calling another function
	}

	Vector<WebPage> topTenRanked () {//TODO
		// Postcondition:	Returns a Vector of the top ten percent of the webpages in order of rank.
		//					Your implementation should be as efficient as possible.
		Vector<WebPage> top= new Vector<WebPage>();
		Set<WebPage> token = pages.importance.keySet();
		WebPage original = root;		//Initializing variables
		WebPage highest = null;
		WebPage oriWeb = null;
		
		int i = 0;
		while (i < numNodes(original)/10){	//Top ten percent
			Iterator<WebPage> names = token.iterator();
			Double high = 0.0;
			Double low = 0.0;
			i++;
			
			for(;names.hasNext();) {
				oriWeb= names.next();
				low = pages.importanceLookUp(oriWeb);	//Finding importance of oriWeb
			
				if (low > high) {
					high = low;
					highest = oriWeb;	//Finding the highest value through sorting
				}
			}
			top.add(highest);
			token.remove(highest);
		}
		return top;
	}
}

