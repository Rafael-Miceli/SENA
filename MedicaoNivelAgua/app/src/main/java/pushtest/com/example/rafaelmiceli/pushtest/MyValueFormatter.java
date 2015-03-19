package pushtest.com.example.rafaelmiceli.pushtest;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by rafael.miceli on 26/02/2015.
 */
public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return "-" + mFormat.format(200 - value) + " cm";
    }

}
