/*
 * mONO is a free app for a telephony provider's client area.
 * Copyright (C) 2017 Álvaro Brey Vilas <alvaro.brv@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0>.
 */

package com.ontherunvaro.onoclient.activities

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import com.ontherunvaro.onoclient.R
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
