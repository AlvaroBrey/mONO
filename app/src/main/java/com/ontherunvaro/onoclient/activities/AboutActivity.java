package com.ontherunvaro.onoclient.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.TextView;

import com.ontherunvaro.onoclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.about_icon_credits)
    TextView textView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.title_activity_about);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spanned aboutIcons;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutIcons = Html.fromHtml(getString(R.string.about_icon_html), Html.FROM_HTML_MODE_LEGACY);
        } else {
            aboutIcons = Html.fromHtml(getString(R.string.about_icon_html));
        }
        textView.setText(aboutIcons);
    }
}
