package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.content.res.Resources;

public class ColorBand {

	protected static Resources res;
	public ColorBand(Context context) {
		res = context.getResources();
	}
	
	public class ValBand extends ColorBand {
		
		protected int[] colors;
		public ValBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.valColors);
		}
		
		public int colorToValue(int color) {
			int val = 0;
			while (val < colors.length) {
				if (color == colors[val]) {
					return val;
				}
				val++;
			}
			return 50; // Error check
		}
	}
	
	public class MultBand extends ColorBand {
		
		protected int[] colors;
		public MultBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.multColors);
		}
		
		public double colorToValue(int color) {
			int val = 0;
			while (val < colors.length) {
				if (color == colors[val]) {
					return Math.pow(10, val);
				}
				val++;
			}
			return 50; // Error check
		}
		
	}
	
	public class TolBand extends ColorBand {
		
		protected int[] colors;
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
		
	}
	
	public class TempBand extends ColorBand {
		
		protected int[] colors;
		public TempBand(Context context) {
			super(context);
			colors = res.getIntArray(R.array.tempColors);
		}
		
		public double colorToValue(int color) {
			double val = 0;
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
		
	}

}