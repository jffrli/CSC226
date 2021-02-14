/* PrimVsKruskal.java
   CSC 226 - Summer 2020
   Assignment 2 - Prim MST versus Kruskal MST Template
   
   The file includes the "import edu.princeton.cs.algs4.*;" so that yo can use
   any of the code in the algs4.jar file. You should be able to compile your program
   with the command
   
	javac -cp .;algs4.jar PrimVsKruskal.java
	
   To conveniently test the algorithm with a large input, create a text file
   containing a test graphs (in the format described below) and run
   the program with
   
	java -cp .;algs4.jar PrimVsKruskal file.txt
	
   where file.txt is replaced by the name of the text file.
   
   The input consists of a graph (as an adjacency matrix) in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry G[i][j] >= 0.0 of the adjacency matrix gives the weight (as type double) of the edge from 
   vertex i to vertex j (if G[i][j] is 0.0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that G[i][j]
   is always equal to G[j][i].
*/

 import edu.princeton.cs.algs4.*;
 import java.util.Scanner;
 import java.io.File;

//Do not change the name of the PrimVsKruskal class
public class PrimVsKruskal{

	/* one step of prim's algorithm
		run while
			pq is not empty
		returns true if eds triggers
	*/
	static boolean primstep(IndexMinPQ<Double> pq,
							boolean[] marked,
							Edge[] edgeTo,
							double[] dist,
							double[][] G,
							int n,

							//early detection system
							LinearProbingHashST<Edge,Integer> klinked,
							WeightedQuickUnionUF kuf)
	{
		int v = pq.delMin();
		marked[v] = true;

		//early detection system
		//this possibly will never trigger
		if (edgeTo[v] != null) {
			Edge ed = edgeTo[v];
			int w = ed.other(v);
			if (kuf.find(v) == kuf.find(w)) {
				if (klinked.contains(ed)) { //makes a cycle (this might be impossible)

					//System.out.println(ed);

					return true;
				}
			}
		}

		for (int i = 0; i < n; i++) {
			if (marked[i]) continue;
			if (G[v][i] > 0 && G[v][i] < dist[i]) {
				edgeTo[i] = new Edge(v,i,G[v][i]);
				dist[i] = G[v][i];
				if (pq.contains(i)) pq.changeKey(i,dist[i]);
				else pq.insert(i,dist[i]);
			}
		}
		return false;
	}

	/*
	Returns a queue given an array of edges
	Used by prim's to make an MST
	*/
	static Queue<Edge> edges(Edge[] edgeTo, int n) {
		Queue<Edge> mst = new Queue<Edge>();
		for (int i = 0; i < n; i++) {
			Edge e = edgeTo[i];
			if (e != null) {
				mst.enqueue(e);
			}
		}
		return mst;
	}

	/* one step of kruskal's algorithm
		run while
			pq is not empty
			mst size < n-1
		returns true if eds triggers
	*/
	static boolean kruskalstep(Queue<Edge> mst,
								MinPQ<Edge> pq,
								WeightedQuickUnionUF uf,

								//early detection system
								LinearProbingHashST<Edge,Integer> linked,
								boolean[] pmarked,
								Edge[] pedge)
	{
		Edge e = pq.delMin();
		int v = e.either(), w = e.other(v);
		if (uf.find(v) != uf.find(w)) {
			uf.union(v,w);
			mst.enqueue(e);

			linked.put(e,0);
			
			//early detection system
			if (pmarked[v] && pmarked[w]) { //only check already checked nodes
				for (int i = 0; i < pedge.length; i++) {
					try {
						pedge[i].other(v);
						pedge[i].other(w);

					} catch (Exception ex) {
						continue;
					}
					return false; //doesn't get to this line if the edges aren't the same
				}
				//System.out.println(e);
				return true; //if the same edge doesn't exist in the prim mst, it will make a cycle
			}
		}

		return false;
	}

	/* Note: ruins the queue and array after this
		Checks if an array and a queue contain the same elements
		Assumes array and queue are of same size
	*/
	static boolean arrqcomp(Queue<Edge> q, Edge[] a, int n) {
		while (!q.isEmpty()) {
			Edge e = q.dequeue();
			int v = e.either(), w = e.other(v);
			boolean match = false;
			for (int i = 0; i < n; i++) {
				if (a[i] == null) continue;
				try {
					a[i].other(v);
					a[i].other(w);
				} catch (Exception ex) {
					continue;
				}
				
				//weights will be the same because it's the same edge
				match = true;
				break;
				
			}
			if (!match) {
				return false;
			}
		}
		return true;
	}

	/* very early detection system
		if the weights are distinct, then the graph must have a unique MST
		returns true if weights are distinct
	*/
	static boolean veds(double[][] G, int n) {
		LinearProbingHashST<Double,Integer> h = new LinearProbingHashST<Double,Integer>(n);
		for (int i = 0; i < n; i++) {
			for (int j=i+1;j<n;j++) {
				if (G[i][j] > 0.0){
					if (h.contains(G[i][j])) {
						return false;
					}
					h.put(G[i][j],0);
				}
			}
		}
		//System.out.println("VEDS triggered");
		return true;
	}

	/*early detection system
	check if adding an edge pick by 1 algorith would make a cycle in the other
		prims: the edge is between 2 marked indices and is not the edge between them
		kruskals: use union find to check if both are in the same set, make sure there isn't an edge between them in the queue
	*/

	/* PrimVsKruskal(G)
		Given an adjacency matrix for connected graph G, with no self-loops or parallel edges,
		determine if the minimum spanning tree of G found by Prim's algorithm is equal to 
		the minimum spanning tree of G found by Kruskal's algorithm.
		
		If G[i][j] == 0.0, there is no edge between vertex i and vertex j
		If G[i][j] > 0.0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	static boolean PrimVsKruskal(double[][] G){
		int n = G.length;

		/* Build the MST by Prim's and the MST by Kruskal's */
		/* (You may add extra methods if necessary) */
		
		/* ... Your code here ... */

		//very early detection system
		//if (veds(G,n)) return true;

		//prim setup
		IndexMinPQ<Double> primpq = new IndexMinPQ<Double>(n);
		Edge[] primto = new Edge[n];
		boolean[] primmarked = new boolean[n];
		double[] primdist = new double[n];
		for (int i = 0; i < n; i++) {
			primdist[i] = Double.POSITIVE_INFINITY;
		}
		primdist[0] = 0.0;
		primpq.insert(0,0.0);
		
		//Kruskal's setup
		MinPQ<Edge> kruskalpq = new MinPQ<Edge>();
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n;j++) {
				if (G[i][j] > 0.0) kruskalpq.insert(new Edge(i,j,G[i][j]));
			}
		}
		Queue<Edge> kruskalmst = new Queue<Edge>();
		WeightedQuickUnionUF kruskaluf = new WeightedQuickUnionUF(n);
		LinearProbingHashST<Edge,Integer> kruskallinks = new LinearProbingHashST<Edge,Integer>();

		//Just runs P|| 
		/*
		while (!primpq.isEmpty()) {) {
	
		}
			primstep(primpq,primmarked,primto,primdist,G,n,kruskallinks,kruskaluf;
		}
		//System.out.println(edges(primto,n));
		*/
		//just runs Kruskal
		/*
		while (!kruskalpq.isEmpty() && kruskalmst.size() < n-1) {
			kruskalstep(kruskalmst,kruskalpq,kruskaluf, krustallinks, primmarked, primto);
		}
		//System.out.println(kruskalmst);
		*/

		boolean pg = true, kg = true;
		while (pg || kg) {
			if (pg) {
				if (primpq.isEmpty()) pg = false;
				else if (primstep(primpq,primmarked,primto,primdist,G,n,kruskallinks,kruskaluf)) {
					/*
					System.out.println("Prims eds triggered");
					System.out.println("Prims: " + edges(primto,n));
					System.out.println("Kruskals: " + kruskalmst);
					*/
					return false;
				}
			}
			if (kg) {
				if (kruskalpq.isEmpty() || kruskalmst.size() >= n-1) kg = false;
				else if (kruskalstep(kruskalmst,kruskalpq,kruskaluf, kruskallinks, primmarked, primto)) {
					/*
					System.out.println("Kruskals eds triggered");
					System.out.println("Prims: " + edges(primto,n));
					System.out.println("Kruskals: " + kruskalmst);
					*/
					return false;
				}
			}
		}
		
		
		//System.out.println("Prims: " + edges(primto,n));
		//System.out.println("Kruskals: " + kruskalmst);
		
		/* Determine if the MST by Prim equals the MST by Kruskal */
		boolean pvk = arrqcomp(kruskalmst, primto, n);
		/* ... Your code here ... */

		return pvk;	
	}


	/* main()
	   Contains code to test the PrimVsKruskal function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below. 
	*/
	public static void main(String[] args) {
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int n = s.nextInt();
		double[][] G = new double[n][n];
		int valuesRead = 0;
		for (int i = 0; i < n && s.hasNextDouble(); i++){
			for (int j = 0; j < n && s.hasNextDouble(); j++){
				G[i][j] = s.nextDouble();
				if (i == j && G[i][j] != 0.0) {
					System.out.printf("Adjacency matrix contains self-loops.\n");
					return;
				}
				if (G[i][j] < 0.0) {
					System.out.printf("Adjacency matrix contains negative values.\n");
					return;
				}
				if (j < i && G[i][j] != G[j][i]) {
					System.out.printf("Adjacency matrix is not symmetric.\n");
					return;
				}
				valuesRead++;
			}
		}
		
		if (valuesRead < n*n){
			System.out.printf("Adjacency matrix for the graph contains too few values.\n");
			return;
		}	
		
        boolean pvk = PrimVsKruskal(G);
        System.out.printf("Does Prim MST = Kruskal MST? %b\n", pvk);
    }
}
