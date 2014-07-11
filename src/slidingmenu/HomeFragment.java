package slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.slidingmenu.R;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import database.DatabaseProcessing;

public class HomeFragment extends Fragment implements TextWatcher{

	DatabaseProcessing db;
	AutoCompleteTextView quickSearch;
	ListView selectedList;
	ImageButton minus;
	Button go;
	CheckBox checkBox_student, checkBox_instructor, checkBox_department, checkBox_classroom, checkBox_course;
	ArrayList<HashMap<String, String>> suggestionList;
	SimpleAdapter adapter;
	ArrayList<HashMap<String, String>> selectedPrograms = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> selectedSuggestions;
	
	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		db = new DatabaseProcessing(getActivity());
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		selectedList = (ListView) rootView.findViewById(R.id.listView1);
		quickSearch = (AutoCompleteTextView) rootView.findViewById(R.id.quickSearch);
		quickSearch.setLines(1);
		quickSearch.addTextChangedListener(this);
		quickSearch.setMaxLines(1);
		
		quickSearch.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				String input = quickSearch.getText().toString();
				String id = input.substring( input.indexOf("id") + 3, input.indexOf(", suggestion_type"));
				String suggestion_type = input.substring( input.indexOf("suggestion_type") + 16, input.indexOf(", suggestion=") + 11);
				String suggestion = input.substring( input.indexOf("suggestion=") + 11, input.length() - 1);
				
				System.out.println(id + " " + suggestion_type + " " + suggestion);
				quickSearch.setText("");

				selectedSuggestions = new HashMap<String, String>();
				selectedSuggestions.put("suggestion", suggestion);
				selectedSuggestions.put("suggestion_type", suggestion_type);
				selectedSuggestions.put("id", id);
				selectedPrograms.add(selectedSuggestions);
                adapter.notifyDataSetChanged();
			}
			
		});
		
		minus = (ImageButton) rootView.findViewById(R.id.imageButton1);
		minus.setOnClickListener(new OnClickListener() {
		 
		public void onClick(View arg0) {
			
			//LinearLayout ll = (LinearLayout)arg0.getParent();
//			int position = selectedList.getPositionForView((View) arg0.getParent());
			//int position =  arg0.getTag();
		//	System.out.println("position: " + position);
			adapter.notifyDataSetChanged();
		}

	});
		
		go = (Button) rootView.findViewById(R.id.button1);
		go.setOnClickListener(new OnClickListener() {
			
			Fragment fragment;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				Bundle bundle=new Bundle();
				bundle.putString("message", "showStudentProgram");
				fragment = new TabbedActivity();
				fragment.setArguments(bundle);
				
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
			}
		});

		
		checkBox_student = (CheckBox)rootView.findViewById(R.id.checkBox1);
		checkBox_instructor = (CheckBox)rootView.findViewById(R.id.checkBox2);
		checkBox_department = (CheckBox)rootView.findViewById(R.id.checkBox3);
		checkBox_classroom = (CheckBox)rootView.findViewById(R.id.checkBox4);
		checkBox_course = (CheckBox)rootView.findViewById(R.id.checkBox5);
				
		String[] from = new String[] {"suggestion"};
        int[] to = new int[] { R.id.suggestion };
        adapter = new SimpleAdapter(getActivity(), selectedPrograms, R.layout.selected_row, from, to);         
        selectedList.setAdapter(adapter);
        
		return rootView;
	}
     
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if(quickSearch.getText().length() >= 3){
			
		suggestionList = new ArrayList<HashMap<String, String>>();
				
			ArrayList<HashMap<String, String>> dump;
			if(checkBox_student.isChecked()){
				
				dump = db.getStudents(quickSearch.getText().toString());
				if(dump.size() != 0)
						suggestionList.addAll(dump);
			}
			if(checkBox_instructor.isChecked()){
				
				dump = db.getInstructors(quickSearch.getText().toString());
				if(dump.size() != 0)
					suggestionList.addAll(dump);
			}
			if(checkBox_department.isChecked()){
				
				dump = db.getDepartment(quickSearch.getText().toString());
				if(dump.size() != 0)
					suggestionList.addAll(dump);
			}
			if(checkBox_classroom.isChecked()){
				
				dump = db.getClassroom(quickSearch.getText().toString());
				if(dump.size() != 0)
					suggestionList.addAll(dump);
			}
			if(checkBox_course.isChecked()){
				
				dump = db.getCourseByCode(quickSearch.getText().toString());
				if(dump.size() != 0)
					suggestionList.addAll(dump);
			}
			if(checkBox_course.isChecked()){
				
				dump = db.getCourseByName(quickSearch.getText().toString());
				if(dump.size() != 0)
					suggestionList.addAll(dump);
			}
			
		System.out.println("sl " + suggestionList);
		SimpleAdapter adapter = new SimpleAdapter(
                    getActivity(), suggestionList,
                    R.layout.autocomplete_list_item, new String[] { "suggestion", "suggestion_type"},
                    new int[] { R.id.from, R.id.subject });
            
			quickSearch.setAdapter(adapter);
		 }
	}
}