package kamkowarrier.collab.resistorreader;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

public class BandTextView extends TextView {

	Integer[] inputColors;
	
	public BandTextView(Context context, Integer[] inputColors) {
		super(context);
		this.inputColors = inputColors;
		this.setLayoutParams(new AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 80));
		this.setTextIsSelectable(false);
		this.setBackgroundResource(R.color.androidPurple);
		
		}
}
