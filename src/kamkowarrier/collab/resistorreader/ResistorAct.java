package kamkowarrier.collab.resistorreader;

import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ResistorAct extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resistor);
		
	    Integer[] colors = { R.color.black, R.color.brown, R.color.red, 
	    		R.color.orange, R.color.yellow, R.color.green,
	    		R.color.blue, R.color.violet, R.color.gray, R.color.white  };
	    
	    Integer[] tolerance = { R.color.brown, R.color.red, 
	    		R.color.gold, R.color.silver, R.color.salmon };
	    
	    final int[] band_vals = {0,0,0,0};
		
		ViewFlow band1_vf = (ViewFlow) findViewById(R.id.band1);
		ViewFlow band2_vf = (ViewFlow) findViewById(R.id.band2);
		ViewFlow multiplier_vf = (ViewFlow) findViewById(R.id.multiplier);
		ViewFlow tolerance_vf = (ViewFlow) findViewById(R.id.tolerance);
		
		CircularAdapter band1_ca = new CircularAdapter(this, colors);
		CircularAdapter band2_ca = new CircularAdapter(this, colors);
		CircularAdapter multiplier_ca = new CircularAdapter(this, colors);
		CircularAdapter tolerance_ca = new CircularAdapter(this, tolerance);
		
		band1_vf.setAdapter(band1_ca, band1_ca.MIDDLE);
		band2_vf.setAdapter(band2_ca, band2_ca.MIDDLE);
		multiplier_vf.setAdapter(multiplier_ca, multiplier_ca.MIDDLE);
		tolerance_vf.setAdapter(tolerance_ca, tolerance_ca.MIDDLE);
		
		band1_vf.setOnViewSwitchListener(new ViewSwitchListener() {
		    public void onSwitched(View v, int position) {
		    	Calculator calc = new Calculator();
		    	TextView output = (TextView) findViewById(R.id.output_value);
		    	String out = calc.calculate(((Integer)v.getTag()).intValue(),band_vals[1],band_vals[2],band_vals[3]);
		    	output.setText(out);
		    	band_vals[0] = ((Integer)v.getTag()).intValue();
		    }
		});
		
		band2_vf.setOnViewSwitchListener(new ViewSwitchListener() {
		    public void onSwitched(View v, int position) {
		    	Calculator calc = new Calculator();
		    	TextView output = (TextView) findViewById(R.id.output_value);
		    	String out = calc.calculate(band_vals[0],((Integer)v.getTag()).intValue(),band_vals[2],band_vals[3]);
		    	output.setText(out);
		    	band_vals[1] = ((Integer)v.getTag()).intValue();
		    }
		});
		
		multiplier_vf.setOnViewSwitchListener(new ViewSwitchListener() {
		    public void onSwitched(View v, int position) {
		    	Calculator calc = new Calculator();
		    	TextView output = (TextView) findViewById(R.id.output_value);
		    	String out = calc.calculate(band_vals[0],band_vals[1],((Integer)v.getTag()).intValue(),band_vals[3]);
		    	output.setText(out);
		    	band_vals[2] = ((Integer)v.getTag()).intValue();
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
