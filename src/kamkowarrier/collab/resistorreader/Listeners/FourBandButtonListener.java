package kamkowarrier.collab.resistorreader.Listeners;

import kamkowarrier.collab.resistorreader.ColorBand;
import kamkowarrier.collab.resistorreader.R;
import kamkowarrier.collab.resistorreader.ResistorAct;
import kamkowarrier.collab.resistorreader.ResistorView;
import kamkowarrier.collab.resistorreader.TextReader;
import android.content.res.Resources;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FourBandButtonListener implements View.OnClickListener {

	private String[] boxVals;
	private ResistorView resistorView;
	private ResistorAct resistorAct;
	private TextView percent;
	private double[] storedTols;


	public FourBandButtonListener(double[] storedTols, String[] boxVals, 
			TextView percent, ResistorView resistorView, ResistorAct resistorAct) {

		this.boxVals = boxVals;
		this.storedTols = storedTols;
		this.percent = percent;
		this.resistorView = resistorView;
		this.resistorAct = resistorAct;
	}

	@Override
	public void onClick(View v) {
		TextReader.tolOut.setText(Double.valueOf(storedTols[0]).toString());
		resistorView.setBandMode(4,false,true);

		updateTolBand();

		resistorView.calculate();
		TextReader.fourBandButton.setTextColor(0xFF000000);
		TextReader.fiveBandButton.setTextColor(TextReader.r.getColor(R.color.gray4));
	}

	public void updateTolBand() {

		int original = resistorView.activeBandNum;
		int originalColor = resistorView.bandColors.get(original);
		double val = Double.valueOf(TextReader.tolOut.getText().toString());
		resistorView.activeBandNum = 3;

		TextReader.setBandNum(4);
		TextReader.setTolerance(val,false);
		TextReader.read(TextReader.valueOut.getText().toString(),true); //also changes lower & upper textviews

		TextReader.tolOut.setText(Double.valueOf(val).toString());
		boxVals[1] = Double.valueOf(val).toString();

		ColorBand c = new ColorBand(resistorView.getContext());
		ColorBand.TolBand tolB = c.new TolBand(resistorView.getContext());

		int color = tolB.valueToColor(val);

		resistorView.updateWithoutCalc(color);
		if (original != 3) {
		resistorView.activeBandNum = original;
		resistorView.updateWithoutCalc(originalColor);
		}

		percent.setText(resistorAct.getString(R.string.percent));
	}
}

