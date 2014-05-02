package com.medicinealarm.client;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.medicinealarm.client.R;
import com.medicinealarm.model.Message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	private String TAG = "com.medicinealarm.client.LoginActivity";

	// Values for email and password at the time of the login attempt.
	private String mPatientNumber;
	private String mPassword;

	// UI references.
	private EditText mPatientNumberView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// Store Session.
	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "AuthenticValue";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Context context = this.getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				PRIVATE_MODE);

//		SharedPreferences.Editor editor = pref.edit();
//		editor.remove(KEY_USERNAME);
//		editor.remove(KEY_PASSWORD);
//		editor.clear();
//		editor.commit();

		if (pref.getString(KEY_USERNAME, null) != null
				&& pref.getString(KEY_PASSWORD, null) != null) {
			startActivity(new Intent(LoginActivity.this,
					PatientInfoActivity.class));
			finish();
		}
		
		setContentView(R.layout.activity_login);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		final Button signInButton = (Button) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		// First logging ins
//
//		
//	}

	private void displayResponse(Message response) {
		Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		mPatientNumberView = (EditText) findViewById(R.id.patientNumber);
		mPasswordView = (EditText) findViewById(R.id.password);

		// Reset errors.
		mPatientNumberView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mPatientNumber = mPatientNumberView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mPatientNumber)) {
			mPatientNumberView
					.setError(getString(R.string.error_field_required));
			focusView = mPatientNumberView;
			cancel = true;
			// } else if (!mPatientNumber.contains("@")) {
			// mPatientNumberView.setError(getString(R.string.error_invalid_email));
			// focusView = mPatientNumberView;
			// cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Message> {

		@Override
		protected void onPreExecute() {
			mPatientNumber = mPatientNumberView.getText().toString();
			mPassword = mPasswordView.getText().toString();
		}

		@Override
		protected Message doInBackground(Void... params) {
			// try {
			// // Simulate network access.
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// return false;
			// }
			//
			// for (String credential : DUMMY_CREDENTIALS) {
			// String[] pieces = credential.split(":");
			// if (pieces[0].equals(mPatientNumber)) {
			// // Account exists, return true if the password matches.
			// return pieces[1].equals(mPassword);
			// }
			// }
			// // TODO: register the new account here.
			// return true;

			final String url = getString(R.string.base_uri) + "/login";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					mPatientNumber, mPassword);
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJacksonHttpMessageConverter());

			try {
				// Make the network request
				Log.d(TAG, url);
				ResponseEntity<Message> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						Message.class);
				return response.getBody();
			} catch (HttpClientErrorException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getStatusText(),
						e.getLocalizedMessage());
			} catch (ResourceAccessException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return new Message(0, e.getClass().getSimpleName(),
						e.getLocalizedMessage());
			}
		}

		@Override
		protected void onPostExecute(Message result) {
			displayResponse(result);
			mAuthTask = null;
			showProgress(false);

			if (result.getId() == 100) {
				saveLoginSession(mPatientNumber, mPassword);
				startActivity(new Intent(LoginActivity.this,
						PatientInfoActivity.class));
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}

			// connect to main page
			// if (result) {
			// startActivity(new Intent(LoginActivity.this,
			// MainActivity.class));
			//
			// //finish();
			// } else {
			// mPasswordView
			// .setError(getString(R.string.error_incorrect_password));
			// mPasswordView.requestFocus();
			// }
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	public void saveLoginSession(String username, String password) {
		Context context = this.getApplicationContext();
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				PRIVATE_MODE);

		Editor editor = pref.edit();

		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);

		editor.commit();
	}
}
