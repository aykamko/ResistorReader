package kamkowarrier.collab.resistorreader;

import android.app.Activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
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
import kamkowarrier.collab.resistorreader.Listeners.*;

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
			final Button lower = (Button) findViewById(R.id.lower_bound);
			final Button upper = (Button) findViewById(R.id.upper_bound);
			final TextView ohm = (TextView) findViewById(R.id.ohm);
			final TextView percent = (TextView) findViewById(R.id.percent);
			
			// Setting arrow
			final View selectHolder = (View) findViewById(R.id.selectHolder);
			final View arrow = (View) findViewById(R.id.arrow);
			
			// Initializing Calculator and setting outputs
			final Calculator calc = new Calculator();
			calc.setOutputViews(valueOut, tolOut, lower, upper, ohm);
			calc.ohmString = getString(R.string.ohm);
			
			
			
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
			
			//Setting stored tolerance values between 4 and 5 bands
			final double[] storedTols = { 10.0, 2.0 }; 
			//change 10.0 to starting value
			
			// status tracker for lower and upper buttons
			//element at index 0 for value, 1 for tolerance
			final boolean[] pressed = { false, false };
			
			//enables/disables lower and upper standard buttons for "MIN", "MAX", and invalid values
			final boolean[] allowStandards = {true, true};
			
		    // Observer that measures various screen elements when they are drawn
			selectLV.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
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

			// Initial calculate
			resistorView.firstCalculate();
			TextReader reader = new TextReader();
			reader.setBandNum(resistorView.bandColors.size());
			reader.setTolerance(reader.findClosestVal(new Double(tolOut.getText().toString()).doubleValue(),TextReader.validTols));
			reader.setOutputs(lower, upper,valueOut,tolOut,fourBandButton,fiveBandButton);
			reader.read(valueOut.getText().toString(),true);
			
			//element at index 0 is for value, 1 for tolerance
			final String[] boxVals = {valueOut.getText().toString(), tolOut.getText().toString()};
			
			resistorView.setUpTextReader(new Double(tolOut.getText().toString()).doubleValue(), lower, upper,
					valueOut,tolOut,fourBandButton,fiveBandButton);
			
			final String[] standards = {lower.getText().toString(), valueOut.getText().toString(),
					upper.getText().toString() };
			
			resistorView.reader.r = r; //Apologies for the hacky code, feel free to fix these.
			resistorView.reader.allowStandards = allowStandards;
			resistorView.reader.ohm = ohm;
			resistorView.reader.standards = standards;
            
			
			
			//TODO: change the tol and value updates to methods!!!
			
			
			lower.setOnClickListener(new LowerStandardListener(allowStandards, pressed, standards, lower, upper, 
					valueOut, tolOut, ohm, resistorView, this,fourBandButton,fiveBandButton));

			upper.setOnClickListener(new UpperStandardListener(allowStandards, pressed, standards, lower, upper, 
					valueOut, tolOut, ohm, resistorView, this,fourBandButton,fiveBandButton));
				
	        fourBandButton.setOnClickListener(new FourBandButtonListener(storedTols, boxVals, standards, fourBandButton, 
	        		fiveBandButton, valueOut, tolOut, percent, resistorView, this, r,lower,upper));
	        
	        fiveBandButton.setOnClickListener(new FiveBandButtonListener(storedTols, boxVals, standards, fourBandButton, 
	        		fiveBandButton, valueOut, tolOut, percent, resistorView, this, r,lower,upper));
			
			
			// Listener for EditText boxes
			final SpannableString redX = new SpannableString("X");
			redX.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, 0);
			
			valueOut.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View view, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch(keyCode) {
							case KeyEvent.KEYCODE_ENTER:
								
		   						TextReader reader = new TextReader();
		   						if (valueOut.getText().toString().length() == 0) {
		   							valueOut.setText(boxVals[0]);
		   							break;
		   						}
		   						allowStandards[0] = false;
								allowStandards[1] = false;
		   						if (!reader.isValidString(valueOut.getText().toString(),false)) {
		   							ohm.setText(redX);
		   							break;
		   						}
		   						  int numBands = resistorView.bandColors.size();
		   						  reader.setTolerance(new Double(tolOut.getText().toString()).doubleValue());
		   						  reader.setOutputs(lower, upper,valueOut,tolOut,fourBandButton,fiveBandButton);
		   						  reader.bandNum = numBands;
	    						  reader.read(valueOut.getText().toString(),true); //also changes lower & upper textviews
	    						  if (!reader.isInRange(reader.numUserVal,numBands)) {
	    							  ohm.setText(redX);
			   						  break;
	    						  }
        						  valueOut.setText(reader.valString);
        						  boxVals[0] = valueOut.getText().toString();
        						  //BAD! This needs to be cleaned up
							  	  int original = resistorView.activeBandNum;
								  for (int i = 0; i < numBands-1; i++) { //replace 3 with variable for length
			        			    resistorView.activeBandNum = i;
			        			    ColorBand c = new ColorBand(resistorView.getContext());
			                        int val = 0;
			        			    if (i < numBands-2) {
			        			    ColorBand.ValBand valB = c.new ValBand(resistorView.getContext());
			        			    val = valB.valueToColor(reader.band[i]);
			        			    }
			        			    else {
			        			        ColorBand.MultBand multB = c.new MultBand(resistorView.getContext());
				        			    val = multB.valueToColor(reader.band[i]);
				        			    System.out.println("reader.band[i] is " + reader.band[i]);
			        			    }
					    			resistorView.updateWithoutCalc(val);
								  }
								  resistorView.activeBandNum = original;
								  System.out.println(reader.isMin + " " + reader.isMax);
								  if (reader.isStandardVal) {
									  ohm.setText(getString(R.string.ohm));
								  }
								  else {
									  ohm.setText("\u26A0");
								  }
								  if (reader.isMin) {
									  System.out.println("IS MIN");
									  lower.setBackgroundResource(R.drawable.btn_default_disabled_holo_dark);
								  }
								  else {
									  allowStandards[0] = true;
									  lower.setBackgroundResource(R.drawable.btn_default_normal);
								  }
								  if (reader.isMax){
									  System.out.println("IS MAX");
									  upper.setBackgroundResource(R.drawable.btn_default_disabled_holo_dark);
								  }
								  else {
									  allowStandards[1] = true;
									  upper.setBackgroundResource(R.drawable.btn_default_normal);
								  }
								  standards[0] = lower.getText().toString();
								  standards[1] = reader.valString;
								  standards[2] = upper.getText().toString();
        						  return true;
						    default:
						    	break;
						} 
				  }
				  return false;	
			}
		});
			tolOut.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View view, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch(keyCode) {
							case KeyEvent.KEYCODE_ENTER:
								if (tolOut.getText().toString().length() == 0) {
		   							tolOut.setText(boxVals[1]);
		   							break;
		   						}
								int original = resistorView.activeBandNum;
								int originalColor = resistorView.bandColors.get(original);
								TextReader reader = new TextReader();
								if (!reader.isValidString(tolOut.getText().toString(),true)) {
									percent.setText(redX);
		   							break;
								}
								double val = Double.valueOf(tolOut.getText().toString());
								val = reader.findClosestVal(val,TextReader.validTols);
								System.out.println("In listener: " + val);
								if (resistorView.bandColors.size() == 4) {
								    resistorView.activeBandNum = 3;
								    reader.setBandNum(4);
								    storedTols[0] = val;
								}
								else {
									resistorView.activeBandNum = 4;
									reader.setBandNum(5);
									storedTols[1] = val;
								}
								// change calc upper and lower to textreader buttons
								
								reader.setTolerance(val);
								reader.setOutputs(lower, upper,valueOut,tolOut,fourBandButton,fiveBandButton);
								//this code enabled the view to update when tol changed
								/*if (reader.bandNum != resistorView.bandColors.size()) {
									if (reader.bandNum == 4) {
										resistorView.setBandMode(4);
						                fourBandButton.setTextColor(0xFF000000);
						                fiveBandButton.setTextColor(r.getColor(R.color.gray4));
									}
									else if (reader.bandNum == 5) {
										resistorView.setBandMode(5);
						                fiveBandButton.setTextColor(0xFF000000);
						                fourBandButton.setTextColor(r.getColor(R.color.gray4));
									}
								}*/
								reader.read(valueOut.getText().toString(),true); //also changes lower & upper textviews
								tolOut.setText(Double.valueOf(val).toString());
								boxVals[1] = Double.valueOf(val).toString();
								ColorBand c = new ColorBand(resistorView.getContext());
			        			ColorBand.TolBand tolB = c.new TolBand(resistorView.getContext());
			        			int color = tolB.valueToColor(val);
			        			System.out.println("UPDATING");
			        			resistorView.updateWithoutCalc(color);
			        			if (original != resistorView.activeBandNum) {
			        			resistorView.activeBandNum = original;
			        			resistorView.updateWithoutCalc(originalColor);
			        			}
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
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch(event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			    	if (ohm.getText().toString().equals("\u26A0")) {
			    	  upper.setBackgroundResource(R.drawable.button_animation_l);
			    	  lower.setBackgroundResource(R.drawable.button_animation_r);
			    	  AnimationDrawable lowerFrame = (AnimationDrawable) lower.getBackground();
			    	  AnimationDrawable upperFrame = (AnimationDrawable) upper.getBackground();
			    	  lowerFrame.stop();
			    	  upperFrame.stop();
			    	  lowerFrame.start();
			    	  upperFrame.start();
			    	}
			    	else if (ohm.getText().toString().equals("X")) {
			    		ohm.setText(getString(R.string.ohm));
			    		valueOut.setText(boxVals[0]);
			    		TextReader reader = new TextReader();
			    		reader.setTolerance(new Double(tolOut.getText().toString()).doubleValue());
			    		reader.setOutputs(lower, upper,valueOut,tolOut,fourBandButton,fiveBandButton);
			    		reader.read(valueOut.getText().toString(),true);
			    	}
					default:
						break;
				}
			return false;
		}
	});
	//Touch listener for percent TextView
	percent.setOnTouchListener(new OnTouchListener() {
		@Override
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
