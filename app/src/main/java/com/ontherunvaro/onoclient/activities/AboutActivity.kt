package com.ontherunvaro.onoclient.activities

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import android.widget.TextView

import com.ontherunvaro.onoclient.R

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*

class AboutActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        toolbar.setTitle(R.string.title_activity_about)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val aboutIcons: Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutIcons = Html.fromHtml(getString(R.string.about_icon_html), Html.FROM_HTML_MODE_LEGACY)
        } else {
            aboutIcons = Html.fromHtml(getString(R.string.about_icon_html))
        }

        about_icon_credits.text = aboutIcons
    }
}
