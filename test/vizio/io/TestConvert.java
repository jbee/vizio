package vizio.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static vizio.engine.Convert.area2bin;
import static vizio.engine.Convert.bin2area;
import static vizio.engine.Convert.bin2event;
import static vizio.engine.Convert.bin2poll;
import static vizio.engine.Convert.bin2product;
import static vizio.engine.Convert.bin2site;
import static vizio.engine.Convert.bin2task;
import static vizio.engine.Convert.bin2user;
import static vizio.engine.Convert.bin2version;
import static vizio.engine.Convert.event2bin;
import static vizio.engine.Convert.poll2bin;
import static vizio.engine.Convert.product2bin;
import static vizio.engine.Convert.site2bin;
import static vizio.engine.Convert.task2bin;
import static vizio.engine.Convert.user2bin;
import static vizio.engine.Convert.version2bin;
import static vizio.model.Email.email;
import static vizio.model.Gist.gist;
import static vizio.model.Name.as;
import static vizio.model.Template.template;

import java.nio.ByteBuffer;
import java.util.function.Predicate;

import org.junit.Test;

import vizio.engine.Change;
import vizio.engine.Convert;
import vizio.engine.Event;
import vizio.engine.Event.Transition;
import vizio.engine.History;
import vizio.engine.NoLimits;
import vizio.engine.Repository;
import vizio.engine.Tracker;
import vizio.model.Area;
import vizio.model.ID;
import vizio.model.IDN;
import vizio.model.Name;
import vizio.model.Poll;
import vizio.model.Poll.Matter;
import vizio.model.Product;
import vizio.model.Site;
import vizio.model.Task;
import vizio.model.Template;
import vizio.model.User;
import vizio.model.Version;

public class TestConvert {

	private long now = System.currentTimeMillis();
	private Tracker tracker = new Tracker(TestConvert.this::tick, new NoLimits());

	private long tick() {
		now += 60000;
		return now;
	}

	@Test
	public void userConversion() {
		User user1 = newTestUser();
		assertConsistentConversion(bin2user, user2bin, user1);
	}
	
	@Test
	public void siteConversion() {
		User user1 = newTestUser();
		Site site1 = tracker.launch(as("my-tasks"), template("foobar"), user1);
		assertConsistentConversion(bin2site, site2bin, site1);
	}

	@Test
	public void productConversion() {
		User user1 = newTestUser();
		Product prod1 = tracker.constitute(as("p1"), user1);
		assertConsistentConversion(bin2product, product2bin, prod1);
	}

	@Test
	public void areaConversion() {
		User user1 = newTestUser();
		Product prod1 = tracker.constitute(as("p1"), user1);
		Area area1 = tracker.compart(prod1, as("area1"), user1);
		assertConsistentConversion(bin2area, area2bin, area1);
	}

	@Test
	public void versionConversion() {
		User user1 = newTestUser();
		Product prod1 = tracker.constitute(as("p1"), user1);
		Version v1 = tracker.tag(prod1, as("v1"), user1);
		assertConsistentConversion(bin2version, version2bin, v1);
	}

	@Test
	public void pollConversion() {
		User user1 = newTestUser();
		Product prod1 = tracker.constitute(as("p1"), user1);
		User user2 = tracker.register(null, as("user2"), email("user2@example.com"));
		Poll poll1 = tracker.poll(Matter.inclusion, prod1.origin, user1, user2);
		assertConsistentConversion(bin2poll, poll2bin, poll1);
	}

	@Test
	public void taskConversion() {
		User user1 = newTestUser();
		Product prod1 = tracker.constitute(as("p1"), user1);
		Task task1 = tracker.reportDefect(prod1, gist("broken"), user1, prod1.somewhere, prod1.somewhen, true);
		assertConsistentConversion(bin2task, task2bin, task1);
	}
	
	@Test
	public void logEntryConversion() {
		long timestamp = System.currentTimeMillis();
		Name user = as("testuser");
		Transition c1 = new Transition(ID.userId(user), Change.Operation.abandon, Change.Operation.attach);
		Transition[] entityChanges = new Transition[] { c1, c1 };
		assertConsistentConversion(bin2event, event2bin, new Event(timestamp, ID.userId(user), entityChanges));
	}
	
	private User newTestUser() {
		User u1 = tracker.register(null, as("user1"), email("user1@example.com"));
		u1 = tracker.authenticate(u1, u1.token);
		return u1;
	}

	static <T> void assertConsistentConversion(Convert<Repository,T> reader, Convert<T, ByteBuffer> writer, T value) {
		ByteBuffer buf = ByteBuffer.allocate(2048);
		writer.convert(value, buf);
		byte[] written = new byte[buf.position()];
		buf.flip();
		buf.get(written);
		assertTrue(written.length > 0);
		T read = reader.convert(new TestRepository(), ByteBuffer.wrap(written));
		buf = ByteBuffer.allocate(2048);
		writer.convert(read, buf);
		byte[] rewritten = new byte[buf.position()];
		buf.flip();
		buf.get(rewritten);
		assertArrayEquals(written, rewritten);
	}

	static class TestRepository implements Repository {

		@Override
		public User user(Name user) {
			User res = new User(1);
			res.name = user;
			return res;
		}
		
		@Override
		public Site site(Name user, Name site) {
			return new Site(1, user, site, Template.BLANK_PAGE);
		}

		@Override
		public Product product(Name product) {
			Product res = new Product(1);
			res.name = product;
			return res;
		}

		@Override
		public Area area(Name product, Name area) {
			Area res = new Area(1);
			res.product = product;
			res.name = area;
			return res;
		}

		@Override
		public Version version(Name product, Name version) {
			Version res = new Version(1);
			res.product = product;
			res.name = version;
			return res;
		}

		@Override
		public Task task(Name product, IDN id) {
			Task res = new Task(1);
			res.product = product(product);
			res.id = id;
			return res;
		}

		@Override
		public Poll poll(Name product, Name area, IDN serial) {
			Poll res = new Poll(1);
			res.serial = serial;
			res.area = area(product, area);
			return res;
		}
		
		@Override
		public Event event(long timestamp) throws UnknownEntity {
			throw new UnknownEntity(ID.eventId(timestamp));
		}
		
		@Override
		public History history(ID entity) throws UnknownEntity {
			throw new UnknownEntity(ID.historyId(entity));
		}
		
		@Override
		public void tasks(Name product, Predicate<Task> consumer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// nothing to do
		}
		
	}
}
