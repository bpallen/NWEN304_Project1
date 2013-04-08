package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class Trip {

	private static Trips trips;

	public static class Trips {

		private final Map<Integer, Trip> trips = new HashMap<Integer, Trip>();
		private final Map<Integer, Set<Trip>> trips_by_routeid = new HashMap<Integer, Set<Trip>>();

		public Trips(InputStream is) {
			trips.clear();
			try {
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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

	public int getDirection() {
		return direction;
	}

	public Route getRoute() {
		return Route.routeByID(route_id);
	}

	public StopTime[] getStopTimes() {
		return StopTime.stopTimesByTripID(id);
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

	public static Trips parseTrips(InputStream is) {
		return new Trips(is);
	}

	public static void useTrips(Trips t) {
		trips = t;
	}

	public static Trip tripByID(int id) {
		return trips.tripByID(id);
	}

	public static Trip[] tripsByRouteID(int id) {
		return trips.tripsByRouteID(id);
	}

}
