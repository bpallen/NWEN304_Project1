package nz.ac.vuw.nwen304.p1.allenbenj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public class Stop {

	private static Map<Integer, Stop> stops = new HashMap<Integer, Stop>();

	private int id;
	private String name;
	private double latitude, longitude;

	static {
		// load from cache?
	}

	private Stop() {

	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Trip[] getTrips() {
		StopTime[] stimes = StopTime.stopTimesByStopID(id);
		Set<Trip> trips = new HashSet<Trip>();
		for (StopTime st : stimes) {
			trips.add(st.getTrip());
		}
		Trip[] trips_arr = new Trip[trips.size()];
		trips.toArray(trips_arr);
		return trips_arr;
	}

	public Route[] getRoutes() {
		StopTime[] stimes = StopTime.stopTimesByStopID(id);
		Set<Route> routes = new HashSet<Route>();
		for (StopTime st : stimes) {
			routes.add(st.getRoute());
		}
		Route[] routes_arr = new Route[routes.size()];
		routes.toArray(routes_arr);
		return routes_arr;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Stop other = (Stop) obj;
		if (id != other.id) return false;
		return true;
	}

	public static void parseStops(InputStream is) {
		stops.clear();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			Stop s = null;
			for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("RECORD")) {
						s = new Stop();
					} else if (name.equalsIgnoreCase("STOP_ID")) {
						s.id = Integer.parseInt(parser.nextText());
						stops.put(s.id, s);
					} else if (name.equalsIgnoreCase("STOP_NAME")) {
						s.name = parser.nextText();
					} else if (name.equalsIgnoreCase("STOP_LAT")) {
						s.latitude = Double.parseDouble(parser.nextText());
					} else if (name.equalsIgnoreCase("STOP_LON")) {
						s.longitude = Double.parseDouble(parser.nextText());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Stop stopByID(int id) {
		return stops.get(id);
	}

}
