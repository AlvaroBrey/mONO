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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Base64
import com.ontherunvaro.onoclient.R
import com.ontherunvaro.onoclient.util.LogUtil
import com.ontherunvaro.onoclient.util.PrefConstants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    fun login() {
        if (!validateFields())
            return

        LogUtil.d(TAG, "login: Trying to login...")
        val mail = edittext_login_email.text.toString().trim()
        val password = edittext_login_password.text.toString().trim()

        // TODO: 3/12/16 secure storage!
        val prefs = getSharedPreferences(PrefConstants.Files.CREDENTIALS, Context.MODE_PRIVATE)
        val edit = prefs.edit()
        if (checkbox_remember.isChecked) {
            edit.putString(PrefConstants.Keys.USERNAME, mail)
            edit.putString(PrefConstants.Keys.PASSWORD, Base64.encodeToString(password.toByteArray(), Base64.NO_PADDING))
        } else {
            if (prefs.contains(PrefConstants.Keys.PASSWORD)) {
                edit.remove(PrefConstants.Keys.PASSWORD)
            }
        }
        edit.apply()

        val i = Intent(this, MainActivity::class.java)
        i.putExtra(MainActivity.EXTRA_USERNAME, mail)
        i.putExtra(MainActivity.EXTRA_PASSWORD, password)
        LogUtil.d(TAG, "login: Starting main activity")
        startActivity(i)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val p = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, Context.MODE_PRIVATE)
        // if user is logged in just forward them to the main activity, don't even set contentview
        if (p.getBoolean(PrefConstants.Keys.LOGGED_IN, false)) {
            LogUtil.d(TAG, "onCreate: User appears to be logged in. Forwarding to main activity.")
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        } else {
            setContentView(R.layout.activity_login)
            button_login.setOnClickListener { login() }

            val prefs = getSharedPreferences(PrefConstants.Files.CREDENTIALS, Context.MODE_PRIVATE)
            if (prefs.contains(PrefConstants.Keys.USERNAME)) {
                edittext_login_email.setText(prefs.getString(PrefConstants.Keys.USERNAME, ""))
            }
            if (prefs.contains(PrefConstants.Keys.PASSWORD)) {
                edittext_login_password.setText(String(Base64.decode(prefs.getString(PrefConstants.Keys.PASSWORD, ""), Base64.NO_PADDING)))
                checkbox_remember.isChecked = true
            }
        }
    }

    private fun validateFields(): Boolean {
        var valid = true

        if (TextUtils.isEmpty(edittext_login_email.text) || TextUtils.isEmpty(edittext_login_email.text.toString().trim())) {
            input_layout_email.error = getString(R.string.error_field_required)
            valid = false
        } else {
            input_layout_email.isErrorEnabled = false
        }
        if (TextUtils.isEmpty(edittext_login_password.text) || TextUtils.isEmpty(edittext_login_password.text.toString().trim())) {
            input_layout_password.error = getString(R.string.error_field_required)
            valid = false
        } else {
            input_layout_password.isErrorEnabled = false
        }
        return valid
    }

    companion object {
        private val TAG = "LoginActivity"
    }
}
