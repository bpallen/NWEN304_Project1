package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import vuw.nwen304.androidtest.R;

import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((RadioButton) findViewById(R.id.radioButton_inbound)).setSelected(true);

		try {
			URL url_routes = new URL("http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/routes.xml");
			URL url_trips = new URL("http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/trips.xml");
			URL url_stops = new URL("http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/stops.xml");
			URL url_stop_times = new URL("http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/stop_times.xml");

			Route.parseRoutes(url_routes.openStream());
			Trip.parseTrips(url_trips.openStream());
			Stop.parseStops(url_stops.openStream());
			StopTime.parseStopTimes(url_stop_times.openStream());

		} catch (Exception e) {
			// o noes
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		spinner_route = (Spinner) findViewById(R.id.spinner_route);
		spinner_route.setOnItemSelectedListener(this);
		spinner_trip = (Spinner) findViewById(R.id.spinner_trip);
		spinner_trip.setOnItemSelectedListener(this);

		spinner_route.setAdapter(new ArrayAdapter<Route>(this, android.R.layout.simple_spinner_item,
				android.R.id.text1, Route.allRoutes()));

		list_stoptimes = (ListView) findViewById(R.id.listView_stoptimes);
	}

	private void showTrips(Trip[] trips) {
		spinner_trip.setAdapter(new ArrayAdapter<Trip>(this, android.R.layout.simple_spinner_item, android.R.id.text1,
				trips));
	}

	private void showStopTimes(StopTime[] stimes) {
		list_stoptimes.setAdapter(new ArrayAdapter<StopTime>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, stimes));
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		switch (parent.getId()) {
		case R.id.spinner_route:
			Route r = (Route) spinner_route.getSelectedItem();
			// TODO inbound / outbound
			showTrips(r.getTrips());
			break;
		case R.id.spinner_trip:
			Trip t = (Trip) spinner_trip.getSelectedItem();
			showStopTimes(t.getStopTimes());
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		switch (parent.getId()) {
		case R.id.spinner_route:

			break;
		case R.id.spinner_trip:

			break;
		}
	}

	public void onRadioSelected(View view) {
		switch (view.getId()) {
		case R.id.radioButton_inbound:

			break;
		case R.id.radioButton_outbound:

			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
