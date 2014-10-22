package com.example.lunchify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class OrganiseLunch extends FragmentActivity implements ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	public static Firebase lunchifyEndpoint;
	public static Firebase restaurantEndpoint;
	public static Firebase timeEndpoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organise_lunch);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);
		
		restaurantEndpoint = new Firebase("https://lunchapp.firebaseio.com/restaurant");
		timeEndpoint = new Firebase("https://lunchapp.firebaseio.com/time");

	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.organise_lunch, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		
		TextView restaurant;
		TextView time;
		EditText restaurantInput;
		EditText timeInput;
		
		Button lunchifyButton;
		

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_organise_lunch_dummy, container, false);
			
			restaurant = (TextView)rootView.findViewById(R.id.restaurant);
			time = (TextView)rootView.findViewById(R.id.time);
			
			restaurantInput = (EditText)rootView.findViewById(R.id.restaurantInput);
			timeInput = (EditText)rootView.findViewById(R.id.timeInput);
			lunchifyButton = (Button)rootView.findViewById(R.id.lunchifyButton);

			lunchifyButton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					
					switch (event.getAction()) {
						case MotionEvent.ACTION_UP:
							sendSuggestionToFirebase();
							break;
						default:
							break;
					}
					return true; //Event was handled
				}
			});
			
			restaurantEndpoint.addValueEventListener(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot data) {
			    	
			    	System.out.println("data is " + data.getValue());
			    	System.out.println("restaurant is: " + restaurant);
			    	if(data != null && data.getValue() != null){
				        restaurant.setText("Place: " + data.getValue().toString());
			    	}
			    }
			    @Override public void onCancelled(FirebaseError error) { }
			});

			timeEndpoint.addValueEventListener(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot data) {
			        time.setText("Time: " + data.getValue().toString());
			    }
			    @Override public void onCancelled(FirebaseError error) { }
			});
			
			return rootView;
		}
		
		private void sendSuggestionToFirebase() {
			if(restaurantEndpoint != null && timeEndpoint != null){
				System.out.println("Attempt to SEND SUGGESTION! --------------------" + restaurantInput);
				restaurantEndpoint.setValue(restaurantInput.getText().toString());
				timeEndpoint.setValue(timeInput.getText().toString());
			}
		}  
		
	}

}
