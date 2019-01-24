package io.nano.tex.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.nano.tex.LaTeX;

/**
 * Created by nano on 18-11-12
 */
public class MainActivity extends AppCompatActivity {

    private TeXView texView;
    private Samples samples;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        samples = new Samples(this);
        samples.readSamples();
        texView = findViewById(R.id.tex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_sample:
                nextSample();
                break;
            case R.id.debug:
                switchDebug(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void nextSample() {
        texView.setLaTeX(samples.next());
    }

    private void switchDebug(MenuItem item) {
        boolean isDebug = LaTeX.instance().isDebug();
        LaTeX.instance().setDebug(!isDebug);
        item.setChecked(LaTeX.instance().isDebug());
        texView.invalidateRender();
    }
}

