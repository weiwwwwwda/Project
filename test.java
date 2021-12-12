package test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

class test {
	public static String[] title = {"Type", "Overall Type", "Arrangement", "Margin", "Margin Sub-Category", "Count"};    	// title 
	public static int m = 6;   											// number of column 
	public static int n = 8;   											// number of rows 
	
	
	public static void main(String[] args) {	//mySet, use numbers to represent different items 
		
		int[][] mySet = {											/* attributes */
															// 1		 2			3			4	
			/* Dogwood  */		{0, 0, 0, 0, 0, 2  },		// Simple    Opposite	Entire		X			
			/* Maple    */		{1, 0, 0, 1, 0, 2  },	 	// Simple	 Opposite	Lobed		X
			/* Ash      */		{2, 1, 0, 2, 0, 40  },		// Compound  Opposite	X			X
			/* Hickey   */		{3, 1, 1, 3, 0, 2  },		// Compound  X			Toothed	 	X
			/* Locust   */	    {4, 1, 2, 0, 0, 2  },	    // Compound  Alternate  Entire		X
			/* Cherry   */		{5, 0, 2, 3, 0, 2 },		// Simple	 Alternate	Toothed		X
			/* White Oak*/		{6, 0, 2, 1, 1, 100 },		// Simple	 Alternate	Lobed		Rounded
			/* Red Oak  */		{7, 0, 2, 1, 2, 100 },		// Simple	 Alternate	Lobed		Pointed
						};
	//If the set only have one attribute or one row, so that there is only one choice.
	if(m == 3 || n == 1) {
		System.out.println("Only one attribute or row. ");
		System.exit(0);
	}
	
	int total = 0;											// Count the total numbers of the samples.
	for(int i = 0; i < n; i++) {
		total += mySet[i][m-1];
	}
	System.out.println("Total samples number are: " + total);
	
	Set<Integer> countTypeNum = new HashSet<Integer>();		// Used to store how many different types there are in each column
	int[] columnTypeNum = new int[m-2];						// Used to get the type numbers in each column
	
	for(int i=1 ; i < m-1; i++) {            				// i - columns, j - rows
		for(int j=0;j<n;j++) {
			countTypeNum.add(mySet[j][i]);					// add to the set	
		}
		columnTypeNum[i-1]=countTypeNum.size();
		countTypeNum.clear();
	}
	int max = 0;
	System.out.print("Get the number of unique type each column: ");
	for(int i = 0; i <columnTypeNum.length; i++) {
		if(max <= columnTypeNum[i])
			max = columnTypeNum[i];
		System.out.print(columnTypeNum[i] + "\\");
	}
	System.out.println();
	System.out.println("max number of type in each column is: " + max);    
	
	double[] v = new double[max];							// Use to store information value for each column in the first choice

	Map<Object,Object> myMap = new HashMap<Object,Object>();      			//Match entry in column and count
	// Start to try the first choice. 
	
	double[] E = new double[m-2];      						// Use to store the expect values for each column
	double min = 100000000;		 							// Initialize the minimal value of E
	for(int i = 1; i < m-1; i++) {						    // Start from the first attribute.
		int tempValue = 0;									// TempValue, use to store the current value and use it for the next value
		
		for(int j = 0; j < n; j++) {						// Use the loop to store total value of each items in the current column in map.
			if(!myMap.containsKey(mySet[j][i])) {			
				myMap.put(mySet[j][i], mySet[j][m-1]);		// If the key doesn't exist, create it.  For example, 0 - 2, 1 - 40
			}else {
				tempValue = (int) myMap.get(mySet[j][i]);
				myMap.put(mySet[j][i], tempValue + mySet[j][m-1]);    // If the key exists, increase the number of the current item
			}														  // For example, finally get, 0 - 206, 1 - 44
		}
		
		for(int j = 0; j < n; j++) {							// Get information value V    formula: -sum_Tpye Pr(Type|Q) * Log_2 Pr(Tpye/Q)
			for(int c=0; c<columnTypeNum[i-1];c++) {
				if(mySet[j][i] == c) {
					v[mySet[j][i]] += (mySet[j][m-1] / (double)(int)myMap.get(mySet[j][i])) * 
										(Math.log((double)(int)myMap.get(mySet[j][i]) / (mySet[j][m-1])) / Math.log(2));
				}
			}
		}
		for(int c = 0; c < columnTypeNum[i-1]; c++) {                   // Get expect value
			E[i-1] += v[c] * (double)(int)myMap.get(c) / total ;
		}
		
		if(min >=  E[i-1]) { min = E[i-1];	}			// Find the minimum value of E
			
		System.out.println("E" + i + ": " + E[i-1]);
	
		v = new double[max];    					// Reset the V value
		myMap.clear(); 								// Reset the map
		
	}
	int indexOfChoice = 0;							// Use to stored the best choice
	for(int i = 0; i < m-2; i++) {
		if(E[i] == min) {
			indexOfChoice = i+1;
			System.out.println("The column " + (i+1) + "   -- " + title[i+1] + " -- is the 1 best choice.");}
	}
	min = 1000000;								// Reset minValue for E
	E = new double[m-1];                 	    // Reset Expect value
	
	
	LinkedList<Integer> finished = new LinkedList<Integer>();		//Add the selected column index to the list
	finished.add(indexOfChoice);
	
	int thisChoice =0;       // Use to store the index of choice for each time.
	
	//---------------start from the second choice to the end-------------------------------------------------
	
	for(int choice =1; choice < m-2; choice++) {					// Big loop for multiple column select: 1-second, 2-third, 3-fourth ...
		int[][] tempSet = new int[n][choice+2];						// Create a smaller 2d array
		Set<Integer> newChoiceIndex = new HashSet<Integer>(); 	 	// Use to add the checked column index to the list
		
		for(int innerLoop = 1;innerLoop < m-finished.size()-1;innerLoop++) {     // Inner loop for every column in each select.
			
			for(int i =innerLoop ;i<m-1;i++) {									// Use to store the index
				if(!finished.contains(i) && !newChoiceIndex.contains(i) ) {		
					newChoiceIndex.add(i);
					thisChoice = i;
					break;
				}
			}
	
			for(int i=0;i<n;i++) {	
				tempSet[i][choice+1] = mySet[i][m-1];						// Copy the count to the rightmost of the 2d array
			
				for(int a=0;a<choice;a++) {									// Copy all finished column to the middle of the 2d array (increased)
					tempSet[i][choice-a] = mySet[i][finished.get(a)];
			}
			
				tempSet[i][0] = mySet[i][thisChoice];						// Copy current column to the leftmost of the 2d array
			}
//		
//			for(int x = 0;x<n;x++) {
//				for(int y=0;y<choice+2;y++) {
//					System.out.print(tempSet[x][y]+ " ");
//				}
//				System.out.println();
//			}

			String[] tempString = new String[n];				// Use to store the entry
			int tempValue = 0;
		
			for(int i=0;i<n;i++) {
				for(int f = 0;f<choice+1;f++) {
					if(f==0) {
						tempString[i] = Integer.toString(tempSet[i][0]);                         // 0 = 0
					}
					else {
						tempString[i] = tempString[i] + Integer.toString(tempSet[i][f]);	     // 00
					}
				}
		
				if(!myMap.containsKey(tempString[i])){
					myMap.put(tempString[i], tempSet[i][choice+1]);								// 00 - 2, 10 - 40,  01 - 100, 02 - 100
				}else {
					tempValue = (int) myMap.get(tempString[i]);
					myMap.put(tempString[i], tempValue + tempSet[i][choice+1]); 				// 00 - 6, 10 - 44
				}	
			}
		
			Map<String,Double> tempMap = new HashMap<String,Double>();					// Create another map, have the same key but different value
			for(int i =0;i<n;i++) {
				
				String s = "";
				double iv;
			
				for(int j=0;j<choice+1;j++ ) {
					s += tempSet[i][j];
				}
			
				iv = tempSet[i][choice+1] / (double)(int)myMap.get(s)    * 
					 (Math.log((double)(int)myMap.get(s) / (tempSet[i][choice+1])) / Math.log(2));
			 
				if(!tempMap.containsKey(s)) 											
					tempMap.put(s, iv);										// Store iv to the map value
				else 
					tempMap.put(s, iv + tempMap.get(s));
			}
				//System.out.println(tempMap);
			for(String s: tempMap.keySet()) {													// Get expect value
				E[thisChoice]  += tempMap.get(s) * (double)(int)myMap.get(s)/total;				// For example, v1 * 6/250 + v2 * 44/250 + 0 + ...
			}									
			System.out.println("E"+thisChoice+": " +E[thisChoice]);
		
			tempMap.clear();
			myMap.clear();
		
		//break;
		}
		
		for(int i=1;i< m-choice;i++) {		// Get min value of E
			if(finished.contains(i))
				continue;
			if(min>=E[i]) 
				min = E[i];
		}
		
		for(int e = 1; e < m-choice; e++) {			// Get the index and column name
			if(finished.contains(e))
				continue;
			if(E[e] == min) {
				indexOfChoice = e;	
				//newChoiceIndex.remove(e);
				System.out.println("The column " + e + " -- "+ title[e] + " -- is the " + (choice+1) + " best choice.");
				if(E[e]==0) {
					System.out.println("Find E equal to 0. Program ends.");			
					System.exit(1);
				}	
				break;
			}
		}
		
		finished.add(indexOfChoice);		   // add the index of choice to the list
		
		E = new double[m-1];                  //reset Expect value

		//break;
	}
		
	}	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
	