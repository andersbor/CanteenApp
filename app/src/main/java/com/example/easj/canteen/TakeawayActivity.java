package com.example.easj.canteen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.easj.canteen.model.Customer;
import com.example.easj.canteen.model.Dish;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TakeawayActivity extends AppCompatActivity {
    private Customer customer;
    private Dish dish;
    private int year, month, dayOfMonth, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeaway);
        Intent intent = getIntent();
        dish = (Dish) intent.getSerializableExtra(Canteen.DISH);
        customer = (Customer) intent.getSerializableExtra(Canteen.CUSTOMER);
        Log.d(Canteen.LOG_TAG, "TakeawayActivity " + customer + " " + customer.getId());
        Log.d(Canteen.LOG_TAG, "TakeawayActivity " + dish + " " + dish.getId());
        TextView titleView = (TextView) findViewById(R.id.takeaway_title_textview);
        titleView.setText("Takeaway " + dish.getTitle());
        ImageView imageView = (ImageView) findViewById(R.id.takeaway_image_imageview);
        Picasso.with(getBaseContext()).load(dish.getPictureUrl()).into(imageView);

        Spinner spinner = (Spinner) findViewById(R.id.takeaway_howmany_spinner);
        final TextView totalPriceView = (TextView) findViewById(R.id.takeaway_totalprice_textview);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                totalPriceView.setText("Total price: " + dish.getPrice() * (position + 1));
                // TODO formatting
                // TODO position not good
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                totalPriceView.setText("");
            }
        });
    }

    public void dateViewClicked(View view) {
        final TextView textView = (TextView) view;
        Calendar mcurrentTime = Calendar.getInstance();
        int year = mcurrentTime.get(Calendar.YEAR);
        int month = mcurrentTime.get(Calendar.MONTH);
        int dayOfMonth = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        Log.d(Canteen.LOG_TAG, "dateViewClicked " + year + " " + month + " " + dayOfMonth);
        final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        DatePickerDialog mTimePicker = new DatePickerDialog(TakeawayActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker DatePicker, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        TakeawayActivity.this.year = selectedYear;
                        TakeawayActivity.this.month = selectedMonth;
                        TakeawayActivity.this.dayOfMonth = selectedDayOfMonth;
                        Calendar cal = new GregorianCalendar(selectedYear, selectedMonth, selectedDayOfMonth);
                        String dateStr = dateFormat.format(cal.getTime());
                        textView.setText(dateStr);
                    }
                }, year, month, dayOfMonth);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void timeViewClicked(final View view) {
        // http://stackoverflow.com/questions/39634387/timepicker-dialog-when-pressing-edittext
        final TextView textView = (TextView) view;
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        Log.d(Canteen.LOG_TAG, "timeViewClicked " + hour + " " + minute);
        final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);

        final boolean is24HourView = true;
        TimePickerDialog mTimePicker = new TimePickerDialog(TakeawayActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                TakeawayActivity.this.hour = selectedHour;
                TakeawayActivity.this.minute = selectedMinute;
                Calendar cal = new GregorianCalendar();
                cal.set(Calendar.HOUR, selectedHour);
                cal.set(Calendar.MINUTE, selectedMinute);
                String timeStr = timeFormat.format(cal.getTime());
                textView.setText(timeStr);
            }
        }, hour, minute, is24HourView);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    public void orderItClicked(View view) {
        Log.d(Canteen.LOG_TAG, "orderItClicked");
        TextView dateView = (TextView) findViewById(R.id.takeaway_date_textview);
        TextView timeView = (TextView) findViewById(R.id.takeaway_time_textview);
        CharSequence dateString = dateView.getText();
        CharSequence timeString = timeView.getText();
        if (dateString == null || dateString.length() == 0) {
            Toast.makeText(getBaseContext(), "Please enter a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeString == null || timeString.length() == 0) {
            Toast.makeText(getBaseContext(), "Please enter a time", Toast.LENGTH_SHORT).show();
            return;
        }
        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth, hour, minute);
        Log.d(Canteen.LOG_TAG, "orderItClicked " + calendar);
        long timeInMillis = calendar.getTimeInMillis();
        Spinner spinner = (Spinner) findViewById(R.id.takeaway_howmany_spinner);
        Object obj = spinner.getSelectedItem();
        String str = obj.toString();
        int howMany = Integer.parseInt(str);
        Log.d(Canteen.LOG_TAG, "orderItClicked " + howMany);
        int customerId = customer.getId();
        int dishId = dish.getId();
        // AsyncTask: POST
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("DishId", dishId);
            jsonObject.put("CustomerId", customerId);
            jsonObject.put("HowMany", howMany);
            jsonObject.put("PickupDateTime", "/Date(" + timeInMillis + ")/"); // +0000 time zone??
            //jsonObject.put("")
            String jsonDocument = jsonObject.toString();
            Log.d(Canteen.LOG_TAG, jsonDocument);

            PostTakeawayTask task = new PostTakeawayTask();
            task.execute("http://anbo-canteen.azurewebsites.net/Service1.svc/takeaways", jsonDocument);
        } catch (JSONException ex) {
            TextView messageView = (TextView) findViewById(R.id.takeaway_message_textview);
            messageView.setText(ex.getMessage());
            messageView.setText(ex.getMessage());
        }
        // finish();
    }

    private class PostTakeawayTask extends AsyncTask<String, Void, CharSequence> {
        //private final String JsonDocument;

        //PostBookTask(String JsonDocument) {
        //    this.JsonDocument = JsonDocument;
        //}

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
                    Log.d(Canteen.LOG_TAG, "responseCode: " + responseCode);
                    String responseMessage = connection.getResponseMessage();
                    throw new IOException("HTTP response code: " + responseCode + " " + responseMessage);
                }
                /*
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                return line;*/
                return "OK";
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
            TextView messageView = (TextView) findViewById(R.id.takeaway_message_textview);
            messageView.setText("Thanks");
        }

        @Override
        protected void onCancelled(CharSequence charSequence) {
            super.onCancelled(charSequence);
            TextView messageView = (TextView) findViewById(R.id.takeaway_message_textview);
            messageView.setText(charSequence);
        }
    }

}
