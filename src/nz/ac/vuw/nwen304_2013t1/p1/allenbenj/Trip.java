package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class Trip {

	private static volatile Trips trips;

	public static class Trips {

		private final Map<Integer, Trip> trips;
		private final Map<Integer, Set<Trip>> trips_by_routeid;

		public Trips() {
			trips = new HashMap<Integer, Trip>();
			trips_by_routeid = new HashMap<Integer, Set<Trip>>();
		}

		public Trips(InputStream is) throws Exception {
			trips = new HashMap<Integer, Trip>();
			trips_by_routeid = new HashMap<Integer, Set<Trip>>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			Trip t = null;
			for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("RECORD")) {
						t = new Trip();
					} else if (name.equalsIgnoreCase("TRIP_ID")) {
						t.id = Integer.parseInt(parser.nextText());
						trips.put(t.id, t);
					} else if (name.equalsIgnoreCase("ROUTE_ID")) {
						t.route_id = Integer.parseInt(parser.nextText());
						Set<Trip> rtrips = trips_by_routeid.get(t.route_id);
						if (rtrips == null) {
							rtrips = new HashSet<Trip>();
							trips_by_routeid.put(t.route_id, rtrips);
						}
						rtrips.add(t);
					} else if (name.equalsIgnoreCase("DIRECTION_ID")) {
						t.direction = Integer.parseInt(parser.nextText());
					}
				}
			}
			is.close();
		}

		public Trip tripByID(int id) {
			return trips.get(id);
		}

		public Trip[] tripsByRouteID(int route_id) {
			Set<Trip> trip_set = trips_by_routeid.get(route_id);
			Trip[] trips = new Trip[trip_set.size()];
			trip_set.toArray(trips);
			return trips;
		}

		public Trip[] tripsByRouteIDAndDirection(int route_id, int dir) {
			Set<Trip> trip_set = trips_by_routeid.get(route_id);
			List<Trip> trips0 = new ArrayList<Trip>();
			for (Trip t : trip_set) {
				if (t.getDirection() == dir) {
					trips0.add(t);
				}
			}
			Trip[] trips = new Trip[trips0.size()];
			trips0.toArray(trips);
			return trips;
		}

		/**
		 * Add the information in the specified Trips object to this one. Is not synchronized, so must not be called if
		 * this Trips object is accessible from mutliple threads.
		 * 
		 * @param t
		 */
		public void add(Trips t) {
			trips.putAll(t.trips);
			trips_by_routeid.putAll(t.trips_by_routeid);
		}
	}

	private int id, route_id;
	private int direction;

	private Trip() {

	}

	public int getTripID() {
		return id;
	}

	public int getRouteID() {
		return route_id;
	}

	/**
	 * @return 0 for outbound, 1 for inbound.
	 */
	public int getDirection() {
		return direction;
	}

	public Route getRoute() {
		return Route.routeByID(route_id);
	}

	/**
	 * Sorts by stop sequence.
	 * 
	 * @return
	 */
	public StopTime[] getStopTimes() {
		return StopTime.stopTimesByTripID(id);
	}

	@Override
	public String toString() {
		return "" + id;
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
		Trip other = (Trip) obj;
		if (id != other.id) return false;
		return true;
	}

	public static Trips parseTrips(InputStream is) throws Exception {
		return new Trips(is);
	}

	public static void useTrips(Trips t) {
		trips = t;
	}

	public static Trip tripByID(int id) {
		if (trips == null) return null;
		return trips.tripByID(id);
	}

	public static Trip[] tripsByRouteID(int id) {
		if (trips == null) return new Trip[0];
		return trips.tripsByRouteID(id);
	}

	public static Trip[] tripsByRouteIDAndDirection(int route_id, int dir) {
		if (trips == null) return new Trip[0];
		return trips.tripsByRouteIDAndDirection(route_id, dir);
	}

}
