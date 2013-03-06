package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CircularAdapter extends BaseAdapter
{   
        private static LayoutInflater mInflater;
	    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
        public final int MIDDLE;
        private Integer[] colorlist;
        private TextView coloredText;

        public CircularAdapter(Context context, Integer[] colors)
        {
            CircularAdapter.mInflater = LayoutInflater.from(context);
        	this.colorlist = colors;
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % colors.length;
        }

        @Override
        public int getCount()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) 
        {
            return colorlist[position % colorlist.length];
        }
        
        @Override
        public long getItemId(int position) {
            return position % colorlist.length;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
           if (convertView == null) {
       		   convertView = mInflater.inflate(R.layout.textview, parent, false);
       	   }
           coloredText = (TextView) convertView.findViewById(R.id.textView);
           coloredText.setBackgroundResource(colorlist[position % colorlist.length]);
           coloredText.setTag((Integer)position % colorlist.length);
       	   return convertView;
       	}
        
 }