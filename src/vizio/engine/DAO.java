package vizio.engine;

import static vizio.engine.Convert.bin2area;
import static vizio.engine.Convert.bin2event;
import static vizio.engine.Convert.bin2poll;
import static vizio.engine.Convert.bin2product;
import static vizio.engine.Convert.bin2site;
import static vizio.engine.Convert.bin2task;
import static vizio.engine.Convert.bin2user;
import static vizio.engine.Convert.bin2version;
import static vizio.model.ID.areaId;
import static vizio.model.ID.pollId;
import static vizio.model.ID.productId;
import static vizio.model.ID.siteId;
import static vizio.model.ID.userId;
import static vizio.model.ID.versionId;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import vizio.db.DB;
import vizio.db.DB.TxR;
import vizio.model.Area;
import vizio.model.Entity;
import vizio.model.ID;
import vizio.model.ID.Type;
import vizio.model.IDN;
import vizio.model.Name;
import vizio.model.Poll;
import vizio.model.Product;
import vizio.model.Site;
import vizio.model.Task;
import vizio.model.User;
import vizio.model.Version;

/**
 * Reading {@link Entity}s from a {@link DB} via a {@link Repository} abstraction.  
 */
public class DAO implements Repository {

	protected final HashMap<ID, Entity<?>> loaded = new HashMap<>();
	
	private final DB.TxR txr;
	
	public DAO(TxR txr) {
		super();
		this.txr = txr;
	}

	@Override
	public final void close() {
		txr.close();
	}
	
	@SuppressWarnings("unchecked")
	private final <T extends Entity<T>> T load(ID id, Convert<Repository, T> reader) {
		Object res = possiblyChanged(id);
		if (res != null)
			return (T) res;		
		T e = loadObject(id, reader);
		loaded.put(id, e);
		return e;
	}

	private <T> T loadObject(ID id, Convert<Repository, T> reader) {
		return reader.convert(this, read(id));
	}
	
	protected Object possiblyChanged(ID id) {
		return loaded.get(id);
	}
	
	@Override
	public Site site(Name user, Name site) {
		return load(siteId(user, site), bin2site);
	}

	@Override
	public Poll poll(Name product, Name area, IDN serial) {
		return load(pollId(product, area, serial), bin2poll);
	}

	@Override
	public Product product(Name product) {
		return load(productId(product), bin2product);
	}

	@Override
	public Area area(Name product, Name area) {
		return load(areaId(product, area), bin2area);
	}

	@Override
	public Version version(Name product, Name version) {
		return load(versionId(product, version), bin2version);
	}

	@Override
	public Task task(Name product, IDN id) {
		return load(ID.taskId(product, id), bin2task);
	}
	
	@Override
	public User user(Name user) {
		return load(userId(user), bin2user);
	}
	
	@Override
	public Event event(long timestamp) throws UnknownEntity {
		return loadObject(ID.eventId(timestamp), bin2event);
	}
	
	@Override
	public History history(ID entity) throws UnknownEntity {
		ID id = ID.historyId(entity);
		return Convert.bin2history.convert(id, read(id));
	}

	private ByteBuffer read(ID id) throws UnknownEntity {
		ByteBuffer buf = txr.get(id);
		if (buf == null)
			throw new Repository.UnknownEntity(id);
		return buf;
	}
	
	@Override
	public void tasks(Name product, Predicate<Task> consumer) {
		txr.range(ID.taskId(product, IDN.ZERO), (k,v) -> { 
			if (!k.startsWith(product))
				return false; // stop as soon as another product is reached
			return consumer.test(Convert.bin2task.convert(this, v)); 
		});
	}
}
