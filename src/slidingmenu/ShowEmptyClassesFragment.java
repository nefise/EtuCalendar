package slidingmenu;

import info.androidhive.slidingmenu.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ShowEmptyClassesFragment extends Fragment{

	public static final String ARG_SECTION_NUMBER = "section_number";
	
	public ShowEmptyClassesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_showemptyclasses, container, false);
         
        return rootView;
    }
}
