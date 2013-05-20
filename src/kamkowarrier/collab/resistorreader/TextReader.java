package kamkowarrier.collab.resistorreader;

//ACCOUNT FOR GOLD AND SILVER BANDS!
//BLACK AS FIRST BAND IS INVALID

public class TextReader{

public boolean isValid;
public int[] band;
public String realVal;
public String userVal;

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
  if (!isIn(lastChar,"1234567890MK.")) {
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
      return 0.0;
    }
    double smallVal = value;
    int numberOfZeroes = 0;
    while (smallVal >= 10) {
      smallVal = smallVal/10;
      numberOfZeroes++;
    }
    double closestVal = findClosestVal(smallVal);
    if (Character.toString(e.charAt(e.length() -1)).equals("M")) {
      value = closestVal*Math.pow(10,numberOfZeroes)*1000000;
      System.out.println("get here");
      realVal = closestVal*Math.pow(10,numberOfZeroes) + "M"; 
    }
    else if (Character.toString(e.charAt(e.length() -1)).equals("K")) {
      value = closestVal*Math.pow(10,numberOfZeroes)*1000;
      realVal = closestVal*Math.pow(10,numberOfZeroes) + "K"; 
    }
    else {
      realVal = value + "";
    }
  }
  return value;
} 


//takes in a double less than 10
// you can make a more efficent algorithm for this!
public double findClosestVal(double val) {
  double[] validVals = {1.0,1.1,1.2,1.3,1.5,1.6,1.8,2.0,2.2,2.4,2.7,3.0,3.3,3.6,3.9,4.3,4.7,5.1,5.6,6.2,6.8,7.5,8.2,9.1};
  double min = 10.0;
  double closestVal = 0.0;
  for (double i : validVals) {
    if (Math.abs(i - val) < min) {
      min = Math.abs(i-val);
      closestVal = i;
    }
  }
  return closestVal;
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
System.out.println(read.findClosestVal(1.8));
System.out.println(read.findClosestVal(5));
System.out.println(read.parseNumbers(".7"));
read.valueToBands(read.parseNumbers(".7"));
for (int x = 0; x < 3; x++) {
  System.out.println(read.band[x]);
}
}


public void valueToBands(double val) { 
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
}