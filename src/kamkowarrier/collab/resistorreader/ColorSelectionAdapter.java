package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorSelectionAdapter extends ArrayAdapter<Integer> {
	
	Context context;
	int layoutResourceId;
	Integer colors[];
	AbsListView.LayoutParams params;
	BandTextView activeBand;
	
	public ColorSelectionAdapter(Context context, int layoutResourceId, 
			Integer[] colors, AbsListView.LayoutParams params, BandTextView activeBand) {
		super(context, R.layout.textview, colors);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.colors = colors;
		this.params = params;
		this.activeBand = activeBand;
    }
	
	@Override
	public int getCount() {
		return colors.length;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	  LayoutInflater li = (LayoutInflater) 
        			  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	  convertView = li.inflate(R.layout.textview, null);
    	   }
        
           convertView = (TextView) convertView.findViewById(R.id.textView);
           convertView.setBackgroundResource(colors[position]);
           convertView.setTag(position);
           
           if (params != null) {
        	   AbsListView.LayoutParams newParams = new AbsListView.LayoutParams
        			   (params.width, (params.height / colors.length) - 8);
        	   convertView.setLayoutParams(newParams);
        	   } else {
        		   convertView.setLayoutParams(new AbsListView.LayoutParams
        				   (AbsListView.LayoutParams.MATCH_PARENT, 60));
        	   }
           
           convertView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent e) {
					switch(e.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						activeBand.setBackgroundResource(colors[position]);
						// TODO: Have MotionEvent communicate with calculator
						return true;
					}
					return false;
					
				}
			});

           return convertView;
    	}
	
}