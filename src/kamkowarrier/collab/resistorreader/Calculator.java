package kamkowarrier.collab.resistorreader;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.widget.EditText;
import android.widget.TextView;

public class Calculator {
	
	double val1;
	double val2;
	double val3;
	double mul;
	double tol;
	double result;
	String[] bounds;
	
	EditText valueOut;
	EditText tolOut;
	TextView lower;
	TextView upper;
	
	public void setOutputViews(EditText valueOut, EditText tolOut, TextView lower, TextView upper) {
		this.valueOut = valueOut;
		this.tolOut = tolOut;
		this.lower = lower;
		this.upper = upper;
	}
	
	public void calculate(int[] bandColors, ColorBand[] bandTypeArray) {
		
		String str = "";
	    ArrayList<Double> values = new ArrayList<Double>();
	    
	    for (int i = 0; i < bandColors.length; i++) {
	    	System.out.println(bandTypeArray[i].colorToValue(bandColors[i]));
	        values.add(bandTypeArray[i].colorToValue(bandColors[i]));
	    }
	    
	    if (values.size() == 4) {
	    	val1 = values.get(0);
	    	val2 = values.get(1);
	    	mul = values.get(2);
	    	tol = values.get(3);
	    	result = (val1 + 0.1 * val2) * mul;
	    	str = addSuffix(result, 1);
	    } else { //Not accounting for six bands!
	    	val1 = values.get(0);
	    	val2 = values.get(1);
	    	val3 = values.get(2);
	    	mul = values.get(3);
	    	tol = values.get(4);
	    	result = (val1 + .1 * val2 + .01 * val3) * mul;
	    	str = addSuffix(result, 1);
	    }
	    
	    bounds = new String[2];
    	bounds[0] = addSuffix(result - ((tol / 100.0) * result), 2);
    	bounds[1] = addSuffix(result + ((tol / 100.0) * result), 2);
    	
    	// Sending output to Views
		valueOut.setText(str);
		tolOut.setText(Double.toString(tol));
		lower.setText(bounds[0]);
		upper.setText(bounds[1]);
		
	}
    
    public String addSuffix(double result, int scale) {
    	String output;
    	if (result > 1e6) {
    		double mill = result / 1e6;
    		BigDecimal mill2 = new BigDecimal(mill);
    		mill2 = mill2.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
    		output = mill2 + "M";
    	} else if (result > 1e3) {
    		double thou = result / 1e3;
    		BigDecimal thou2 = new BigDecimal(thou);
    		thou2 = thou2.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
    		output = thou2 + "K";
    	} else {
    		int resultInt = (Double.valueOf(result)).intValue();
    		output = (Integer.valueOf(resultInt)).toString();
    	}
    return output;
    }
}
