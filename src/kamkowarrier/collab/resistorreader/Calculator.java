package kamkowarrier.collab.resistorreader;

public class Calculator {
	
	double val1;
	double val2;
	double mul;
	double tol; //CURRENTLY NOT USED
	
    public String calculate(int a, int b, int c, int d) {
    	val1 = a;
    	val2 = b;
    	mul = c;
    	tol = d;
    	int result;
    	String output;
    	
    	result = (int) ((10*val1 + val2) * Math.pow(10.0,mul));
    	
    	// kilo 000
    	//mega 000,000
    	
    	if (result > 100000000) {
    		int mill = result/1000000;
    		int thou = (result%1000000)/1000;
    		
    		output = mill + "." + thou + " M " + "\u2126";
    		
    	}
    	
    	else if (result > 1000) {
    		int thou = result/1000;
    		int hund = (result%100)/100;
    		
    		output = thou + "." + hund + " K " + "\u2126";
    		
    	}
    	
    	else{
    		output = "" + result + "\u2126";//
    	}
    return output;	
    	
    }
}