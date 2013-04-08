package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

	Updater updater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((RadioButton) findViewById(R.id.radioButton_inbound)).setSelected(true);

		spinner_route = (Spinner) findViewById(R.id.spinner_route);
		spinner_route.setOnItemSelectedListener(this);
		spinner_trip = (Spinner) findViewById(R.id.spinner_trip);
		spinner_trip.setOnItemSelectedListener(this);

		list_stoptimes = (ListView) findViewById(R.id.listView_stoptimes);

		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {

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
