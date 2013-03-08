package kamkowarrier.collab.resistorreader;

import java.math.BigDecimal;

public class Calculator {
	
	double val1;
	double val2;
	double mul;
	double tol;
	String[] bounds;
	
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
    	result = (((10 * val1) + val2) * Math.pow(10, mul)); //

    	bounds = new String[2];
    	bounds[0] = addSuffix(result-((tol/100.0)*result),2);
    	bounds[1] = addSuffix(result+((tol/100.0)*result),2);
        return addSuffix(result,1);
    }
    
    public String addSuffix(double result,int scale) {
    	String output;
    	if (result > 1e6) {
    		double mill = result / 1e6;
    		BigDecimal mill2 = new BigDecimal(mill);
    		mill2 = mill2.setScale(scale,BigDecimal.ROUND_HALF_DOWN);
    		output = mill2 + "M";
    	} else if (result > 1e3) {
    		double thou = result / 1e3;
    		BigDecimal thou2 = new BigDecimal(thou);
    		thou2 = thou2.setScale(scale,BigDecimal.ROUND_HALF_DOWN);
    		output = thou2 + "K";
    	} else {
    		int resultInt = (Double.valueOf(result)).intValue();
    		output = (Integer.valueOf(resultInt)).toString();
    	}
    return output;
    }
}