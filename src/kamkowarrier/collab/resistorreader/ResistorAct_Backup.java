package kamkowarrier.collab.resistorreader;

import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ResistorAct_Backup extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_beta);
		
		// Setting color inputs
		final Integer[] colors = { R.color.black, R.color.brown, R.color.red, 
				R.color.orange, R.color.yellow, R.color.green, R.color.blue, 
				R.color.violet, R.color.gray, R.color.white  };
		final Integer[] tolerance = { R.color.violet, R.color.blue,
				R.color.green, R.color.brown, R.color.red, 
				R.color.gold, R.color.silver };
		
		// Setting all elements
		final EditText output = (EditText) findViewById(R.id.output_value);
		final EditText tol_output = (EditText) findViewById(R.id.tolerance_output);
		final TextView lower = (TextView) findViewById(R.id.lower_bound);
		final TextView upper = (TextView) findViewById(R.id.upper_bound);
		final ViewFlow band1_vf = (ViewFlow) findViewById(R.id.band1);
		final ViewFlow band2_vf = (ViewFlow) findViewById(R.id.band2);
		final ViewFlow multiplier_vf = (ViewFlow) findViewById(R.id.multiplier);
		final ViewFlow tolerance_vf = (ViewFlow) findViewById(R.id.tolerance);
		
		// Setting adapters
		CircularAdapter band1_ca = new CircularAdapter(this, colors);
		CircularAdapter band2_ca = new CircularAdapter(this, colors);
		CircularAdapter multiplier_ca = new CircularAdapter(this, colors);
		CircularAdapter tolerance_ca = new CircularAdapter(this, tolerance);
		band1_vf.setAdapter(band1_ca, band1_ca.MIDDLE + 4);
		band2_vf.setAdapter(band2_ca, band2_ca.MIDDLE + 6);
		multiplier_vf.setAdapter(multiplier_ca, multiplier_ca.MIDDLE + 7);
		tolerance_vf.setAdapter(tolerance_ca, tolerance_ca.MIDDLE + 1);
		
		// Creating calculator
		final Calculator calc = new Calculator();
		final int[] band_vals = {4,6,7,1};
		
		// Initial calculate
		String out = calc.calculate(band_vals[0], band_vals[1], band_vals[2], band_vals[3]);
		output.setText(out);
		lower.setText(calc.bounds[0]);
		upper.setText(calc.bounds[1]);
		tol_output.setText(Double.toString(calc.tol));
		
		// Creating listeners for each band
		band1_vf.setOnViewSwitchListener(new ViewSwitchListener() {
                    public void onSwitched(View v, int position) {
                        String out = calc.calculate(((Integer)v.getTag()).intValue(), band_vals[1], 
                            band_vals[2], band_vals[3]);
                        output.setText(out);
                        lower.setText(calc.bounds[0]);
                        upper.setText(calc.bounds[1]);
                        band_vals[0] = ((Integer)v.getTag()).intValue();
                    }
                });

                band2_vf.setOnViewSwitchListener(new ViewSwitchListener() {
                    public void onSwitched(View v, int position) {
                        String out = calc.calculate(band_vals[0], ((Integer)v.getTag()).intValue(), 
                            band_vals[2], band_vals[3]);
                        output.setText(out);
                        lower.setText(calc.bounds[0]);
                        upper.setText(calc.bounds[1]);
                        band_vals[1] = ((Integer)v.getTag()).intValue();
                    }
                });
                
                multiplier_vf.setOnViewSwitchListener(new ViewSwitchListener() {
                    public void onSwitched(View v, int position) {
                        String out = calc.calculate(band_vals[0], band_vals[1], 
                            ((Integer)v.getTag()).intValue(), band_vals[3]);
                        output.setText(out);
                        lower.setText(calc.bounds[0]);
                        upper.setText(calc.bounds[1]);
                        band_vals[2] = ((Integer)v.getTag()).intValue();
                    }
                });
                
                tolerance_vf.setOnViewSwitchListener(new ViewSwitchListener() {
                    public void onSwitched(View v, int position) {
                        String out = calc.calculate(band_vals[0], band_vals[1],
                            band_vals[2], ((Integer)v.getTag()).intValue());
                        out = Double.toString(calc.tol);
                        tol_output.setText(out);
                        lower.setText(calc.bounds[0]);
                        upper.setText(calc.bounds[1]);
                        band_vals[3] = ((Integer)v.getTag()).intValue();}
                });
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
