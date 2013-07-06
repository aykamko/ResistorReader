package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.content.res.Resources;

public class ColorBand {
      
	protected static Resources res;
	public ColorBand(Context context) {
		res = context.getResources();
	}
	
	public double colorToValue(int color) { return 0; }
	public int valueToColor(double val) { return 0; }
	public int[] colors;
	
	public class ValBand extends ColorBand {
		
		public int[] colors;
		public ValBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.valColors);
		}
		
		public double colorToValue(int color) {
			int val = 0;
			while (val < colors.length) {
				if (color == colors[val]) {
					return val;
				}
				val++;
			}
			return 50; // Error check
		}
		
		public int valueToColor(int val) {
			return colors[val];
		}
		
	}
	
public class FirstBand extends ColorBand {
		
		public int[] colors;
		public FirstBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.firstColors);
		}
		
		public double colorToValue(int color) {
			int val = 0;
			while (val < colors.length) {
				if (color == colors[val]) {
					return val + 1;
				}
				val++;
			}
			return 50; // Error check
		}
		
		public int valueToColor(int val) throws Exception {
			if (val == 0) {
				throw new Exception("If you're seeing this then FirstBand in ColorBand needs to be fixed");
			}
			return colors[val-1];
		}
		
	}
	
	public class MultBand extends ColorBand {
		
		public int[] colors;
		public MultBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.multColors);
		}
		
		public double colorToValue(int color) {
			int val = 0;
			while (val < colors.length) {
				if (color == colors[val]) {
				    return Math.pow(10, val-2);
				}
				val++;
			}
			return 50; // Error check
		}
		
		public int valueToColor(double val) {
			//int i = Double.valueOf(Math.log10(val)).intValue();
			int i = Double.valueOf(val).intValue();
			return colors[i];
		}
	
	}
	
	public class TolBand extends ColorBand {
		
		public int[] colors;
		public TolBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.tolColors);
		}
		
		public double colorToValue(int color) {
			double val = 0;
			switch (color) {
	    	case 0xFFCCCCCC:
	    		val = 10;
	    		break;
	    	case 0xFFFFCC00:
	    		val = 5;
	    		break;
	    	case 0xFF43140F:
	    		val = 1;
	    		break;
	    	case 0xFFFF4444:
	    		val = 2;
	    		break;
	    	case 0xFF99CC00:
	    		val = 0.5;
	    		break;
	    	case 0xFF33B5E5:
	    		val = 0.25;
	    		break;
	    	case 0xFFAA66CC:
	    		val = 0.1;
	    		break;
	    	}
			return val;
		}
		
		public int valueToColor(double val) {
			int color = 0xFFFF00FF;
			int sw = Double.valueOf(val * 10).intValue();
			switch (sw) {
	    	case 100:
	    		color = 0xFFCCCCCC;
	    		break;
	    	case 50:
	    		color = 0xFFFFCC00;
	    		break;
	    	case 10:
	    		color = 0xFF43140F;
	    		break;
	    	case 20:
	    		color = 0xFFFF4444;
	    		break;
	    	case 5:
	    		color = 0xFF99CC00;
	    		break;
	    	case 2:
	    		color = 0xFF33B5E5;
	    		break;
	    	case 1:
	    		color = 0xFFAA66CC;
	    		break;
	    	}
			return color;
		}
		
	}
	
	public class TempBand extends ColorBand {
		
		public int[] colors;
		public TempBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.tempColors);
		}
		
		public double colorToValue(int color) {
			int val = 0;
			switch (color) {
	    	case 0xFF43140F:
	    		val = 100;
	    		break;
	    	case 0xFFFF4444:
	    		val = 50;
	    		break;
	    	case 0xFFFF8800:
	    		val = 15;
	    		break;
	    	case 0xFFFFFF4E:
	    		val = 25;
	    		break;
	    	}
			return val;
		}
		
		public int valueToColor(int val) {
			int color = 0xFFFF00FF;
			switch (val) {
	    	case 100:
	    		color = 0xFF43140F;
	    		break;
	    	case 50:
	    		color = 0xFFFF4444;
	    		break;
	    	case 15:
	    		color = 0xFFFF8800;
	    		break;
	    	case 25:
	    		color = 0xFFFFFF4E;
	    		break;
	    	}
			return color;
		}
		
	}

}