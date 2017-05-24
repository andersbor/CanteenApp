package com.example.easj.canteen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easj.canteen.model.Customer;
import com.example.easj.canteen.model.Dish;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DishListActivity extends AppCompatActivity {
    public static final String CUSTOMER = "CUSTOMER";

    private static final String DISHES_URI = "http://anbo-canteen.azurewebsites.net/Service1.svc/dishes";

    private Customer customer = null;
    final List<Dish> dishes = new ArrayList<>();
    private int totalEnergy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Canteen.LOG_TAG, "onCreate");
        setContentView(R.layout.activity_dish_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Canteen.LOG_TAG, "DishListActivity onResume()");
        tryLogin();
    }

    private void tryLogin() {
        SharedPreferences preferences = getSharedPreferences(Canteen.SHARED_PREFERENCES, MODE_PRIVATE);
        Log.d(Canteen.LOG_TAG, preferences.getAll().toString());
        String email = preferences.getString(Canteen.EMAIL, null);
        String password = preferences.getString(Canteen.PASSWORD, null);
        //email = "anbo@easj.dk";
        //password = "secret12";
        Log.d(Canteen.LOG_TAG, "username: " + email);
        if (email != null && password != null) {
            LoginTask loginTask = new LoginTask();
            String fullUri = "http://anbo-canteen.azurewebsites.net/Service1.svc/customers/" + email + "/" + password;
            loginTask.execute(fullUri);
        }
        ReadTask readTask = new ReadTask();
        readTask.execute(DISHES_URI);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tryLogin();
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
        //Toast.makeText(this, "Login not ready yet", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivityForResult(intent, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42) {
            if (resultCode == RESULT_CANCELED) {
                // Do nothing
            } else {
                customer = (Customer) data.getSerializableExtra(Canteen.CUSTOMER);
                invalidateOptionsMenu();
            }
        }
    }

    private class LoginTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            Log.d(Canteen.LOG_TAG, "User jsonString: " + jsonString);
            if (jsonString == null || jsonString.length() == 0) {
                return;
            }
            try {
                JSONObject obj = new JSONObject(jsonString.toString());
                int id = obj.getInt("Id");
                String firstname = obj.getString("Firstname");
                String lastname = obj.getString("Lastname");
                String email = obj.getString("Email");
                String pictureUrl = obj.getString("PictureUrl");
                String password = obj.getString("Password");
                customer = new Customer(id, firstname, lastname, email, password, pictureUrl);
                invalidateOptionsMenu();
            } catch (JSONException ex) {
                Log.e(Canteen.LOG_TAG, ex.getMessage());
            }
        }

        @Override
        protected void onCancelled(CharSequence message) {
            Log.d(Canteen.LOG_TAG, message.toString());
        }
    }

    private class ReadTask extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.dishlist_progress_progressbar);
            progressBar.setVisibility(View.GONE);
            TextView messageTextView = (TextView) findViewById(R.id.dishlist_message_textview);
            //messageTextView.setText(charSequence);

            try {
                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("Id");
                    String title = obj.getString("Title");
                    String description = obj.getString("Description");
                    int energy = obj.getInt("Energy");
                    double price = obj.getDouble("Price");
                    double alcohol = obj.getDouble("Alcohol");
                    double protein = obj.getDouble("Protein");
                    double fat = obj.getDouble("Fat");
                    double weight = obj.getDouble("Weight");
                    double carbohydrates = obj.getDouble("Carbohydrates");
                    String pictureUrl = obj.getString("PictureUrl");
                    Dish book = new Dish(id, title, description, energy, alcohol, carbohydrates, protein, fat, weight, price, pictureUrl);
                    dishes.add(book);
                }
                ListView listView = (ListView) findViewById(R.id.dishlist_dishes_listview);
                //ArrayAdapter<Dish> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, dishes);
                DishListItemAdapter adapter = new DishListItemAdapter(getBaseContext(), R.layout.dishlist_item, dishes);
                listView.setAdapter(adapter);
                /* click listener set in Adapter to make it work with check box
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), DishActivity.class);
                        intent.putExtra(DishActivity.DISH, dishes.get((int) id));
                        Log.d(LOG_TAG, "DisListActivity: Customer: " + customer);
                        intent.putExtra(CUSTOMER, customer);
                        startActivity(intent);
                    }
                });
                */
            } catch (JSONException ex) {
                messageTextView.setText(ex.getMessage());
                Log.e(Canteen.LOG_TAG, ex.getMessage());
            }
        }

        @Override
        protected void onCancelled(CharSequence message) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.dishlist_progress_progressbar);
            progressBar.setVisibility(View.GONE);
            TextView messageTextView = (TextView) findViewById(R.id.dishlist_message_textview);
            messageTextView.setText(message);
            Log.e(Canteen.LOG_TAG, message.toString());
        }
    }

    private class DishListItemAdapter extends ArrayAdapter<Dish> {
        private final int resource;

        public DishListItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Dish> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Dish dish = getItem(position);
            String title = dish.getTitle();
            double price = dish.getPrice();
            LinearLayout dishView;
            if (convertView == null) {
                dishView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
                li.inflate(resource, dishView, true);
            } else {
                dishView = (LinearLayout) convertView;
            }
            CheckBox checkbox = (CheckBox) dishView.findViewById(R.id.dishlist_item_checkbox);
            TextView titleView = (TextView) dishView.findViewById(R.id.dishlist_item_title);
            TextView priceView = (TextView) dishView.findViewById(R.id.dishlist_item_price);

            LinearLayout layout = (LinearLayout) dishView.findViewById(R.id.dishlist_info_layout);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(Canteen.LOG_TAG, "CheckBox " + position + " " + isChecked);
                    Dish dish = dishes.get(position);
                    if (isChecked) {
                        totalEnergy += dish.getEnergy();
                    } else {
                        totalEnergy -= dish.getEnergy();
                    }

                    TextView totalEnergyView = (TextView) findViewById(R.id.dishlist_total_energy_view);
                    totalEnergyView.setText("Total energy: " + totalEnergy + " of " + 11800);
                    if (totalEnergy <= 11800 * 0.66) {
                        totalEnergyView.setBackgroundColor(Color.GREEN);
                    } else if (totalEnergy <= 11800) {
                        totalEnergyView.setBackgroundColor(Color.YELLOW);
                    } else {
                        // http://www.apoteket.dk/KropOgHelbred/Kost/Anbefalinger/De%20nordiske%20n%C3%A6ringsstofanbefalinger.aspx
                        totalEnergyView.setBackgroundColor(Color.RED);
                    }
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(Canteen.LOG_TAG, "LinearLayout clicked: " + position);
                    Intent intent = new Intent(getBaseContext(), DishActivity.class);
                    intent.putExtra(DishActivity.DISH, dishes.get(position));
                    intent.putExtra(CUSTOMER, customer);
                    startActivity(intent);
                }
            });
            titleView.setText(title);
            priceView.setText("price " + price);
            ImageView imageView = (ImageView) dishView.findViewById(R.id.dishlist_item_image);
            Picasso.with(getBaseContext()).load(dish.getPictureUrl()).into(imageView);
            return dishView;
        }
    }
}