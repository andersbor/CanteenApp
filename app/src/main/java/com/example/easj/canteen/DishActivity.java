package com.example.easj.canteen;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easj.canteen.model.Customer;
import com.example.easj.canteen.model.Dish;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DishActivity extends AppCompatActivity {
    public static final String DISH = "DISH";
    private Dish dish;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        Intent intent = getIntent();
        dish = (Dish) intent.getSerializableExtra(DISH);
        customer = (Customer) intent.getSerializableExtra(DishListActivity.CUSTOMER);

        TextView titleView = (TextView) findViewById(R.id.dish_title_textview);
        titleView.setText(dish.getTitle());

        //WebView pictureView = (WebView) findViewById(R.id.dish_picture_webview);
        //pictureView.loadUrl(dish.getPictureUrl());

        ImageView imageView = (ImageView) findViewById(R.id.dish_image_imageview);
        Picasso.with(getBaseContext()).load(dish.getPictureUrl()).into(imageView);

        TextView carboGramView = (TextView) findViewById(R.id.dish_carbogram_textview);
        carboGramView.setText(Double.toString(dish.getCarbohydrates()) + " gram");

        TextView carboPercentView = (TextView) findViewById(R.id.dish_carbopercent_textview);
        final int carbohydratesPercent = (int) Math.round(dish.getCarbohydrates() * 18 / dish.getEnergy() * 100);
        carboPercentView.setText(carbohydratesPercent + "%");
        if (carbohydratesPercent < 55) {
            carboPercentView.setBackgroundColor(Color.YELLOW);
        }
        if (carbohydratesPercent > 60) {
            carboPercentView.setBackgroundColor(Color.RED);
        }

        TextView fatGramView = (TextView) findViewById(R.id.dish_fatgram_textview);
        fatGramView.setText(Double.toString(dish.getFat()) + " gram");
        TextView fatPercentView = (TextView) findViewById(R.id.dish_fatpercent_textview);
        int fatPercent = (int) Math.round(dish.getCarbohydrates() * 17.7 / dish.getEnergy() * 100);
        fatPercentView.setText(fatPercent + "%");
        if (fatPercent > 33) {
            carboPercentView.setBackgroundColor(Color.RED);
        }

        TextView proteinGramView = (TextView) findViewById(R.id.dish_proteingram_textview);
        proteinGramView.setText(Double.toString(dish.getProtein()) + " gram");

        TextView energyView = (TextView) findViewById(R.id.dish_energy_textview);
        energyView.setText("Energy " + dish.getEnergy() + " kJ");

        RatingBar averageRatingBar = (RatingBar) findViewById(R.id.dish_average_rating_ratingbar);
        averageRatingBar.setRating(2.5f);

        RatingBar userRatingBar = (RatingBar) findViewById(R.id.dish_user_rating_ratingbar);
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(Canteen.LOG_TAG, "Rating: " + rating);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        Log.d(Canteen.LOG_TAG, "onCreateOptionsMenu " + customer);
        MenuItem logout = menu.findItem(R.id.menu_general_action_logout);
        if (customer != null) {
            MenuItem button = menu.findItem(R.id.menu_general_action_user);
            button.setTitle(customer.getEmail());
            logout.setVisible(true);
        } else {
            logout.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_general_action_user:
                Log.d(Canteen.LOG_TAG, "Menu user");
                if (item.getTitle().equals("Login")) {
                    login();
                } else {
                    showCustomerInfo();
                }
                break;
            case R.id.menu_general_action_logout:
                customer = null;
                invalidateOptionsMenu();
                break;
        }
        return false;
    }

    private void showCustomerInfo() {
        Toast.makeText(this, customer.toString(), Toast.LENGTH_LONG).show();
    }

    private void login() {
        Toast.makeText(this, "Login not ready yet", Toast.LENGTH_SHORT).show();
    }


    public void sendRating(View view) {
        if (customer == null) {
            Toast.makeText(getBaseContext(), "Please, login", Toast.LENGTH_LONG).show();
        } else {
            int customerId = customer.getId();
            int dishId = dish.getId();
            RatingBar userRatingBar = (RatingBar) findViewById(R.id.dish_user_rating_ratingbar);
            float ratingFloat = userRatingBar.getRating();
            int rating = Math.round(ratingFloat);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("DishId", dishId);
                jsonObject.put("CustomerId", customerId);
                jsonObject.put("Rate", rating);
                String jsonDocument = jsonObject.toString();
                Log.d(Canteen.LOG_TAG, jsonDocument);
                PostRatingTask task = new PostRatingTask();
                task.execute("http://anbo-canteen.azurewebsites.net/Service1.svc/ratings", jsonDocument);
            } catch (JSONException ex) {
                TextView messageView = (TextView) findViewById(R.id.dish_message_textview);
                messageView.setText(ex.getMessage());
            }
        }
    }

    public void takeawayClicked(View view) {
        if (customer == null) {
            Toast.makeText(getBaseContext(), "To order takeaway you must first login", Toast.LENGTH_SHORT).show();
            //Alternative: TextView
        } else {
            //Toast.makeText(getBaseContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), TakeawayActivity.class);
            intent.putExtra(Canteen.DISH, dish);
            intent.putExtra(Canteen.CUSTOMER, customer);
            startActivity(intent);
        }
    }

    private class PostRatingTask extends AsyncTask<String, Void, CharSequence> {

        @Override
        protected CharSequence doInBackground(String... params) {
            String urlString = params[0];
            String jsonDocument = params[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(jsonDocument);
                osw.flush();
                osw.close();
                int responseCode = connection.getResponseCode();
                if (responseCode / 100 != 2) {
                    String responseMessage = connection.getResponseMessage();
                    throw new IOException("HTTP response code: " + responseCode + " " + responseMessage);
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                return line;
            } catch (MalformedURLException ex) {
                cancel(true);
                String message = ex.getMessage() + " " + urlString;
                Log.e(Canteen.LOG_TAG, message);
                return message;
            } catch (IOException ex) {
                cancel(true);
                Log.e(Canteen.LOG_TAG, ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            Toast.makeText(getBaseContext(), "Rating OK", Toast.LENGTH_SHORT).show();
            TextView messageView = (TextView) findViewById(R.id.dish_message_textview);
            messageView.setText(charSequence);
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            TextView messageView = (TextView) findViewById(R.id.dish_message_textview);
            messageView.setText(charSequence);
        }
    }
}
