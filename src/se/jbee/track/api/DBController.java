package se.jbee.track.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.jbee.track.cache.Cache;
import se.jbee.track.cache.Matches;
import se.jbee.track.db.DB;
import se.jbee.track.engine.Server;
import se.jbee.track.model.Name;
import se.jbee.track.model.Page;
import se.jbee.track.model.Template;
import se.jbee.track.model.User;

public class DBController {

	private final Server server; 
	private final DB db;
	private final Cache cache;
	private final Map<Name, User> sessions = new ConcurrentHashMap<>();

	public DBController(Server server, DB db, Cache cache) {
		this.server = server;
		this.db = db;
		this.cache = cache;
	}

	public ListView evaluate(Params params) {
		// TODO Auto-generated method stub
		return new ListView(new User(1), System.currentTimeMillis(), new Page[0], new Page(1, Name.as("prod"), Name.as("area"), Name.as("xyz"), Template.template("Hello")), new Matches[0]);
	}

	
}
