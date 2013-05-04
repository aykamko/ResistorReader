package kamkowarrier.collab.resistorreader;

import java.util.ArrayList;

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
	}
	
	// Data variables
	int[] bandColors = {};
	ArrayList<Range> eventRangeList = new ArrayList<Range>();
	float scale;
	int activeBand;
	ColorSelectionAdapter selectAdap;
	
	// Drawing variables
	final Resources res = getResources();
	Bitmap resistorOutline;
	Bitmap resistorMask;
	Bitmap drawBitmap;
	int canvasWidth;
	int canvasHeight;
	Paint paint;
	Canvas drawCanvas;
	
	// Init method (includes a touch listener)
	public ResistorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		resistorOutline = BitmapFactory.decodeResource(res, R.drawable.resistor_outline);
		resistorMask = BitmapFactory.decodeResource(res, R.drawable.resistor_mask);
		
		canvasWidth = resistorMask.getWidth();
		canvasHeight = resistorMask.getHeight();
		
		drawBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
		paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
		
		drawCanvas = new Canvas(drawBitmap);
		activeBand = 0;
		
		this.setOnTouchListener(new OnTouchListener () {
			public boolean onTouch(View v, MotionEvent e) {
				switch(e.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					float pos = e.getY() * scale;
					int i = 0;
					while (i < eventRangeList.size()) {
							if (eventRangeList.get(i).isBetween(pos)) {
								activeBand = i; // Updates "pointer" to resistor band on touch
								selectAdap.setActiveScheme(i);
								return true;
							}
							i++;
					}
				}
				return false;
			}
		});
	}
	
	public void initializeColors(int[] bandColors) {
		this.bandColors = bandColors;
		invalidate(); // Redraws bitmap with bands
	}
	
	public void setSelector(ColorSelectionAdapter s) {
		this.selectAdap = s;
	}
	
	private void drawBands() {
		
		int startY = -360;
		int len = bandColors.length;
		int i = 0;
		eventRangeList.clear();
		Range current;
		
		while (i < len) {
			paint.setColor(res.getColor(bandColors[i]));
			current = new Range((canvasHeight / 2) + (startY + (i * ((600 / len) + (40 - (10 * (len - 4)))))),
					(canvasHeight / 2) + ((startY + (600 / len)) + (i * ((600 / len) + (40 - (10 * (len - 4)))))));
		    drawCanvas.drawRect(0, current.start, canvasWidth, current.end, paint);
		    eventRangeList.add(current);
		    i++;
		}
	}
	
	private Bitmap drawBitmap() {
		drawBands();
		drawCanvas.drawBitmap(resistorOutline, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		
		drawCanvas.drawBitmap(resistorMask, 0, 0, paint);
		paint.setXfermode(null);
		
		scale = (float) canvasHeight / this.getHeight();
		Bitmap scaled = Bitmap.createScaledBitmap(drawBitmap, this.getWidth(), this.getHeight(), true);
		
		return scaled;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Bitmap finalized = drawBitmap();
		canvas.drawBitmap(finalized, 0, 0, paint);
	}
	
	public void updateActiveBand(int color){
		bandColors[activeBand] = color;
		
		invalidate();
	}

}