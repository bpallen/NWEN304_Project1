package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.os.Handler;
import android.util.Xml;

public class Updater extends Thread {

	private static final String URL_BASE = "http://homepages.ecs.vuw.ac.nz/~allenbenj/nwen304/p1/";

	private Context c;
	private Handler h;

	public Updater(Context c_, Handler h_) {
		c = c_;
		h = h_;
		setDaemon(true);
	}

	@Override
	public void run() {



	}

	private Map<String, List<String>> parseIndex(InputStream is) {
		Map<String, List<String>> index = new HashMap<String, List<String>>();
		index.put("ROUTES", new ArrayList<String>());
		index.put("STOPS", new ArrayList<String>());
		index.put("TRIPS", new ArrayList<String>());
		index.put("STOP_TIMES", new ArrayList<String>());
		String key = null;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			Stop s = null;
			for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("ROUTES")) {
						key = "ROUTES";
					} else if (name.equalsIgnoreCase("STOPS")) {
						key = "STOPS";
					} else if (name.equalsIgnoreCase("TRIPS")) {
						key = "TRIPS";
					} else if (name.equalsIgnoreCase("STOP_TIMES")) {
						key = "STOP_TIMES";
					} else if (name.equalsIgnoreCase("FILE")) {
						index.get(key).add(parser.nextText());
					}
				}
			}
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return index;
	}

	private boolean loadCached() {
		try {
			Map<String, List<String>> index = parseIndex(c.openFileInput("index.xml"));
			for (Map.Entry<String, List<String>> me : index.entrySet()) {
				// parse individual files

			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

}
