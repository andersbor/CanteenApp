package com.example.easj.canteen;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easj.canteen.model.Dish;

import java.util.List;

public class DishListItemAdapter extends ArrayAdapter<Dish> {
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
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Canteen.LOG_TAG, "LinearLayout clicked: " + position);
            }
        });
        titleView.setText(title);
        priceView.setText("price " + price);
        return dishView;
    }
}
