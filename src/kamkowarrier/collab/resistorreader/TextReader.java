package kamkowarrier.collab.resistorreader;

import java.math.*;
import android.content.res.Resources;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//TODO: change band settings when tolerance is set.

public class TextReader{

	//used to notify ResistorAct about aspects of the text input
    protected static boolean isValid;
	public static boolean isStandardVal;
	public static Resources r;
	public static boolean[] allowStandards;
	
	public static int[] band;
	public static String valString;
	public static String[] standards;
	
	public static double numUserVal;
	public static double lowerStandard;
	public static double upperStandard;
	public static TextView ohm;
	public static TextView lower;
	public static TextView upper;
	public static EditText valueOut;
	public static EditText tolOut;
	
	static double[] validTolsFour = {5.0, 10.0}; //TODO: Add 20% for three bands
	static double[] validTolsFive = {0.1, 0.25, 0.5, 1.0, 2.0};
	public static double[] validTols;
	static public int bandNum; //this is the band number that corresponds to the tolerance. Public because the listeners are in a different package.
	static Double[] currValArray;
	
	static double[] e6 = {1.0, 1.5, 2.2 , 3.3, 4.7, 6.8 }; //20%
	static double[] e12 = {1.2,1.8,2.7,3.9,5.6,8.2}; // plus e6! 10%
	static double[] e24 = {1.1,1.3,1.6,2.0,2.4,3.0,3.6,4.3,5.1,6.2,7.5,9.1}; //plus e12! 5%
	static double[] e48 = { 1.00,1.21,1.47,1.78,2.15,2.61,3.16,3.83,4.64,5.62,6.81,8.25,
			1.05,1.27,1.54,1.87,2.26,2.74,3.32,4.02,4.87,5.90,7.15,8.66,
			1.10,1.33,1.62,1.96,2.37,2.87,3.48,4.22,5.11,6.19,7.50,9.09,
			1.15,1.40,1.69,2.05,2.49,3.01,3.65,4.42,5.36,6.49,7.87,9.53
	};
	static double[] e96 = {102,1.24,1.50,1.82,2.21,2.67,3.24,3.92,4.75,5.76,6.98,8.45,
			1.07,1.30,1.58,1.91,2.32,2.80,3.40,4.12,4.99,6.04,7.32,8.87,
			1.13,1.37,1.65,2.00,2.43,2.94,3.57,4.32,5.23,6.34,7.68,9.31,
			1.18,1.43,1.74,2.10,2.55,3.09,3.74,4.53,5.49,6.65,8.06,9.76
	}; 
	static double[] e192 = {101,1.23,1.49,1.80,2.18,2.64,3.20,3.88,4.70,5.69,6.90,8.35,
			1.04,1.26,1.52,1.84,2.23,2.71,3.28,3.97,4.81,5.83,7.06,8.56,
			1.06,1.29,1.56,1.89,2.29,2.77,3.36,4.07,4.93,5.97,7.23,8.76, 
			1.09,1.32,1.60,1.93,2.34,2.84,3.44,4.17,5.05,6.12,7.41,8.98, 
			1.11,1.35,1.64,1.98,2.40,2.91,3.52,4.27,5.17,6.26,7.59,9.20, 
			1.14,1.38,1.67,2.03,2.46,2.98,3.61,4.37,5.30,6.42,7.77,9.42, 
			1.17,1.42,1.72,2.08,2.52,3.05,3.70,4.48,5.42,6.57,7.96,9.65,
			1.20,1.45,1.76,2.13,2.58,3.12,3.79,4.59,5.56,6.73,8.16,9.88
	};
	public static Button fourBandButton;
	public static Button fiveBandButton;
	
	public static void setUp(TextView ohm, TextView lower, TextView upper, EditText valueOut,
			EditText tolOut, Button fourBandButton, Button fiveBandButton,
			boolean[] allowStandards, Resources r, String[] standards) {
		TextReader.ohm = ohm;
		TextReader.lower = lower;
		TextReader.upper = upper;
		TextReader.valueOut = valueOut;
		TextReader.tolOut = tolOut;
		TextReader.allowStandards = allowStandards;
		TextReader.fourBandButton = fourBandButton;
		TextReader.fiveBandButton = fiveBandButton;
		TextReader.r = r;
		TextReader.standards = standards;
	}

    /* read() is used by ResistorAct to parse input and update the lower and upper standards buttons.
     * 
     * @param str is the String to parse (not always a valid number)
     * @param update updates the standards buttons if true, leaves them alone if false.
     * 
     * @return no return value, but changes are made to the internal state of the TextReader and the lower and upper buttons.
     */
	public static void read(String str, boolean update) {
		valueToBands(parseNumbers(str,update));
	}

	public static void setBandNum(int num) {
		if (num == 4) {
			TextReader.validTols = validTolsFour;
			bandNum = num;
		}
		else if (num == 5) {
			TextReader.validTols = validTolsFive;
			bandNum = num;
		}
	}

	public static boolean isIn(String e, String things) {
		for (int i = 0; i < things.length(); i++) {
			if (e.equals(Character.toString(things.charAt(i)))) {
				return true;
			}
		}
		return false;
	}
    
	/* note: isValidString() doesn't account for the String.empty case
	 * because that should be taken care of by ResistorAct
	 */
	public static boolean isValidString(String e, boolean tol) {
		String lastChar = Character.toString(e.charAt(e.length()-1));
		if (e.length() == 1 && !isIn(lastChar,"1234567890")) {
			return false;
		}
		if (!isIn(lastChar,"1234567890MK.mk")) {
			return false;
		}
		if (tol && !isIn(lastChar,"1234567890.")) {
			return false;
		}
		int dotCount = 0;
		for (int i = 0; i < e.length()-1; i++) {
			if (!isIn(Character.toString(e.charAt(i)),"1234567890.")) {
				return false;
			}
			if (Character.toString(e.charAt(i)).equals(".")) {
				dotCount++;
			}
		}
		if (Character.toString(e.charAt(e.length()-1)).equals(".")) {
			dotCount++;
		}
		if (dotCount > 1) {
			return false;
		}
		return true;
	}

	/* parseNumbers() changes the state of the lower and upper standards boxes as well as updating the state of this object
	 * so that the ResistorAct can access the updated values. Note that the upper and lower box strings are pre set to 
	 * "INVALID" and "VAL" respectively.
	 */
	public static double parseNumbers(String e, boolean update) {
		allowStandards[0] = true;
		allowStandards[1] = true;
		double value = 0.0;
		double smallVal = 0;
		String lowerString = "INVALID";
		String upperString = "INPUT";
		
		String lowerNeighbor = null;
		String upperNeighbor = null;
		double last = currValArray[currValArray.length-1];
		double first = currValArray[0];
		
		if (isValidString(e,false)) {
			System.out.println("valid");
			isValid = true;
			if (!isIn(Character.toString(e.charAt(e.length()-1)), "1234567890")) {
				value = Double.parseDouble(e.substring(0,e.length()-1));
			}
			else {
				value = Double.parseDouble(e.substring(0,e.length()));
			}
			if (value == 0) {
				isValid = false;
				if (update = true) {
					lower.setText(lowerString);
					upper.setText(upperString);
				}
				valString = " " + 0.0;
				return value;
			}


			if (Character.toString(e.charAt(e.length() -1)).equals("M") 
					|| Character.toString(e.charAt(e.length() -1)).equals("m")) {
				value = value*1000000;
			}
			else if (Character.toString(e.charAt(e.length() -1)).equals("K") 
					|| Character.toString(e.charAt(e.length() -1)).equals("k")) {
				value = value*1000;
			}

			value = roundValue(value,bandNum);
			
			numUserVal = value;
			smallVal = value; //make sure that this can be out to two sigfigs
			

			int numberOfZeroes = 0;
			while (smallVal >= 10.0) {
				smallVal = smallVal/10;
				numberOfZeroes++;
			}

			while (smallVal < 1.0) {
				smallVal = smallVal*10;
				numberOfZeroes--;
			}
			double[] standards = findClosestStandardVals(smallVal,currValArray);
			lowerStandard = standards[0];
			upperStandard = standards[2];

			isStandardVal = false;

			for (int i = 0; i < currValArray.length; i++) {
				if (Math.abs(currValArray[i]- smallVal) < .0001) {
					isStandardVal = true;
					break;
				}
			}

			System.out.println("numZeroes is " + numberOfZeroes);
			if (numberOfZeroes >= 6) {
				valString = roundValue(smallVal*Math.pow(10,numberOfZeroes-6),bandNum) + "M";
				if ((bandNum == 4 && value > 99000000) || (bandNum == 5 && value > 999000000)) {
					upperString = "MAX";
					allowStandards[1] = false;
					lowerString = roundValue(currValArray[currValArray.length-1]*Math.pow(10, bandNum-3),bandNum)
							+ "M";
				}
				else {
					lowerString = roundValue(lowerStandard*Math.pow(10,numberOfZeroes-6),bandNum) + "M";
					upperString = roundValue(upperStandard*Math.pow(10,numberOfZeroes-6),bandNum) + "M";
					lowerNeighbor = roundValue(last*Math.pow(10,numberOfZeroes-7),bandNum) 
							+ getSuffix(last*Math.pow(10,numberOfZeroes));
				    upperNeighbor = roundValue(first*Math.pow(10,numberOfZeroes-5),bandNum) 
							+ getSuffix(first*Math.pow(10,numberOfZeroes));
				}
			}
			else if (numberOfZeroes >= 3) {
				valString = roundValue(smallVal*Math.pow(10,numberOfZeroes-3),bandNum) + "K";
				lowerString = roundValue(lowerStandard*Math.pow(10,numberOfZeroes-3),bandNum) + "K";
				upperString = roundValue(upperStandard*Math.pow(10,numberOfZeroes-3),bandNum) + "K";
				lowerNeighbor = roundValue(last*Math.pow(10,numberOfZeroes-4),bandNum) 
						+ getSuffix(last*Math.pow(10,numberOfZeroes));
				System.out.println(lowerNeighbor + "lowerNeigh");
			    upperNeighbor = roundValue(first*Math.pow(10,numberOfZeroes-2),bandNum) 
						+ getSuffix(first*Math.pow(10,numberOfZeroes));
			    System.out.println(upperNeighbor + "upperNeigh");
			}
			else {
				lowerString = roundValue(lowerStandard*Math.pow(10,numberOfZeroes),bandNum) + "";
				upperString = roundValue(upperStandard*Math.pow(10,numberOfZeroes),bandNum) + "";
				valString = roundValue(smallVal*Math.pow(10,numberOfZeroes),bandNum) + "";
				if (value <= 0.1) {
					lowerString = "MIN";
					allowStandards[0] = false;
					upperString = roundValue(currValArray[0],bandNum) + "";          
				}
				lowerNeighbor = roundValue(last*Math.pow(10,numberOfZeroes-1),bandNum) 
						+ getSuffix(last*Math.pow(10,numberOfZeroes));
			    upperNeighbor = roundValue(first*Math.pow(10,numberOfZeroes+1),bandNum) 
						+ getSuffix(first*Math.pow(10,numberOfZeroes));
			}
		}
		// boundary cases
		if (upperString.equals(valString)) {
			System.out.println("HIT CASE 1");
			if (isStandardVal || smallVal > last) {
				upperString = upperNeighbor;
			}
		}
		else if (lowerString.equals(valString)) {
			System.out.println("HIT CASE 2");
			if (isStandardVal || smallVal < first) {
			System.out.println(lowerNeighbor + "final NEIGH");
			lowerString = lowerNeighbor;
			}
		}
		if (update) {
			System.out.println(value + " " + isInRange(value,bandNum));
			if (!isInRange(value,bandNum)) {
				System.out.println("NOT IN RANGE: " + value);
				//isValid = false;
				lower.setText("OUTSIDE");
				upper.setText("RANGE");
				allowStandards[0] = false;
				allowStandards[1] = false;
			}
			else {
				standards[0] = lowerString;
				standards[1] = valString;
				standards[2] = upperString;
				System.out.println("setting lower and upper" + lowerString + upperString);
				lower.setText(lowerString);
				upper.setText(upperString);
			}
		}
		else {
			//allowStandards[0] = true;
			//allowStandards[1] = true;
		}
		System.out.println("final value is: " + value);
		return value;
	}

	public static double roundValue(double val, int numBands) {
		BigDecimal num = new BigDecimal(val);
		if (numBands == 4) {
			num = num.round(new MathContext(2, RoundingMode.HALF_UP));
		}
		else if (numBands == 5) {
			num = num.round(new MathContext(3, RoundingMode.HALF_UP));
		}
		return num.doubleValue();
	}

	/* findClosestVal() is a regrettably inefficient and 
	 * finds the double in valids[] closest to val.
	 */
	public static double findClosestVal(double val, double[] valids) {
		double min = 99999;
		double closestVal = 0.0;
		for (double i : valids) {
			if (Math.abs(i - val) < min) {
				min = Math.abs(i-val);
				closestVal = i;
			}
		}
		return closestVal;
	}

	public static String getSuffix(double val) {
		
		double smallVal = roundValue(val,bandNum);
		int numberOfZeroes = 0;
		while (smallVal >= 10.0) {
			smallVal = smallVal/10;
			numberOfZeroes++;
		}
		while (smallVal < 1.0) {
			smallVal = smallVal*10;
			numberOfZeroes--;
		}
		if (numberOfZeroes >= 6) {
			return "M";
		}
		else if (numberOfZeroes >= 3) {
			return "K";
		}
		else {
			return "";
		}
		
	}
	
	/* setTolerance() builds the valid tolerance array and changes the band setting of this object values 
	 * given a valid tolerance.
	 */
	public static void setTolerance(double tol,boolean noUpdate) {
		tol = findClosestVal(tol,validTols);
		double[][] tempArrays = {e6,e12,e24,e48,e96,e192};
		Double[][] tolArrays = new Double[tempArrays.length][];  
		for (int i = 0; i < tempArrays.length; i++) {
			tolArrays[i] = new Double[tempArrays[i].length]; 
			for (int j = 0; j < tempArrays[i].length; j++) {
				tolArrays[i][j] = new Double(tempArrays[i][j]);
			}
		}
		Double[] e6D = tolArrays[0];
		Double[] e12D = tolArrays[1];
		Double[] e24D = tolArrays[2];
		Double[] e48D = tolArrays[3];
		Double[] e96D = tolArrays[4];
		Double[] e192D = tolArrays[5];
		if (Double.valueOf(tol).equals(20.0)) {
			currValArray = mergeAndSortArrays(new Double[][] {e6D});
			setBandNum(4); //TODO: SHOULD BE 3
		}
		else if (Double.valueOf(tol).equals(10.0)) {
			currValArray = mergeAndSortArrays(new Double[][] {e6D,e12D});
			setBandNum(4);
		}
		else if (Double.valueOf(tol).equals(5.0)) {
			currValArray = mergeAndSortArrays(new Double[][] {e6D,e12D,e24D});
			setBandNum(4);
		}
		else if (Double.valueOf(tol).equals(2.0)) {
			currValArray = mergeAndSortArrays(new Double[][] {e48D});
			setBandNum(5);
		}
		else if (Double.valueOf(tol).equals(1.0)) {
			currValArray = mergeAndSortArrays(new Double[][] {e48D,e96D,e6D,e12D,e24D});
			setBandNum(5);
		}
		else if (Double.valueOf(tol).equals(0.5) || Double.valueOf(tol).equals(0.25)
				|| Double.valueOf(tol).equals(0.1)) {
			currValArray = mergeAndSortArrays(new Double[][] {e48D,e96D,e192D});
			setBandNum(5);
		}
		else {
			System.out.println("Message from setTolerance in TextReader: Not a valid tolerance!");
			System.exit(0);
		}
		if (noUpdate) {
			read(valueOut.getText().toString(),false);
			return;
		}
		if (valueOut != null) {
			read(valueOut.getText().toString(),true);
		}
	}

	/* findClosestStandardValues() uses a modified binary search algorithm to return the
	 * two values below and above the closest standard value to the given value. 
	 */
	private static double[] findClosestStandardVals(double val, Double[] valArray) {
		double[] workingArray = new double[valArray.length];
		for (int i = 0; i < valArray.length; i++) {
			workingArray[i] = valArray[i].doubleValue();
		}
		TextReader reader = new TextReader();
		return reader.modBSearch(val,workingArray,0,workingArray.length-1);
	}
    /* isInRange() checks if a value is in a given range allowed by the band settings.
     * Note that a value can be in range but be above the greatest standard value for
     * either band setting.
     */
	public static boolean isInRange(double val, int numBands) {
		val = roundValue(val,numBands);
		if (val < 0.1 || val > Math.pow(10,6)*999) {
			return false;
		}
		return true;
	}

	//the double given to this function must have a max of 3 sig digits
	public static void valueToBands(double val) { 
		System.out.println("ValtoBands given: " + val);
		if (bandNum == 4) {
			band = new int[3];
			int numZeroes = 0;
			if (!isInRange(val,bandNum)) {
				return;
				//These values are invalid
			}
			if (val < 1) {
				Double v = new Double(val*10);
				band[0] = v.intValue();
				v = new Double((val*10-band[0])*10) + .0001;
				band[1] = v.intValue();
				band[2] = 0; //silver
			} 
			else if (val < 10) {
				Double v = new Double(val);
				band[0] = v.intValue();
				v = new Double((val- band[0])*10 + .0001);
				System.out.println("v is" + v);
				band[1] = v.intValue();
				band[2] = 1; //gold
			} 
			else {
				while (val >= 10) {  
					val = val/10;
					numZeroes ++;
				}
				Double v = new Double(val);
				band[0] = v.intValue();
				v = new Double((val- band[0])*10 + .0001);
				band[1] = v.intValue();
				if (numZeroes > 0) {
					band[2] = numZeroes+1;
				}
				else {
					band[2] = 0;
				}
			}
		}
		else if (bandNum == 5) {
			band = new int[4];
			if (val < 1) { //assuming val is >= 0.1
				band[0] = 0; //black
				Double v = new Double(val*10 + .0001);
				band[1] = v.intValue();
				v = new Double((val*10-band[1])*10 + .0001);
				band[2] = v.intValue();
				band[3] = 0; //silver
			}
			else if (val < 10) { 
				Double v = new Double(val);
				band[0] = v.intValue();
				v = new Double((val-band[0])*10 +.0001);
				band[1] = v.intValue(); //v.intValue();
				if (val-band[0]-(new Double(band[1]).doubleValue()) < .005) { //if only 2 digits
					band[2] = 0; //black
				}
				else {
					v = new Double((val*10-band[0]*10-band[1])*10 + .0001);
					band[2] = v.intValue();
				}
				band[3] = 0; //silver
				//System.out.println(band[0] + " " + band[1] + " " + band[2] + " " + band[3]);
			}
			else if (val < 100) {
				Double v = new Double(val/10);
				band[0] = v.intValue();
				v = new Double((val/10-band[0])*10 + .0001);
				band[1] = v.intValue();
				v = new Double((val-10*band[0]-band[1])*10 + .0001);
				band[2] = v.intValue();
				band[3] = 1; //gold
			}
			else {
				int numZeroes = 0;
				while (val >= 1000) {  
					val = val/10;
					numZeroes ++;
				}
				Double v = new Double(val/100);
				band[0] = v.intValue();
				v = new Double(val/10- band[0]*10 + .0001);
				band[1] = v.intValue();
				v = new Double(val-100*band[0] - 10*band[1] + .0001);
				band[2] = v.intValue();
				band[3] = numZeroes;
			}
		}
		System.out.println("band 3 is " + band[2]);
	}

	/*ModBSearch takes in a double and a SORTED array of standard values, 
	 * and uses binary search to give the two values below and above the input value. 
	 * Note that it provides for cases where val is the maximum or minimum standard value.
	 */
	public double[] modBSearch(double val, double[] standards, int low, int high) {
		int mid = (high+low)/2;
		double[] result = null;
		if (low > high) {
			if (low == 0) {
				result = new double[] {val, val, standards[low]};
			}
			else if (low == standards.length) {
				result = new double[] {standards[low-1],val,val};
			}
			else {
				result = new double[] {standards[high],val,standards[low]};
			}
		}
		else if (Math.abs(val-standards[mid]) < .0001) {
			if (mid == 0) {
				result = new double[] {standards[mid], standards[mid], standards[mid+1]};
			}
			else if (mid == standards.length-1) {
				result = new double[] {standards[mid -1],standards[mid],standards[mid]};
			}
			else {
				result = new double[] {standards[mid-1],standards[mid],standards[mid+1]};
			}
		}
		else if (val-standards[mid] > .0009) {
			result = modBSearch(val,standards,mid+1,high);
		}
		else if (val-standards[mid] < -.0009) {
			result = modBSearch(val,standards,low,mid-1);
		}
		return result;
	}


	//TODO: make a generic version of merge and sort
	//TODO: test with duplicate values.
	
	
	/* mergeAndSortArrays() uses the incomplete sorted standard arrays given and returns a sorted
	 * array of Doubles[]
	 * used to build the standard value arrays for a given tolerance.
	 */
	public static Double[] mergeAndSortArrays(Double[][] arrays) { 
		DoublePQ[] qArray = new DoublePQ[arrays.length];
		int totalLength = 0;
		TextReader reader = new TextReader();
		for (int i = 0; i < arrays.length; i++) {
			totalLength += arrays[i].length;
			qArray[i] = reader.new DoublePQ(arrays[i]);
		}
		Double[] result = new Double[totalLength];
		int i = 0;
		while (!allAreNull(qArray)) { //TODO: use a check that doesn't go over the array twice
			DoublePQ comparisonQ = reader.new DoublePQ(qArray.length);
			for (int j = 0; j < qArray.length; j++) {
				if (!(qArray[j].peek() == null)) {
					comparisonQ.insert(qArray[j].removeMin());
				}
			}
			while (!(comparisonQ.peek() == null)) {
				for (int j = 0; j < qArray.length; j++) {
					if (qArray[j].peek()!= null && qArray[j].peek().compareTo(comparisonQ.peek()) < 0) {
						comparisonQ.insert(qArray[j].removeMin());
					}
				}
				result[i] = comparisonQ.removeMin();
				i++;
			}
		}

		return result;
	}

	/* The DoublePQ class is a partial priority queue implementation that is limited
	 * to the size of the array it is constructed with. Because of this its insert() method
	 * is bounded, and all items must be filled before using the other methods.
	 */
	protected class DoublePQ { 
		Double[] heap;
		int numItems;

		public DoublePQ(int size) {
			heap = new Double[size];
			for (int i = 0; i < heap.length; i++) {
				heap[i] = null;
			}
			numItems = 0;
		}

		public DoublePQ(Double[] array) {
			heap = new Double[array.length];
			for (int i = 0;i < array.length;i++) {
				heap[i] = array[array.length-i-1];
			}
			numItems = array.length;
			bottomUpHeap();

		}

		public void insert(Double key) {
			if (numItems == heap.length) {
				System.out.println("ERROR, array out of space in DoublePQ");
				System.exit(0);
			}
			heap[heap.length-numItems-1] = key;
			numItems++;
			bottomUpHeap();

		}

		private void bottomUpHeap() {
			if (numItems == 0) {
				return;
			}
			for (int i = heap.length- numItems;i < heap.length-1;i++) {
				int parent = heap.length - Double.valueOf(Math.ceil((heap.length-i-1)/2.0)).intValue();
				if (heap[i].compareTo(heap[parent]) < 0) {
					Double temp = heap[i];
					heap[i] = heap[parent];
					heap[parent] = temp;
				} 
			}
		}

		public Double removeMin() {
			if (numItems == 0) {
				return null;
			}
			Double min = heap[heap.length-1];
			heap[heap.length-1] = heap[heap.length-numItems];
			heap[heap.length-numItems] = null;
			numItems--;
			bottomUpHeap();
			return min;
		}

		public Double peek() {
			Double result = heap[heap.length-1];
			if (result == null) {
				return null;
			}
			return result;
		}
	}


	private static boolean allAreNull(DoublePQ[] array) {
		for (int i =0; i < array.length; i ++) {
			if (array[i].peek() != null) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		
		//random tests, not comprehensive by any means.
		
		String str1 = "123M";
		String str2 = "4M54M";
		String str3 = "23.543M";
		String str4 = "2345.M";
		String str5 = "12.34.";
		TextReader read = new TextReader();
		//read.read("1.2542345M");
		double[] a = read.findClosestStandardVals(3.61,read.currValArray);
		System.out.println(a[0] + " " + a[1] + " " + a[2]);
		System.out.println(read.isValidString(str1,false));
		System.out.println(read.isValidString(str2,false));
		System.out.println(read.isValidString(str3,false));
		System.out.println(read.isValidString(str4,false));
		System.out.println(read.isValidString(str5,false));
		//System.out.println(read.parseNumbers(".7"));
		//read.valueToBands(read.parseNumbers(".7"));
		for (int x = 0; x < 3; x++) {
			System.out.println(read.band[x]);
		}
	}



}

