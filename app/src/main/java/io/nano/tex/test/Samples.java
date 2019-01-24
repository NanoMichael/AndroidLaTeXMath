package io.nano.tex.test;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nano on 18-11-13
 */
public class Samples {

    private Context context;
    private int index = 0;
    private List<String> samples = new ArrayList<>();

    public Samples(Context context) {
        this.context = context;
    }

    public void readSamples() {
        try {
            InputStreamReader ir = new InputStreamReader(context.getAssets().open("SAMPLES.tex"));
            BufferedReader reader = new BufferedReader(ir);
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && !isSpace(line) && isSeparator(line)) {
                    addSample(sb.toString());
                    sb.delete(0, sb.length());
                } else if (!line.isEmpty() && !isSpace(line)) {
                    sb.append(line).append('\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isSpaceChar(str.charAt(i))) return false;
        }
        return true;
    }

    private boolean isSeparator(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '%') return false;
        }
        return true;
    }

    private void addSample(String sample) {
        if (sample == null || sample.isEmpty() || isSpace(sample)) return;
        samples.add(sample);
    }

    public String next() {
        if (index >= samples.size()) index = 0;
        String x = samples.get(index);
        index++;
        return x;
    }
}
