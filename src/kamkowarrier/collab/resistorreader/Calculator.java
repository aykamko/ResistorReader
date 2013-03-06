package kamkowarrier.collab.resistorreader;

public class Calculator {
	
	double val1;
	double val2;
	double mul;
	double tol; //CURRENTLY NOT USED
	
    public String calculate(int a, int b, int c, int d) {
    	
    	this.val1 = a;
    	this.val2 = b;
    	this.mul = c;
    	this.tol = d;
    	
    	double result;
    	String output;
    	
    	result = (((10 * val1) + val2) * Math.pow(10, mul));
    	
    	//kilo 000 or 1e4
    	//mega 000,000 or 1e6 /
    	
    	if (result > 1e6) {
    		double mill = result / 1e6;
    		double thou = (result % 1e3) / 1e3;
    		output = mill + thou + " M ";
    	} else if (result > 1e3) {
    		double thou = result / 1e3;
    		double hund = (result % 1e2) / 1e2;
    		output = thou + hund + " K ";
    	} else {
    		output = result + "";
    	}
    return output;
    }
}