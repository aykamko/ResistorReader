package kamkowarrier.collab.resistorreader;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ResistorView extends View {
	
	// Range class for storing valid touch event ranges
	private class Range {
		float start;
		float end;
		
		public Range(float start, float end) {
			this.start = start;
			this.end = end;
		}
		
		public boolean isBetween(float val) {
			if ((val > start) && (val < end)) {
				return true;
			}
			return false;
		}
		
		public float middle() {
			return ((end - start) / 2) + start;
		}
	}
	
	// Initializing band class instances
	Context ctx = getContext();
	ColorBand ColB = new ColorBand(ctx);
	ColorBand.FirstBand firstB = ColB.new FirstBand(ctx);
	ColorBand.ValBand valB = ColB.new ValBand(ctx);
	ColorBand.TolBand tolB = ColB.new TolBand(ctx);
	ColorBand.MultBand mulB = ColB.new MultBand(ctx);
	ColorBand.MultBandFive mulBF = ColB.new MultBandFive(ctx);
	ColorBand.TempBand temB = ColB.new TempBand(ctx);
	
	// Data variables
	int[][] bandScheme = {};
	ColorBand[] bandTypeArray = {};
	public int activeBandNum; //need to create getters and setters for listeners
	int thirdValBand;
	public ArrayList<Integer> bandColors = new ArrayList<Integer>(); //changed to public, should be changed back to protected once
	//listeners are modified (see above)
	ArrayList<Range> eventRangeList = new ArrayList<Range>();
	
	// Variables and methods to set ColorSelectionAdapter and Calculator instances
	ColorSelectionAdapter selectAdap;
	Calculator calc;
	public void setSelector(ColorSelectionAdapter s) { this.selectAdap = s; }
	public void setCalc(Calculator c) { this.calc = c; }
	
	// Drawing variables
	final Resources res = getResources();
	Bitmap resistorOutline;
	Bitmap resistorMask;
	Bitmap drawBitmap;
	int canvasWidth;
	int canvasHeight;
	Paint paint;
	Canvas drawCanvas;
	float scale;
	
	// Arrow variables
	float viewTop = 0;
	float arrowHeight = 0;
	ImageView arrow;
	public void setArrow(ImageView v) { this.arrow = v; }
	public void setArrowVars(float top, float height) { 
		this.viewTop = top; 
		this.arrowHeight = height;
	}
	
	// Init method (includes a touch listener)
	public ResistorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setBandMode(4,true);
		this.bandColors.addAll(Arrays.asList(bandScheme[0][3], bandScheme[1][5], bandScheme[2][6], bandScheme[3][0]));
		this.changeToBand(0);
		this.thirdValBand = 0xFF1D1D1D;
		
		this.resistorOutline = BitmapFactory.decodeResource(res, R.drawable.resistor_outline);
		this.resistorMask = BitmapFactory.decodeResource(res, R.drawable.resistor_mask);
		
		this.canvasWidth = resistorMask.getWidth();
		this.canvasHeight = resistorMask.getHeight();
		
		this.paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
		
		this.setOnTouchListener(new OnTouchListener () {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch(e.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					float pos = e.getY() * scale; // Adjust to new scale of View
					int i = 0;
					while (i < eventRangeList.size()) {
							if (eventRangeList.get(i).isBetween(pos)) {
								changeToBand(i); // Updates "pointer" to resistor band on touch
								updateArrow();
								selectAdap.setActives(i);
								return true;
							}
							i++;
					}
				}
				return false;
			}
		});
	}
	
	// Method that sets the band mode for the ResistorView (4 bands or 5 bands)
	public void setBandMode(int mode,boolean calculate) {
		switch(mode) {
		case 4:
			bandScheme = new int[][] { firstB.colors, valB.colors, mulB.colors, tolB.colors };
			bandTypeArray = new ColorBand[] { firstB, valB, mulB, tolB };
			
			if (activeBandNum == 3) {
				selectAdap.setActives(2);
			}
			
			if (bandColors.size() == 5) {
				thirdValBand = bandColors.get(2).intValue();
				bandColors.remove(2);
				//int color = bandColors.get(2);
				//color = mulB.advanceVal(color);
				//bandColors.set(2, color);
				if (activeBandNum > 1) {
					activeBandNum -= 1;
				}
			}
			
			break;
		case 5:
			bandScheme = new int[][] { firstB.colors, valB.colors, valB.colors, mulBF.colors, tolB.colors };
			bandTypeArray = new ColorBand[] { firstB, valB, valB, mulBF, tolB };
			
			if (activeBandNum == 2) {
				selectAdap.setActives(3);
			}
			
			if (bandColors.size() == 4) {
				bandColors.add(2, thirdValBand);
				
				//this check avoids errors in transition from MulB to MulBF
				if (mulB.getIndex(bandColors.get(3)) == mulBF.colors.length) {
					int fixedColor = mulB.decreaseVal(bandColors.get(3));
					bandColors.set(3, fixedColor);
				}
				
				//int black = valB.valueToColor(0);
				//bandColors.add(2,black);
				//int color = bandColors.get(3);
				//color = mulB.decreaseVal(color);
				//bandColors.set(3, color);
				if (activeBandNum > 1) {
					activeBandNum += 1;
				}
			}
			
			break;
		default:
			break;
		}
		
		if (arrow != null) {
			arrow.setVisibility(View.INVISIBLE);
			invalidate();
			if (calculate) {
			calculate();
			}
		}
		
	}
	
	private void drawBands() {
		
		int startY = -360;
		int len = bandColors.size();
		eventRangeList.clear();
		Range current;
		
		for (int i = 0; i < len; i++) {
			paint.setColor(bandColors.get(i).intValue());
			current = new Range((canvasHeight / 2) + (startY + (i * ((600 / len) + (40 - (10 * (len - 4)))))),
					(canvasHeight / 2) + ((startY + (600 / len)) + (i * ((600 / len) + (40 - (10 * (len - 4)))))));
		    drawCanvas.drawRect(0, current.start, canvasWidth, current.end, paint);
		    eventRangeList.add(current);
		}
	}
	
	private Bitmap drawBitmap() {
		
		this.drawBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
		this.drawCanvas = new Canvas(drawBitmap);
		
		drawBands();
		drawCanvas.drawBitmap(resistorOutline, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		
		drawCanvas.drawBitmap(resistorMask, 0, 0, paint);
		paint.setXfermode(null);
		
		scale = (float) canvasHeight / this.getHeight();
		Bitmap scaled = Bitmap.createScaledBitmap(drawBitmap, this.getWidth(), this.getHeight(), true);
		
		if (arrow.getVisibility() == View.INVISIBLE) {
			updateArrow();
		}
		
		return scaled;
	} 
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Bitmap finalized = drawBitmap();
		canvas.drawBitmap(finalized, 0, 0, paint);
	}
	
	public void changeToBand(int i) {
		this.activeBandNum = i;
	}
	
	public void updateArrow() {
		arrow.setVisibility(VISIBLE);
		arrow.setY((eventRangeList.get(activeBandNum).middle() / scale) - (viewTop + (arrowHeight / 2)));
	}
	
	public void updateActiveBand(int color){
		bandColors.set(activeBandNum, color);
		calculate();
		invalidate();
	}
	
	public void updateWithoutCalc(int color) {
		bandColors.set(activeBandNum, color);
		invalidate();
	}
	
	public void firstCalculate() {
		calc.calculate(bandColors, bandTypeArray);
	}
	
	public void calculate() {
		for (int i = 0; i < bandColors.size(); i++) {
			System.out.println(bandTypeArray[i].colorToValue((bandColors.get(i))));
		}
		calc.calculate(bandColors, bandTypeArray);
		for (int i = 0; i < bandColors.size(); i++) {
			System.out.println(bandTypeArray[i].colorToValue((bandColors.get(i))));
		}
TextReader.setTolerance(new Double(TextReader.tolOut.getText().toString()).doubleValue(),false);
TextReader.read(TextReader.valueOut.getText().toString(),true);

		if (TextReader.isStandardVal) {
			TextReader.ohm.setText(TextReader.r.getString(R.string.ohm));
		  }
		  else {
			  TextReader.ohm.setText("\u26A0");
		  }
		  if (!TextReader.allowStandards[0]) {
			  System.out.println("IS MIN");
			  TextReader.lower.setBackgroundResource(R.drawable.btn_default_disabled_holo_dark);
		  }
		  else {
			  TextReader.lower.setBackgroundResource(R.drawable.btn_default_normal);
		  }
		  if (!TextReader.allowStandards[1]){
			  System.out.println("IS MAX");
			  TextReader.upper.setBackgroundResource(R.drawable.btn_default_disabled_holo_dark);
		  }
		  else {
			  TextReader.upper.setBackgroundResource(R.drawable.btn_default_normal);
		  }
	}
	
	// changes band mode if a tolerance is selected that doesn't belong to the current band setting.
	//this is ONLY called when the active band is the tolerance band!
	public void changeBandMode(int color) {
		double val = tolB.colorToValue(color);
		if (bandColors.size() == 4) {
			if (val <= 2.0) {
				//setBandMode(5,false);
				setBandMode(5,true);
				TextReader.fiveBandButton.setTextColor(0xFF000000);
				TextReader.fourBandButton.setTextColor(TextReader.r.getColor(R.color.gray4));
			}
		}
		else if (bandColors.size() == 5) {
			if (val >= 5.0) {
				setBandMode(4,true);
				TextReader.fourBandButton.setTextColor(0xFF000000);
				TextReader.fiveBandButton.setTextColor(TextReader.r.getColor(R.color.gray4));
			}
		}
	}

}