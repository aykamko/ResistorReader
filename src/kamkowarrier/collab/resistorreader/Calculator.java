package kamkowarrier.collab.resistorreader;

public class Calculator {
	
	double val1;
	double val2;
	double mul;
	double tol; 
	
    public String calculate(int a, int b, int c, int d) {
    	
    	this.val1 = a;
    	this.val2 = b;
    	this.mul = c;
    	this.tol = d;
    	
    	switch ((int) tol) {
    	case 0:
    		tol = 0.1;
    		break;
    	case 1:
    		tol = 0.25;
    		break;
    	case 2:
    		tol = 0.5;
    		break;
    	case 3:
    		tol = 1;
    		break;
    	case 4:
    		tol = 2;
    		break;
    	case 5:
    		tol = 5;
    		break;
    	case 6:
    		tol = 10;
    		break;
    	
    	}
    	
    	double result;
    	String output;
    	
    	result = (((10 * val1) + val2) * Math.pow(10, mul)); //
    	
    	//kilo 000 or 1e4
    	//mega 000,000 or 1e6
    	
    	if (result > 1e6) {
    		double mill = result / 1e6;
    		double thou = (result % 1e3) / 1e3;
    		int millInt = (Double.valueOf(mill)).intValue();
    		int thouInt = (Double.valueOf(thou)).intValue();
    		output = millInt + thouInt + "M";
    	} else if (result > 1e3) {
    		double thou = result / 1e3;
    		double hund = (result % 1e2) / 1e2;
    		int thouInt = (Double.valueOf(thou)).intValue();
    		int hundInt = (Double.valueOf(hund)).intValue();
    		output = thouInt + hundInt + "K";
    	} else {
    		int resultInt = (Double.valueOf(result)).intValue();
    		output = (Integer.valueOf(resultInt)).toString();
    	}
    return output;
    }
}