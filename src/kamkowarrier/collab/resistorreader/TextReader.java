package kamkowarrier.collab.resistorreader;

import java.math.*;

//ACCOUNT FOR GOLD AND SILVER BANDS!
//BLACK AS FIRST BAND IS INVALID
// Needs to know whether the resistor is 4 band or 5 band

public class TextReader{

public boolean isValid;
public int[] band;
public String realVal;
public String userVal;
public double numUserVal;
double[] validVals = {1.0,1.1,1.2,1.3,1.5,1.6,1.8,2.0,2.2,2.4,2.7,3.0,3.3,3.6,3.9,4.3,4.7,5.1,5.6,6.2,6.8,7.5,8.2,9.1};
double[] validTols = {0.1, 0.25, 0.5, 1.0, 2.0, 10.0};

double[] e6 = {1.0, 1.5, 2.2 , 3.3, 4.7, 6.8 }; //20%
double[] e12 = {1.2,1.8,2.7,3.9,5.6,8.2}; // plus e6! 10%
double[] e24 = {1.1,1.3,1.6,2.0,2.4,3.0,3.6,4.3,5.1,6.2,7.5,9.1}; //plus e12! 5%
/*double[] e48 = {100,121,147,178,215,261,316,383,464,562,681,825,
        105,127,154,187,226,274,332,402,487,590,715,866
        110  133  162  196  237  287  348  422  511  619  750  909
         115  140  169  205  249  301  365  442  536  649  787  953
*/

public void read(String str,int numBands) {
	valueToBands(parseNumbers(str), numBands);
}

public boolean isIn(String e, String things) {
  for (int i = 0; i < things.length(); i++) {
    if (e.equals(Character.toString(things.charAt(i)))) {
      return true;
    }
  }
  return false;
}

public boolean isValidString(String e) {
  String lastChar = Character.toString(e.charAt(e.length()-1));
  if (e.length() == 1 && !isIn(lastChar,"123456789")) {
    //System.out.println("String is one char and char is not a number");
    return false;
  }
  if (!isIn(lastChar,"1234567890MK.mk")) {
    //System.out.println("Last char " + lastChar + " is not valid");
    return false;
  }
  int dotCount = 0;
  for (int i = 0; i < e.length()-1; i++) {
    if (!isIn(Character.toString(e.charAt(i)),"1234567890.")) {
      //System.out.println("character" + e.charAt(i) + "");
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
  //System.out.println(dotCount);
  return true;
}

//takes in a string of numbers (and M or K) and converts it to an integer value if the value is greater than 10, a double value otherwise. The returned double value must only have one decimal point.
public double parseNumbers(String e) { // Decide on accuracy! 1 decimal right now.
  double value = 0.0;
  userVal = e;
  if (isValidString(e)) {
    isValid = true;
    if (!isIn(Character.toString(e.charAt(e.length()-1)), "1234567890")) {
      value = Double.parseDouble(e.substring(0,e.length()-1));
    }
    else {
      value = Double.parseDouble(e.substring(0,e.length()));
    }
    if (value == 0.0) {
      numUserVal = 0.0;
      return 0.0;
    }
    double smallVal = value;
    int numberOfZeroes = 0;
    while (smallVal >= 10) {
      smallVal = smallVal/10;
      numberOfZeroes++;
    }
    double closestVal = findClosestVal(smallVal,validVals);
    if (Character.toString(e.charAt(e.length() -1)).equals("M") || Character.toString(e.charAt(e.length() -1)).equals("m")) {
      numUserVal = smallVal*Math.pow(10,numberOfZeroes)*1000000;
      value = closestVal*Math.pow(10,numberOfZeroes)*1000000;
      //System.out.println("get here");
      realVal = closestVal*Math.pow(10,numberOfZeroes) + "M"; 
    }
    else if (Character.toString(e.charAt(e.length() -1)).equals("K") || Character.toString(e.charAt(e.length() -1)).equals("k")) {
      numUserVal = smallVal*Math.pow(10,numberOfZeroes)*1000;
      value = closestVal*Math.pow(10,numberOfZeroes)*1000;
      realVal = closestVal*Math.pow(10,numberOfZeroes) + "K"; 
    }
    else {
      numUserVal = smallVal*Math.pow(10,numberOfZeroes);
      value = closestVal*Math.pow(10, numberOfZeroes); 
      realVal = value + "";
    }
  }
  return value;
} 

public double roundValue(double val, int numBands) {
	BigDecimal num = new BigDecimal(val);
	if (numBands == 4) {
		num = num.round(new MathContext(2, RoundingMode.HALF_UP));
	}
	else if (numBands == 5) {
		num = num.round(new MathContext(3, RoundingMode.HALF_UP));
	}
	System.out.println(num + " num");
	return num.doubleValue();
}

public boolean isInRange(double val, int numBands) {
	val = roundValue(val,numBands);
	if (numBands == 4) {
		if (val < 0.1 || val > 99e9) {
			return false;
		}
	}
	else if (numBands == 5) {
		if (val < 0.1 || val > 999e9) {
			return false;
		}
	}
	return true;
} //this needs to be fixed so it notifies calling method of max allowed value

//takes in a double less than 10
// you can make a more efficient algorithm for this!
public double findClosestVal(double val, double[] valids) {
  double min = 10.0;
  double closestVal = 0.0;
  for (double i : valids) {
    if (Math.abs(i - val) < min) {
      min = Math.abs(i-val);
      closestVal = i;
    }
  }
  return closestVal;
}

//the double given to this function must have a max of 3 sig digits
public void valueToBands(double val, int numBands) { 
	if (numBands == 4) {
	  band = new int[3];
	  int numZeroes = 0;
	  if (val < 1) {
	    Double v = new Double(val*10);
	    band[0] = v.intValue();
	    v = new Double((val*10-band[0])*10);
	    band[1] = v.intValue();
	    band[2] = 0; //silver
	  } 
	  else if (val < 10) {
	    Double v = new Double(val);
	    band[0] = v.intValue();
	    v = new Double((val- band[0])*10);
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
	    v = new Double((val- band[0])*10);
	    band[1] = v.intValue();
	    if (numZeroes > 0) {
	      band[2] = numZeroes-1;
	    }
	    else {
	      band[2] = 0;
	    }
	  }
	}
	else if (numBands == 5) {
		band = new int[4];
		if (val < 1) { //assuming val is >= 0.1
			band[0] = 0; //black
			Double v = new Double(val*10);
			band[1] = v.intValue();
			v = new Double((val*10-band[1])*10);
			band[2] = v.intValue();
			band[3] = 0; //silver
		}
		else if (val < 10) { 
			System.out.println(val + " Meee");
		    Double v = new Double(val);
			band[0] = v.intValue();
			System.out.println((val-band[0])*10);
			v = new Double((val-band[0])*10 +.01);
			band[1] = v.intValue(); //v.intValue();
			if (val-band[0]-(new Double(band[1]).doubleValue()) < .005) { //if only 2 digits
				band[2] = 0; //black
			}
			else {
			    v = new Double((val*10-band[0]*10-band[1])*10 + .01);
			    band[2] = v.intValue();
			}
		    band[3] = 0; //silver
		    //System.out.println(band[0] + " " + band[1] + " " + band[2] + " " + band[3]);
		}
		else if (val < 100) {
		    Double v = new Double(val/10);
		    band[0] = v.intValue();
		    v = new Double((val/10-band[0])*10);
		    band[1] = v.intValue();
		    v = new Double((val-10*band[0]-band[1])*10 + .01);
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
			    v = new Double(val/10- band[0]*10);
			    band[1] = v.intValue();
			    v = new Double(val-100*band[0] - 10*band[1]);
			    band[2] = v.intValue();
			    band[3] = numZeroes;
		}
	}
	}


public static void main(String[] args) {
String str1 = "123M";
String str2 = "4M54M";
String str3 = "23.543M";
String str4 = "2345.M";
String str5 = "12.34.";
TextReader read = new TextReader();
System.out.println(read.isValidString(str1));
System.out.println(read.isValidString(str2));
System.out.println(read.isValidString(str3));
System.out.println(read.isValidString(str4));
System.out.println(read.isValidString(str5));
System.out.println(read.findClosestVal(1.8,read.validVals));
System.out.println(read.findClosestVal(5,read.validVals));
System.out.println(read.parseNumbers(".7"));
read.valueToBands(read.parseNumbers(".7"),4);
for (int x = 0; x < 3; x++) {
  System.out.println(read.band[x]);
}
}



}
