package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
// import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Integer> {
	
	Context context;
	int layoutResourceId;
	Integer colors[] = null;
	AbsListView.LayoutParams params;
	
	public CustomAdapter(Context context, int layoutResourceId, Integer[] colors, AbsListView.LayoutParams params) {
		super(context, R.layout.textview, colors);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.colors = colors;
		this.params = params;
    }
		
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	  LayoutInflater li = (LayoutInflater) 
        			  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	  convertView = li.inflate(R.layout.textview, null);
    	   }
           convertView = (TextView) convertView.findViewById(R.id.textView);
           convertView.setBackgroundResource(colors[position]);
           AbsListView.LayoutParams newParams = new AbsListView.LayoutParams(
        		   params.width, (params.height / colors.length) - 8);
           convertView.setLayoutParams(newParams);
    	   return convertView;
    	}
	
}
