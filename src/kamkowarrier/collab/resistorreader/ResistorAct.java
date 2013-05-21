package kamkowarrier.collab.resistorreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ResistorAct extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resistor);
		
			// Setting output elements
			final EditText valueOut = (EditText) findViewById(R.id.output_value);
			final EditText tolOut = (EditText) findViewById(R.id.tolerance_output);
			final TextView lower = (TextView) findViewById(R.id.lower_bound);
			final TextView upper = (TextView) findViewById(R.id.upper_bound);
			
			// Initializing Calculator and setting outputs
			final Calculator calc = new Calculator();
			calc.setOutputViews(valueOut, tolOut, lower, upper);
			
			// Setting input elements
			final ResistorView resistorView = (ResistorView) findViewById(R.id.resistor_view);
			final ListView selectLV = (ListView) findViewById(R.id.LV_bands);
			
			// Initializing and assigning ColorSelectionAdapter
			final ColorSelectionAdapter selectAdapter = new ColorSelectionAdapter(ResistorAct.this, 
					R.layout.textview, resistorView, calc);
			selectLV.setAdapter(selectAdapter);
			resistorView.setSelector(selectAdapter);
			resistorView.setCalc(calc);
			
			selectLV.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() { 
					int selectLVHeight = selectLV.getHeight();
					selectAdapter.setParams(selectLVHeight);
				}
			});
			
			// Initial calculate
			resistorView.calculate();
			
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
