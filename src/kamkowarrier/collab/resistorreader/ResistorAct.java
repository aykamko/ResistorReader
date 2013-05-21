package kamkowarrier.collab.resistorreader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
// Create new BandTextViews
final BandTextView band1 = new BandTextView(this, bandColors);
final BandTextView band2 = new BandTextView(this, bandColors);
final BandTextView mult = new BandTextView(this, multColors);
final BandTextView tol = new BandTextView(this, tolColors);

// Create list of BandTextViews to be used in Adapter
final BandTextView[] resistorBands = { band1, band2, mult, tol };
*/

// Create and assign Resistor Band adapter
/* ResistorAdapter resAdapter = new ResistorAdapter(this, selectAdapter, resistorBands);
resistorView.setAdapter(resAdapter); */

// Set default Active Band to band1
// selectAdapter.activeBand = band1;

public class ResistorAct extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resistor);
			
			// Setting output elements
			final EditText output = (EditText) findViewById(R.id.output_value);
			final EditText tolOutput = (EditText) findViewById(R.id.tolerance_output);
			final TextView lower = (TextView) findViewById(R.id.lower_bound);
			final TextView upper = (TextView) findViewById(R.id.upper_bound);
			final Calculator calc = new Calculator();
			
			// Setting input elements
			final ResistorView resistorView = (ResistorView) findViewById(R.id.resistor_view);
			final ListView selectLV = (ListView) findViewById(R.id.LV_bands);
			
			// Create and assign Color Select adapter
			ColorSelectionAdapter selectAdapter = new ColorSelectionAdapter(ResistorAct.this, 
					R.layout.textview, null, resistorView);
			selectLV.setAdapter(selectAdapter);
			resistorView.setSelector(selectAdapter);
			resistorView.setCalc(calc);
			
			// Create calculator
			
			final int[] band_vals = {4, 6, 7, 1};
			// System.out.println(band_vals[1]);
			
			// Initial calculate
			String out = calc.calculate(band_vals[0], band_vals[1], band_vals[2], band_vals[3]);
			output.setText(out);
			lower.setText(calc.bounds[0]);
			upper.setText(calc.bounds[1]);
			tolOutput.setText(Double.toString(calc.tol));
			
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
