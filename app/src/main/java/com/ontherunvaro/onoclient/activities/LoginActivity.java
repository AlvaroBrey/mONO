package com.ontherunvaro.onoclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import com.ontherunvaro.onoclient.R;
import com.ontherunvaro.onoclient.util.PrefConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    @BindView(R.id.edittext_login_email)
    EditText mailText;
    @BindView(R.id.edittext_login_password)
    EditText passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        SharedPreferences prefs = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, MODE_PRIVATE);
        if (prefs.contains(PrefConstants.Keys.USERNAME)) {
            mailText.setText(prefs.getString(PrefConstants.Keys.USERNAME, ""));
        }
        if (prefs.contains(PrefConstants.Keys.PASSWORD)) {
            passwordText.setText(new String(Base64.decode(prefs.getString(PrefConstants.Keys.PASSWORD, ""), Base64.NO_PADDING)));
        }
    }

    @OnClick(R.id.button_login)
    public void login() {
        Log.d(TAG, "login: Trying to login...");
        final String mail = mailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        // TODO: 3/12/16 secure storage!
        SharedPreferences prefs = getSharedPreferences(PrefConstants.Files.MAIN_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PrefConstants.Keys.USERNAME, mail);
        edit.putString(PrefConstants.Keys.PASSWORD, Base64.encodeToString(password.getBytes(), Base64.NO_PADDING));
        edit.apply();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_USERNAME, mail);
        i.putExtra(MainActivity.EXTRA_PASSWORD, password);
        Log.d(TAG, "login: Starting main activity");
        startActivity(i);
        finish();
    }
}
