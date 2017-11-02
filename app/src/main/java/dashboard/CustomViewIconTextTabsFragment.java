package dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import util.Utils;

@SuppressLint("ValidFragment")

public class CustomViewIconTextTabsFragment extends Fragment {

    private Toolbar toolbar;
    String trackerID;
    public static TabLayout tabLayout;
    private ViewPager viewPager;
    Context context;
    boolean select1;

    public CustomViewIconTextTabsFragment() {

    }
    public CustomViewIconTextTabsFragment(boolean bool) {
        select1 = bool;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        context = getActivity();
        Fabric.with(context, new Crashlytics());
        v = inflater.inflate(R.layout.activity_custom_view_icon_text_tabs, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

        trackerID = Utils.getPreferences("TrackerID",context);

//        Toast.makeText(context, "Login fail", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().beginTransaction().commit();
        setupViewPager(viewPager);
//        viewPager.getOffscreenPageLimit();

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        int pages =  viewPager.getOffscreenPageLimit();
        Log.d("new pages ", "new pages " + pages);

        if(select1 == true)
            selectPage(1);
        return v;
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tab1 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tab1.setText("DASHBOARD");
        tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dashboard, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tab1);
        tab1.setSelected(true);

        TextView tab2 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tab2.setText("NAVIGATION");
        tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.navigation, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tab2);

        TextView tab3 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tab3.setText("TRIPS");
        tab3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.trips, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tab3);

        TextView tab4 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tab4.setText("NOTIFICATION");
        tab4.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.notification, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tab4);

        TextView tab5 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tab5.setText("CAR INFO");
        tab5.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.carinfo, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tab5);

//        TextView tab6 = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
//        tab6.setText("SOS");
//        tab6.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sos_tap_icon, 0, 0);
//        tabLayout.getTabAt(5).setCustomView(tab6);

    }

    /**
     * Adding fragments to ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new DashboardFragment1(), "Dashboard");
        adapter.addFrag(new NavigationFragment(), "Navigation");
        adapter.addFrag(new TripsFragment(), "Trips");
        adapter.addFrag(new NotificationFragment(), "Alerts");
        adapter.addFrag(new CarInfoFragment(), "Car Info");
//        adapter.addFrag(new SosFragment(), "Sos");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        int pages =  viewPager.getOffscreenPageLimit();
        Log.d("pages","pages"+pages);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void selectPage(int index){
        viewPager.setCurrentItem(index);
    }

}
