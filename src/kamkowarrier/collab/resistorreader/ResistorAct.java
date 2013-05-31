package kamkowarrier.collab.resistorreader;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ResistorAct extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resistor);
		
		    // Getting scaled translation amount for arrow image
	        final Resources r = getResources();
	        final float trans = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
		
			// Setting output elements
			final EditText valueOut = (EditText) findViewById(R.id.output_value);
			final EditText tolOut = (EditText) findViewById(R.id.tolerance_output);
			final TextView lower = (TextView) findViewById(R.id.lower_bound);
			final TextView upper = (TextView) findViewById(R.id.upper_bound);
			final TextView ohm = (TextView) findViewById(R.id.ohm);
			final TextView percent = (TextView) findViewById(R.id.percent);
			
			// Setting arrow
			final View selectHolder = (View) findViewById(R.id.selectHolder);
			final View arrow = (View) findViewById(R.id.arrow);
			
			// Initializing Calculator and setting outputs
			final Calculator calc = new Calculator();
			calc.setOutputViews(valueOut, tolOut, lower, upper);
			
			// Setting input elements
			final Button fourBandButton = (Button) findViewById(R.id.fourBandButton);
			final Button fiveBandButton = (Button) findViewById(R.id.fiveBandButton);
			fiveBandButton.setTextColor(r.getColor(R.color.gray4));
			final ResistorView resistorView = (ResistorView) findViewById(R.id.resistor_view);
			final ListView selectLV = (ListView) findViewById(R.id.LV_bands);
			
			// Initializing and assigning ColorSelectionAdapter
			final ColorSelectionAdapter selectAdapter = new ColorSelectionAdapter(ResistorAct.this, 
					R.layout.textview, resistorView, calc);
			selectLV.setAdapter(selectAdapter);
			resistorView.setSelector(selectAdapter);
			resistorView.setCalc(calc);
			resistorView.setArrow((ImageView) arrow);
			

			
		    
		    // Observer that measures various screen elements when they are drawn
			selectLV.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() { 
					int selectLVHeight = selectLV.getMeasuredHeight();
					float resistorViewTop = resistorView.getY();
					
					int[] selectHolderCoords = { 0, 0 };
					selectHolder.getLocationOnScreen(selectHolderCoords);
					
					float arrowWidth = arrow.getWidth();
					
					arrow.setX(selectHolderCoords[0] - arrowWidth + trans);
					selectAdapter.setParams(selectLVHeight);
					resistorView.setArrowVars(resistorViewTop, arrowWidth);
				}
			});
			
	        fourBandButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                resistorView.setBandMode(4);
	                fourBandButton.setTextColor(0xFF000000);
	                fiveBandButton.setTextColor(r.getColor(R.color.gray4));
	            }
	        });
	        
	        fiveBandButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                resistorView.setBandMode(5);
	                fiveBandButton.setTextColor(0xFF000000);
	                fourBandButton.setTextColor(r.getColor(R.color.gray4));
	            }
	        });
			
			// Initial calculate
			resistorView.calculate();
			
			// Listener for EditText boxes
			//Need to add error checking/ handling
			//TextReader doesn't work with 5 band resistors
			//Add checks for out of range
			// The above is to set the X for error checking
			final SpannableString redX = new SpannableString("X");
			redX.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, 0);
			
			//element at index 0 is for value, 1 for tolerance
			final String[] boxVals = {valueOut.getText().toString(), tolOut.getText().toString()};
			
			valueOut.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View view, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch(keyCode) {
							case KeyEvent.KEYCODE_ENTER:
		   						TextReader reader = new TextReader();
		   						if (!reader.isValidString(valueOut.getText().toString())) {
		   							ohm.setText(redX);
		   							break;
		   						}
		   						  int numBands = resistorView.bandColors.size();
	    						  reader.read(valueOut.getText().toString(),numBands); //change to correct ref
	    						  if (!reader.isInRange(reader.numUserVal,numBands)) {
	    							  ohm.setText(redX);
			   						  break;
	    						  }
        						  valueOut.setText(Double.valueOf(
        								  reader.roundValue(reader.numUserVal,numBands)).toString());
        						  System.out.println("g " + reader.roundValue(reader.numUserVal,numBands));
        						  System.out.println(reader.numUserVal);
        						  //need to decide to give options or give max/min value
        						  //remember to do a second set text if necessary
        						  boxVals[0] = valueOut.getText().toString();
        						  //BAD! This needs to be cleaned up
							  	  int original = resistorView.activeBandNum;
								  for (int i = 0; i < 3; i++) { //replace 3 with variable for length
			        			    resistorView.activeBandNum = i;
			        			    ColorBand c = new ColorBand(resistorView.getContext());
			        			    ColorBand.ValBand valB = c.new ValBand(resistorView.getContext());
			        			    int val = valB.valueToColor(reader.band[i]);
					    			resistorView.updateWithoutCalc(val);
								  }
								  resistorView.activeBandNum = original;
								  ohm.setText(getString(R.string.ohm));
        						  return true;
						    default:
						    	break;
						} 
				  }
				  return false;	
			}
		});
			tolOut.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View view, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch(keyCode) {
							case KeyEvent.KEYCODE_ENTER:
								resistorView.activeBandNum = 3; //make this more general!
								TextReader reader = new TextReader();
								if (!reader.isValidString(tolOut.getText().toString())) {
									percent.setText(redX);
		   							break;
								}
								double val = Double.valueOf(tolOut.getText().toString());
								System.out.println(val);
								val = reader.findClosestVal(val,reader.validTols);
								tolOut.setText(Double.valueOf(val).toString());
								boxVals[1] = Double.valueOf(val).toString();
								ColorBand c = new ColorBand(resistorView.getContext());
			        			ColorBand.TolBand tolB = c.new TolBand(resistorView.getContext());
			        			int color = tolB.valueToColor(val);
			        			resistorView.updateWithoutCalc(color);
			        			percent.setText(getString(R.string.percent));
			        	        return true;
							default:
								break;
						}
					}
					return false;
				}
			});
							
	 
	//Touch listener for ohm textView
	ohm.setOnTouchListener(new OnTouchListener() {
		public boolean onTouch(View view, MotionEvent event) {
			switch(event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	if (ohm.getText().toString().equals("X")) {
			    		ohm.setText(getString(R.string.ohm));
			    		valueOut.setText(boxVals[0]);
			    	}
					default:
						break;
				}
			return false;
		}
	});
	//Touch listener for percent TextView
	percent.setOnTouchListener(new OnTouchListener() {
		public boolean onTouch(View view, MotionEvent event) {
			switch(event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	if (percent.getText().toString().equals("X")) {
			    		percent.setText(getString(R.string.percent));
			    		tolOut.setText(boxVals[1]);
			    	}
					default:
						break;
				}
			return false;
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
