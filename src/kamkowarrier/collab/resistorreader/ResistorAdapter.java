/* package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResistorAdapter extends BaseAdapter {

	BandTextView[] bandViews = null;
	ColorSelectionAdapter colorSelect;
	
	public ResistorAdapter(Context context, ColorSelectionAdapter colorSelect, BandTextView[] bandViews) {
		this.bandViews = bandViews;
		this.colorSelect = colorSelect;
	}

	@Override
	public int getCount() {
		return bandViews.length;
	}

	@Override
	public Object getItem(int position) {
		return bandViews[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
//
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = (BandTextView) bandViews[position];
			final Integer[] colorList = ( (BandTextView) convertView ).inputColors;
			convertView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View convertView, MotionEvent e) {
					switch(e.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						// colorSelect.colors = colorList;
						// colorSelect.activeBand = (BandTextView) convertView;
						colorSelect.notifyDataSetChanged();
						return true;
					}
					return false;
					
				}
			});
		}
		return convertView;
	}

}
*/