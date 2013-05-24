package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorSelectionAdapter extends ArrayAdapter<Integer[]> {
	
	Context context;
	int layoutResourceId;
	int[] activeScheme;
	ColorBand activeType;
	int listViewHeight = 0;
	ResistorView resistorView;
	Calculator calc;
	DisplayMetrics metric;
	
	public ColorSelectionAdapter(Context context, int layoutResourceId, ResistorView resistorView, Calculator calc) {
		super(context, R.layout.textview);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.resistorView = resistorView;
		this.calc = calc;
		this.metric = context.getResources().getDisplayMetrics();
		setActives(0);
    }
	
	@Override
	public int getCount() {
		return activeScheme.length;
	}
	
	public void setActives(int i) {
		this.activeScheme = resistorView.bandScheme[i];
		this.activeType = resistorView.bandTypeArray[i];
		this.notifyDataSetChanged();
	}
	
	public void setParams(int height) {
		this.listViewHeight = height;
		this.notifyDataSetChanged();
	}
	
	public float spToPix(float sp) {
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metric);
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	  LayoutInflater li = (LayoutInflater) 
        			  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	  convertView = li.inflate(R.layout.textview, null);
    	   }
        
           TextView resultView = (TextView) convertView.findViewById(R.id.textView);
           resultView.setBackgroundColor(activeScheme[position]);
           
           String text = "";
           if (activeScheme.length > 7) {
        	   text = calc.addSuffix(activeType.colorToValue(activeScheme[position]), 0);
           } else {
        	   text = (Double.valueOf(activeType.colorToValue(activeScheme[position])).toString());
           }
           
           resultView.setText(text);
           resultView.setTextSize(20);
           resultView.setGravity(Gravity.CENTER);
           
           if ((activeScheme[position] == 0xFF1D1D1D) || 
        		   (activeScheme[position] == 0xFF43140F)) {
        	   resultView.setTextColor(0xFFFFFFFF);
           } else {
        	   resultView.setTextColor(0xFF000000);
           }
           
           if (listViewHeight != 0) {
        	   AbsListView.LayoutParams newParams = new AbsListView.LayoutParams
        			   (AbsListView.LayoutParams.MATCH_PARENT, 
        					   (int) (((listViewHeight - spToPix(4)) / activeScheme.length) - spToPix(4)));
        	   resultView.setLayoutParams(newParams);
        	   } else {
        		   resultView.setLayoutParams(new AbsListView.LayoutParams
        				   (AbsListView.LayoutParams.MATCH_PARENT, 60));
        	   }
           
           resultView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent e) {
					switch(e.getAction())
					{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						resistorView.updateActiveBand(activeScheme[position]);
						//System.out.println("In CSA" + resistorView.activeBandNum);
						break;
					}
					return true;
				}
			});

           return resultView;
    	}
	
}