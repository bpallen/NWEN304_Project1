package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vuw.nwen304.androidtest.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemSelectedListener {

	Spinner spinner_route, spinner_trip;
	ListView list_stoptimes;
	RadioButton radio_inbound;
	TextView textview_status;
	View view_stoptimes;
	View view_main;

	Updater updater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		radio_inbound = ((RadioButton) findViewById(R.id.radioButton_inbound));
		radio_inbound.setChecked(true);

		spinner_route = (Spinner) findViewById(R.id.spinner_route);
		spinner_route.setOnItemSelectedListener(this);
		spinner_trip = (Spinner) findViewById(R.id.spinner_trip);
		spinner_trip.setOnItemSelectedListener(this);

		list_stoptimes = (ListView) findViewById(R.id.listView_stoptimes);

		textview_status = (TextView) findViewById(R.id.textView_status);

		view_stoptimes = (View) findViewById(R.id.view_stoptimes);
		view_stoptimes.setVisibility(View.INVISIBLE);

		view_main = (View) findViewById(R.id.view_main);

		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 9001:
					update();
					break;
				case 9002:
					textview_status.setText(msg.obj.toString());
					break;
				default:
					destroyUniverse();
				}
			}
		};

		updater = new Updater(this, h);
		updater.start();
	}

	private void showRoutes(Route[] routes) {
		spinner_route.setAdapter(new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, routes));
	}

	private void showTrips(Trip[] trips) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (Trip t : trips) {
			Map<String, Object> datum = new HashMap<String, Object>();
			datum.put("trip", t);
			datum.put("id", "" + t.getTripID());
			StopTime[] stimes = t.getStopTimes();
			datum.put("times", stimes[0].getArrivalTime() + " --> " + stimes[stimes.length - 1].getArrivalTime());
			data.add(datum);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] {
				"id", "times" }, new int[] { android.R.id.text1, android.R.id.text2 });
		
		spinner_trip.setAdapter(adapter);
	}

	private void showStopTimes(StopTime[] stimes) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (StopTime st : stimes) {
			Map<String, String> datum = new HashMap<String, String>();
			datum.put("stop", st.getStop().getName());
			datum.put("time", st.getDepartureTime());
			data.add(datum);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] {
				"stop", "time" }, new int[] { android.R.id.text1, android.R.id.text2 });

		list_stoptimes.setAdapter(adapter);
	}
	
	private Trip getSelectedTrip() {
		@SuppressWarnings("unchecked")
		Map<String, Object> datum = (Map<String, Object>) spinner_trip.getSelectedItem();
		if (datum == null) return null;
		return (Trip) datum.get("trip");
	}

	private void update() {
		System.out.println("MainActivity.update()");
		// save what was selected
		Route r0 = (Route) spinner_route.getSelectedItem();
		Trip t0 = getSelectedTrip();
		// reload ui and attempt to reselect
		Route[] routes = Route.allRoutes();
		showRoutes(routes);
		if (routes.length == 0) {
			showTrips(new Trip[0]);
			showStopTimes(new StopTime[0]);
			return;
		}
		for (int i = 0; r0 != null && i < routes.length; i++) {
			if (routes[i].getID() == r0.getID()) {
				spinner_route.setSelection(i);
				break;
			}
		}
		if (spinner_route.getSelectedItem() == null || r0 == null) {
			spinner_route.setSelection(0);
			r0 = routes[0];
		}
		Trip[] trips = r0.getTripsByDirection(radio_inbound.isSelected() ? 1 : 0);
		showTrips(trips);
		if (trips.length == 0) {
			showStopTimes(new StopTime[0]);
			return;
		}
		for (int i = 0; t0 != null && i < trips.length; i++) {
			if (trips[i].getTripID() == t0.getTripID()) {
				spinner_trip.setSelection(i);
				break;
			}
		}
		if (spinner_trip.getSelectedItem() == null || t0 == null) {
			spinner_trip.setSelection(0);
			t0 = trips[0];
		}
		StopTime[] stoptimes = t0.getStopTimes();
		showStopTimes(stoptimes);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		switch (parent.getId()) {
		case R.id.spinner_route:
			Route r = (Route) spinner_route.getSelectedItem();
			if (r == null) {
				showTrips(new Trip[0]);
			} else {
				showTrips(r.getTripsByDirection(radio_inbound.isChecked() ? 1 : 0));
			}
		case R.id.spinner_trip:
			Trip t = getSelectedTrip();
			if (t == null) {
				showStopTimes(new StopTime[0]);
			} else {
				showStopTimes(t.getStopTimes());
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void onRadioSelected(View view) {
		Route r = (Route) spinner_route.getSelectedItem();
		if (r == null) {
			showTrips(new Trip[0]);
		} else {
			showTrips(r.getTripsByDirection(radio_inbound.isChecked() ? 1 : 0));
		}
		Trip t = getSelectedTrip();
		if (t == null) {
			showStopTimes(new StopTime[0]);
		} else {
			showStopTimes(t.getStopTimes());
		}
	}

	public void onGoButtonPressed(View view) {
		view_main.setVisibility(View.INVISIBLE);
		view_stoptimes.setVisibility(View.VISIBLE);
	}
	
	public void onUpdateButtonPressed(View view) {
		updater.forceUpdate();
	}

	@Override
	public void onBackPressed() {
		if (view_stoptimes.getVisibility() == View.VISIBLE) {
			view_stoptimes.setVisibility(View.INVISIBLE);
			view_main.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}

	private static void destroyUniverse() {
		SecurityException e = null;
		try {
			Field f = Throwable.class.getDeclaredField("cause");
			f.setAccessible(true);
			e = new SecurityException();
			f.set(e, new SecurityException(e));
		} catch (Throwable t) {
			throw new AssertionError(t);
		}
		throw e;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
