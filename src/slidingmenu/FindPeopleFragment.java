package slidingmenu;

import database.DatabaseProcessing;
import info.androidhive.slidingmenu.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FindPeopleFragment extends Fragment {
	
	public FindPeopleFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_find_people, container, false);
         DatabaseProcessing db = new DatabaseProcessing(getActivity());
        TextView textViewToChange = (TextView) rootView.findViewById(R.id.quickSearchCourse);
     
        return rootView;
    }
}
