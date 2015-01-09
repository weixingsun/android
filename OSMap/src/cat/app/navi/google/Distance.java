package cat.app.navi.google;

import java.text.DecimalFormat;

public class Distance {
    private String text;
    private long value;

    public Distance(String text, long value) {
        this.text = text;
        this.value = value;
    }

    // 0.2 km
    // 90 m
    public String getText() {
    	if(text.startsWith("0.")){
    		String number = text.split(" ")[0];  //0.2
    		Float f= Float.valueOf(number) * 1000;
    		String newNumber = new DecimalFormat("0").format(f);
    		return newNumber+" m" ; //+"("+text+")"
    	}else if(text.contains(".")){
    		Float f= Float.valueOf(text.split(" ")[0]);
    		int i = (int)Math.floor(f);
            return i+text.split(" ")[1];
    	}
    	return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
