package com.example.easj.canteen;

import android.os.AsyncTask;
import android.util.Log;

import com.example.easj.canteen.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by EASJ on 03/05/2017.
 */

public abstract class LoginTask extends AsyncTask<CharSequence, Void, Customer> {
    @Override
    protected Customer doInBackground(CharSequence... params) {
        CharSequence emailStr = params[0];
        CharSequence passwordStr = params[1];
        String urlString = "http://anbo-canteen.azurewebsites.net/Service1.svc/customers/" + emailStr + "/" + passwordStr;
        try {
            CharSequence jsonString = HttpHelper.GetHttpResponse(urlString);
            Log.d(Canteen.LOG_TAG, "User jsonString: " + jsonString);
            if (jsonString == null || jsonString.length() == 0) {
                return null;
            }
            try {
                JSONObject obj = new JSONObject(jsonString.toString());
                int id = obj.getInt("Id");
                String firstname = obj.getString("Firstname");
                String lastname = obj.getString("Lastname");
                String email = obj.getString("Email");
                String pictureUrl = obj.getString("PictureUrl");
                String password = obj.getString("Password");
                Customer customer = new Customer(id, firstname, lastname, email, password, pictureUrl);
                return customer;
            } catch (JSONException ex) {
                Log.e(Canteen.LOG_TAG, ex.getMessage());
                return new Customer(ex.getMessage() + " " + jsonString);
            }
        } catch (IOException ex) {
            cancel(true);
            String errorMessage = ex.getMessage() + "\n" + urlString;
            Log.e("ReadHttpTask", errorMessage);
            return new Customer(ex.getMessage());
        }
    }
}
