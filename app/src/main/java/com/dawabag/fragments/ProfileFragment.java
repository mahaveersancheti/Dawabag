package com.dawabag.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.activities.AddAddressActivity;
import com.dawabag.activities.AddressesActivity;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.LoginActivity;
import com.dawabag.activities.PatientsActivity;
import com.dawabag.activities.PrescriptionsActivity;
import com.dawabag.activities.WebviewActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Profile;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listView;
    ArrayList<Profile> alItem;
    PrefManager prefManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefManager = new PrefManager(getActivity());

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


        listView = (ListView) view.findViewById(R.id.lvProfile);

        alItem = new ArrayList<>();
        if(prefManager.getIsLogin()) {
            Profile profile = new Profile(); profile.setName("Profile"); profile.setImage(R.drawable.user); alItem.add(profile);
            profile = new Profile(); profile.setName("Prescriptions"); profile.setImage(R.drawable.prescription); alItem.add(profile);
            profile = new Profile(); profile.setName("Patients"); profile.setImage(R.drawable.patient); alItem.add(profile);
            profile = new Profile(); profile.setName("Addresses"); profile.setImage(R.drawable.address); alItem.add(profile);
            profile = new Profile(); profile.setName("Need help?"); profile.setImage(R.drawable.help); alItem.add(profile);
            profile = new Profile(); profile.setName("Legal"); profile.setImage(R.drawable.legal); alItem.add(profile);
            profile = new Profile(); profile.setName("Logout"); profile.setImage(R.drawable.logout); alItem.add(profile);
        } else {
            Profile profile = new Profile(); profile.setName("Login"); profile.setImage(R.drawable.logout); alItem.add(profile);
            profile = new Profile(); profile.setName("Need help?"); profile.setImage(R.drawable.help); alItem.add(profile);
            profile = new Profile(); profile.setName("Legal"); profile.setImage(R.drawable.legal); alItem.add(profile);
        }

        listView.setAdapter(new ItemAdapter(getActivity(), alItem));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = alItem.get(i).getName();
                switch (str) {
                    case "Prescriptions" :
                        Intent prescription = new Intent(getActivity(), PrescriptionsActivity.class);
                        prescription.putExtra("frm", "profile");
                        startActivity(prescription);
                        break;
                    case "Addresses" :
                        Intent addresses = new Intent(getActivity(), AddressesActivity.class);
                        addresses.putExtra("frm", "profile");
                        startActivity(addresses);
                        break;
                    case "Logout" :
                        prefManager.clearPreference();
                        prefManager.setFirstTimeLaunch(false);
                        Intent login = new Intent(getActivity(), LoginActivity.class);
                        startActivity(login);
                        getActivity().finish();
                        break;
                    case "Profile" :
                        Fragment fragment = null;
                        fragment = new UserProfileFragment();
                        if(fragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_container, fragment);
                            transaction.commit();
                        }
                        break;
                    case "Need help?" :
                        Intent intent = new Intent(getActivity(), WebviewActivity.class);
                        intent.putExtra("forWhat", "needHelp");
                        intent.putExtra("url", Config.BASE_URL + "test.php");
                        startActivity(intent);
                        break;
                    case "Legal" :
                        Intent intent1 = new Intent(getActivity(), WebviewActivity.class);
                        intent1.putExtra("forWhat", "privacy");
                        intent1.putExtra("url", Config.BASE_URL + "test.php");
                        startActivity(intent1);
                        break;
                    case "Patients" :
                        Intent patient = new Intent(getActivity(), PatientsActivity.class);
                        patient.putExtra("frm", "profile");
                        startActivity(patient);
                        break;
                    default :
                        Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        return view;
    }

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Profile> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Profile> arrayList) {
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

            final Profile item = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_profile, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvName);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvTitle.setText(item.getName());
            holder.ivIcon.setImageResource(item.getImage());

            return convertView;
        }
    }
    class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
    }
}