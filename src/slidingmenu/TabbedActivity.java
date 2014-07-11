package slidingmenu;

import info.androidhive.slidingmenu.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import database.DatabaseProcessing;


//import database.DatabaseHandler;

import android.app.ProgressDialog;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class TabbedActivity extends Fragment {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	DatabaseProcessing db;

	private ProgressDialog pDialog;
	
	// Hashmap for ListView
	static ArrayList <ArrayList<HashMap<String, String>>> lists = new ArrayList <ArrayList<HashMap<String, String>>>();

	public static TabbedActivity newInstance() {
		return new TabbedActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());
		
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
	    String method = getArguments().getString("message");    
		db = new DatabaseProcessing(getActivity());
		
		if(method.equals("showEmptyClasses"))
			lists = db.getEmptyClasses();
		else if(method.equals("showStudentProgram"))
			lists = db.getStudentProgram("101101026");
		return v;
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			case 5:
				return getString(R.string.title_section6).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends ListFragment{
			
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tabbed_dummy,
					container, false);
			
			return rootView;
		}
				  
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			
			String[] from = new String[] {"hour", "prg"};
	        int[] to = new int[] { R.id.item1, R.id.item2 };
	 
	        int page = getArguments().getInt(ARG_SECTION_NUMBER);
	        SimpleAdapter adapter;
	        adapter = new SimpleAdapter(getActivity(), lists.get(page - 1), R.layout.grid_item, from, to);
	        
	        setListAdapter(adapter);
	        
		}

	}

}
