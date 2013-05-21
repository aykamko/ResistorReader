package kamkowarrier.collab.resistorreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ResistorAct extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_beta);
		
		// Setting color inputs
		final Integer[] androidColors = { R.color.androidBlack, R.color.androidBrown, 
				R.color.androidRed, R.color.androidOrange, R.color.androidYellow, 
				R.color.androidGreen, R.color.androidBlue, R.color.androidPurple, 
				R.color.androidGray, R.color.androidWhite };
	/*	final Integer[] colors = { R.color.black, R.color.brown, R.color.red, 
				R.color.orange, R.color.yellow, R.color.green, R.color.blue, 
				R.color.violet, R.color.gray, R.color.white  };
		final Integer[] tolerance = { R.color.violet, R.color.blue,
				R.color.green, R.color.brown, R.color.red, 
				R.color.gold, R.color.silver }; */
		
		// Setting all elements
		final EditText output = (EditText) findViewById(R.id.output_value);
		final EditText tol_output = (EditText) findViewById(R.id.tolerance_output);
		final TextView lower = (TextView) findViewById(R.id.lower_bound);
		final TextView upper = (TextView) findViewById(R.id.upper_bound);
	//	final ListView lv = (ListView) findViewById(R.id.bandLV);
		
		// Setting adapters
	/*	CircularAdapter band1_ca = new CircularAdapter(this, colors);
		CircularAdapter band2_ca = new CircularAdapter(this, colors);
		CircularAdapter multiplier_ca = new CircularAdapter(this, colors);
		CircularAdapter tolerance_ca = new CircularAdapter(this, tolerance);  */
		//
		final ListView lv = (ListView) findViewById(R.id.bandLV);
		lv.post(new Runnable() {
		    @Override
		    public void run() {
		        int w = lv.getMeasuredHeight();
		        int h = lv.getMeasuredHeight();
		        AbsListView.LayoutParams params = new AbsListView.LayoutParams(w,  h);
				CustomAdapter lvCA = new CustomAdapter(ResistorAct.this, R.layout.textview, 
						androidColors, params);
				lv.setAdapter(lvCA);
		    }
		});
		//
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
	/*	band1_vf.setOnViewSwitchListener(new ViewSwitchListener() {
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
                }); */
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
