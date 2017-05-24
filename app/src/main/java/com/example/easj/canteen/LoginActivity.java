package com.example.easj.canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easj.canteen.model.Customer;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText emailView = (EditText) findViewById(R.id.login_email_edittext);
        EditText passwordView = (EditText) findViewById(R.id.login_password_edittext);
        SharedPreferences preferences = getSharedPreferences(Canteen.SHARED_PREFERENCES, MODE_PRIVATE);
        String email = preferences.getString(Canteen.EMAIL, null);
        String password = preferences.getString(Canteen.PASSWORD, null);
        if (email != null) {
            emailView.setText(email);
        }
        if (password != null) {
            passwordView.setText(password);
        }
    }

    public void loginClicked(View view) {
        EditText emailView = (EditText) findViewById(R.id.login_email_edittext);
        EditText passwordView = (EditText) findViewById(R.id.login_password_edittext);
        CharSequence email = emailView.getText();
        CharSequence password = passwordView.getText();
        TextView messageView = (TextView) findViewById(R.id.login_message_textview);
        if (email == null || email.length() == 0) {
            messageView.setText("Please type an email");
            return;
        }
        if (password == null || password.length() == 0) {
            messageView.setText("Please type a password");
            return;
        }

        LocalLoginActivity task = new LocalLoginActivity();
        task.execute(email, password);
    }

    public void cancelClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void registerClicked(View view) {
        // Open another Activity that does REST call using POST
        Toast.makeText(getBaseContext(), "Register not ready yet", Toast.LENGTH_SHORT).show();
        /*
        Customer customer = new Customer();
        Intent data = new Intent();
        data.putExtra(DishListActivity.CUSTOMER, customer);
        setResult(RESULT_OK, data);
        finish();
        */
    }

    private class LocalLoginActivity extends LoginTask {
        private ProgressBar progressBar;
        private TextView messageView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.login_progress_progressbar);
            progressBar.setVisibility(View.VISIBLE);
            messageView = (TextView) findViewById(R.id.login_message_textview);
            messageView.setText("");
        }

        @Override
        protected void onPostExecute(Customer customer) {
            super.onPostExecute(customer);
            Log.d(Canteen.LOG_TAG, "Login onPostExecute Customer: " + customer);
            progressBar.setVisibility(View.GONE);
            if (customer == null) {
                TextView messageView = (TextView) findViewById(R.id.login_message_textview);
                messageView.setText("No such email and password");
            } else {
                Toast.makeText(getBaseContext(), "You are in", Toast.LENGTH_SHORT).show();
                //Log.d(Canteen.LOG_TAG, "LoginActivity onPostExecute Customer: " + customer.getEmail() + " " + customer.getPassword());
                SharedPreferences preferences = getSharedPreferences(Canteen.SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                CheckBox keepBox = (CheckBox) findViewById(R.id.login_save_checkbox);
                if (keepBox.isChecked()) {
                    editor.putString(Canteen.EMAIL, customer.getEmail());
                    editor.putString(Canteen.PASSWORD, customer.getPassword());

                } else {
                    editor.remove(Canteen.EMAIL);
                    editor.remove(Canteen.PASSWORD);
                }
                editor.apply();
                Intent data = new Intent();
                data.putExtra(Canteen.CUSTOMER, customer);
                setResult(RESULT_OK, data);
                finish();
            }
        }

        @Override
        protected void onCancelled(Customer customer) {
            super.onCancelled(customer);
            progressBar.setVisibility(View.GONE);
            TextView messageView = (TextView) findViewById(R.id.login_message_textview);
            messageView.setText("Something went wrong: " + customer.getMessage());
        }
    }

}
