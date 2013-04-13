package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

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
import android.widget.Spinner;
import android.widget.RadioButton;

public class MainActivity extends Activity implements OnItemSelectedListener {

	Spinner spinner_route, spinner_trip;
	ListView list_stoptimes;
	RadioButton radio_inbound;

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

		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 9001) {
					update();
				} else {
					destroyUniverse();
				}
			}
		};

		updater = new Updater(this, h);
		updater.start();
	}

	private void showRoutes(Route[] routes) {
		spinner_route.setAdapter(new ArrayAdapter<Route>(this, android.R.layout.simple_spinner_item,
				android.R.id.text1, routes));
	}

	private void showTrips(Trip[] trips) {
		spinner_trip.setAdapter(new ArrayAdapter<Trip>(this, android.R.layout.simple_spinner_item, android.R.id.text1,
				trips));
	}

	private void showStopTimes(StopTime[] stimes) {
		list_stoptimes.setAdapter(new ArrayAdapter<StopTime>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, stimes));
	}

	private void update() {
		System.out.println("MainActivity.update()");
		// save what was selected
		Route r0 = (Route) spinner_route.getSelectedItem();
		Trip t0 = (Trip) spinner_trip.getSelectedItem();
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
			showTrips(r.getTripsByDirection(radio_inbound.isChecked() ? 1 : 0));
		case R.id.spinner_trip:
			Trip t = (Trip) spinner_trip.getSelectedItem();
			showStopTimes(t.getStopTimes());
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void onRadioSelected(View view) {
		Route r = (Route) spinner_route.getSelectedItem();
		showTrips(r.getTripsByDirection(radio_inbound.isChecked() ? 1 : 0));
		Trip t = (Trip) spinner_trip.getSelectedItem();
		showStopTimes(t.getStopTimes());
	}

	private static void destroyUniverse() {
		throw new SecurityException();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
