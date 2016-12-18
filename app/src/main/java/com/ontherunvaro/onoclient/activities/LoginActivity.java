package com.ontherunvaro.onoclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ontherunvaro.onoclient.R;
import com.ontherunvaro.onoclient.util.PrefConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    @BindView(R.id.input_layout_password)
    TextInputLayout inputLayoutPassword;
    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.edittext_login_email)
    EditText mailText;
    @BindView(R.id.edittext_login_password)
    EditText passwordText;
    @BindView(R.id.checkbox_remember)
    CheckBox rememberCheckbox;

    @OnClick(R.id.button_login)
    public void login() {

        if (!validateFields())
            return;

        Log.d(TAG, "login: Trying to login...");
        final String mail = mailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        // TODO: 3/12/16 secure storage!
        SharedPreferences prefs = getSharedPreferences(PrefConstants.Files.CREDENTIALS, MODE_PRIVATE);
        final SharedPreferences.Editor edit = prefs.edit();
        if (rememberCheckbox.isChecked()) {
            edit.putString(PrefConstants.Keys.USERNAME, mail);
            edit.putString(PrefConstants.Keys.PASSWORD, Base64.encodeToString(password.getBytes(), Base64.NO_PADDING));
        } else {
            if (prefs.contains(PrefConstants.Keys.PASSWORD)) {
                edit.remove(PrefConstants.Keys.PASSWORD);
            }
        }
        edit.apply();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_USERNAME, mail);
        i.putExtra(MainActivity.EXTRA_PASSWORD, password);
        Log.d(TAG, "login: Starting main activity");
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences p = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, MODE_PRIVATE);
        // if user is logged in just forward him to the main activity
        if (p.getBoolean(PrefConstants.Keys.LOGGED_IN, false)) {
            Log.d(TAG, "onCreate: User appears to be logged in. Forwarding to main activity.");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);

            SharedPreferences prefs = getSharedPreferences(PrefConstants.Files.CREDENTIALS, MODE_PRIVATE);
            if (prefs.contains(PrefConstants.Keys.USERNAME)) {
                mailText.setText(prefs.getString(PrefConstants.Keys.USERNAME, ""));
            }
            if (prefs.contains(PrefConstants.Keys.PASSWORD)) {
                passwordText.setText(new String(Base64.decode(prefs.getString(PrefConstants.Keys.PASSWORD, ""), Base64.NO_PADDING)));
                rememberCheckbox.setChecked(true);
            }
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        if (TextUtils.isEmpty(mailText.getText()) || TextUtils.isEmpty(mailText.getText().toString().trim())) {
            inputLayoutEmail.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(passwordText.getText()) || TextUtils.isEmpty(passwordText.getText().toString().trim())) {
            inputLayoutPassword.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return valid;
    }
}
