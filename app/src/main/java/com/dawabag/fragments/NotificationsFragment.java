package com.dawabag.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.OrdersActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.helpers.Config;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listView;
    ArrayList<Medicine> alItem;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ImageView ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        ImageView ivCart = (ImageView) view.findViewById(R.id.ivCart);

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });
        TextView tvCartCnt = (TextView) view.findViewById(R.id.tvCartCnt);
        tvCartCnt.setText(Config.cartItemCount + "");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) view.findViewById(R.id.lvNotifications);

        alItem = new ArrayList<>();
        Medicine medicine = new Medicine();
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        listView.setAdapter(new ItemAdapter(getActivity(), alItem));

        return view;
    }

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Medicine> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Medicine> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;

            final Medicine item = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_notification, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            if(position%2 == 1) {
//                convertView.setBackgroundColor(Color.parseColor("#F7F6F6"));
//            } else {
//                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            }

//            holder.tvTitle.setText(item.getTitle());
//            holder.tvNote.setText(item.getDescription());
//            holder.tvDate.setText(item.getDate());
//            Glide.with(getActivity())
//                    .load(item.getImage())
//                    //.centerCrop()
//                    .placeholder(R.drawable.noimg)
//                    .into(holder.iv);

            return convertView;
        }
    }
    class ViewHolder {
        TextView tvTitle;
    }
}