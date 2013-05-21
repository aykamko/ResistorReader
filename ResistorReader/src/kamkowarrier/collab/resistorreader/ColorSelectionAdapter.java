package kamkowarrier.collab.resistorreader;

// import kamkowarrier.collab.resistorreader.ColorBand.ValBand;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ColorSelectionAdapter extends ArrayAdapter<Integer[]> {
	
	Context context;
	int layoutResourceId;
	int[] activeScheme;
	ColorBand activeType;
	AbsListView.LayoutParams params;
	ResistorView resistorView;
	
	public ColorSelectionAdapter(Context context, int layoutResourceId, AbsListView.LayoutParams params, ResistorView resistorView) {
		super(context, R.layout.textview);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.params = params;
		this.resistorView = resistorView;
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
	
	public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	  LayoutInflater li = (LayoutInflater) 
        			  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	  convertView = li.inflate(R.layout.textview, null);
    	   }
        
           TextView resultView = (TextView) convertView.findViewById(R.id.textView);
           resultView.setBackgroundColor(activeScheme[position]);
           resultView.setText(Double.valueOf(activeType.colorToValue(activeScheme[position])).toString());
           
           if (params != null) {
        	   AbsListView.LayoutParams newParams = new AbsListView.LayoutParams
        			   (params.width, (params.height / activeScheme.length) - 8);
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
					case MotionEvent.ACTION_MOVE:
						resistorView.updateActiveBand(activeScheme[position]);
						break;
					}
					return true;
				}
			});

           return resultView;
    	}
	
}