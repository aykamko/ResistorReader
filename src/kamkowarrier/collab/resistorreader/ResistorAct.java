package kamkowarrier.collab.resistorreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.*;
import android.view.View.*;
import android.widget.*;

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
			
			// Listener for EditText boxes
			valueOut.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View view, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) { //Key down? {
						switch(keyCode) {
							case KeyEvent.KEYCODE_ENTER:
		   						TextReader reader = new TextReader();
	    						  reader.read(valueOut.getText().toString());
	    						  System.out.println("entered val is" + reader.userVal);
	    						  System.out.println("real val is" + reader.realVal);
	    						  System.out.println("bands should be" + reader.band[0] + " " + reader.band[1] + " " + reader.band[2]);
        						  valueOut.setText(reader.realVal);
        						  //BAD!
        						  int[] bands = new int[3];
							  	  int original = resistorView.activeBandNum;
							  	  for (int i = 0; i < 3; i++) {
							  		  bands[i] = reader.band[i];
							  	  }
								  for (int i = 0; i < 3; i++) { //replace 3 with variable for length
			        			    resistorView.activeBandNum = i;
					    			resistorView.updateWithoutCalc(bands[i]);
					    			//resistorView.selectAdap.setActives(i);
								  }
								  resistorView.activeBandNum = original;
        						  return true;
						    default:
						    	break;
						} 
				  }
				  return false;	
			}
		});
	
	//Still need to add tolerance box
			
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resistor, menu);
		return true;
	}
	
} 
