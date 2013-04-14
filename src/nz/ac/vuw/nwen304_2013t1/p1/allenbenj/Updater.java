package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

public class Updater extends Thread {

	private static final String URL_BASE = "http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/";

	private Context c;
	private Handler h;
	
	private volatile boolean force_update = false;

	public Updater(Context c_, Handler h_) {
		c = c_;
		h = h_;
		setDaemon(true);
	}

	@Override
	public void run() {
		// load any cached data
		loadCached();
		h.sendEmptyMessage(9001);
		status("Loaded cached data.");

		long time_last = 0;

		while (true) {
			try {
				if ((System.currentTimeMillis() - time_last < 600000) && !force_update) {
					// keep waiting
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					// check for updates
					force_update = false;
					time_last = System.currentTimeMillis();
					if (doNetUpdate()) {
						loadCached();
					}
					h.sendEmptyMessage(9001);
					status("Loaded up-to-date data.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				status("Error checking for updates.");
			}
		}
	}
	
	public void forceUpdate() {
		force_update = true;
	}

	private void status(String s) {
		System.out.println(s);
		h.sendMessage(Message.obtain(h, 9002, s));
	}

	private File localFile(String fname) {
		return new File(c.getFilesDir().getAbsolutePath() + "/" + fname);
	}

	private void downloadAndSave(String fname) throws Exception {
		status("Downloading " + fname);
		InputStream is = new URL(URL_BASE + fname).openStream();
		OutputStream os = c.openFileOutput(fname, Context.MODE_PRIVATE);
		byte[] buf = new byte[1024];
		int total_read = 0;
		while (true) {
			int read = is.read(buf);
			if (read == -1) break;
			total_read += read;
			os.write(buf, 0, read);
		}
		is.close();
		os.close();
		System.out.println("Download finished, " + total_read + " bytes.");
	}

	private boolean doNetUpdate() throws Exception {
		status("Begin update check.");
		downloadAndSave("index.xml");
		FileIndex index = new FileIndex(c.openFileInput("index.xml"));
		List<String> files_del = new ArrayList<String>();
		for (String fname : c.getFilesDir().list()) {
			files_del.add(fname);
		}
		// slight hack...
		files_del.remove("index.xml");
		int changecount = 0;
		for (String fname : index.filesAll()) {
			if (files_del.contains(fname)) {
				// present
				if (localFile(fname).length() != index.fileSize(fname)) {
					// corrupt (file size not what it should be)
					// i know, i should hash check
					System.err.println("local file <" + fname + "> is corrupt; expected size " + index.fileSize(fname)
							+ ", found size " + localFile(fname).length());
					continue;
				}
				// remove from list to delete
				files_del.remove(fname);
			} else {
				// not present => download
				try {
					downloadAndSave(fname);
					changecount++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// delete the old ones
		for (String fname : files_del) {
			localFile(fname).delete();
			changecount++;
		}
		return changecount > 0;
	}

	private boolean loadCached() {
		try {
			status("Loading cached data...");
			FileIndex index = new FileIndex(c.openFileInput("index.xml"));
			System.out.println("Successfully loaded file index.");

			Route.Routes routes = new Route.Routes();
			Stop.Stops stops = new Stop.Stops();
			Trip.Trips trips = new Trip.Trips();
			StopTime.StopTimes stoptimes = new StopTime.StopTimes();

			for (String fname : index.filesRoutes()) {
				try {
					System.out.println("Loading routes file: " + fname);
					routes.add(Route.parseRoutes(c.openFileInput(fname)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (String fname : index.filesStops()) {
				try {
					System.out.println("Loading stops file: " + fname);
					stops.add(Stop.parseStops(c.openFileInput(fname)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (String fname : index.filesTrips()) {
				try {
					System.out.println("Loading trips file: " + fname);
					trips.add(Trip.parseTrips(c.openFileInput(fname)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (String fname : index.filesStopTimes()) {
				try {
					System.out.println("Loading stoptimes file: " + fname);
					stoptimes.add(StopTime.parseStopTimes(c.openFileInput(fname)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Route.useRoutes(routes);
			Stop.useStops(stops);
			Trip.useTrips(trips);
			StopTime.useStopTimes(stoptimes);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static class FileIndex {

		private List<String> files_all = new ArrayList<String>();
		private List<String> files_routes = new ArrayList<String>();
		private List<String> files_stops = new ArrayList<String>();
		private List<String> files_trips = new ArrayList<String>();
		private List<String> files_stoptimes = new ArrayList<String>();

		private Map<String, Integer> sizes = new HashMap<String, Integer>();

		public FileIndex(InputStream is) throws Exception {
			List<String> files = null;
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("ROUTES")) {
						files = files_routes;
					} else if (name.equalsIgnoreCase("STOPS")) {
						files = files_stops;
					} else if (name.equalsIgnoreCase("TRIPS")) {
						files = files_trips;
					} else if (name.equalsIgnoreCase("STOP_TIMES")) {
						files = files_stoptimes;
					} else if (name.equalsIgnoreCase("FILE")) {
						int size = Integer.parseInt(parser.getAttributeValue(null, "size"));
						String fname = parser.nextText();
						files.add(fname);
						files_all.add(fname);
						sizes.put(fname, size);
					}
				}
			}
			is.close();
		}

		public List<String> filesRoutes() {
			return Collections.unmodifiableList(files_routes);
		}

		public List<String> filesStops() {
			return Collections.unmodifiableList(files_stops);
		}

		public List<String> filesTrips() {
			return Collections.unmodifiableList(files_trips);
		}

		public List<String> filesStopTimes() {
			return Collections.unmodifiableList(files_stoptimes);
		}

		public List<String> filesAll() {
			return Collections.unmodifiableList(files_all);
		}

		public int fileSize(String fname) {
			return sizes.get(fname);
		}

	}

}
