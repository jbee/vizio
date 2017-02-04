package vizio.engine;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Iterator;

import vizio.engine.Change.Operation;
import vizio.model.ID;

/**
 * An event is looking at changes of an transaction from some distance.
 * Not all details of the transaction are included but the basics of what happened:
 * 
 * - When did the change happen?
 * - Who made the change?
 * - What kind of change was that?
 */
public final class Event implements Comparable<Event>, Iterable<Event.Transition> {

	/**
	 * A little "trick" needs to be done to ensure no two entries happen in same moment (timestamp).
	 * 
	 * That is to make sure time at least increases by 1 ms each time a new entry is written.
	 * 
	 * The neat by-effect of this trick is that keys are chronological order and time-slices can
	 * easily be extracted using cursors.
	 */
	public final long timestamp; // also the key
	public final ID originator; 
	private final Transition[] transitions;
	
	public Event(long timestamp, ID originator, Transition... transitions) {
		super();
		this.timestamp = timestamp;
		this.originator = originator;
		this.transitions = transitions;
	}

	/**
	 * Changes applied to the entity in order of occurrence.  
	 */
	public static final class Transition {
		public final ID entity;
		public final Change.Operation[] ops;
		
		public Transition(ID entity, Operation... ops) {
			super();
			this.entity = entity;
			this.ops = ops;
		}
		
		@Override
		public String toString() {
			return entity+Arrays.toString(ops);
		}
	}
	
	public ZonedDateTime time() {
		return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC);
	}

	public ID uniqueID() {
		return ID.eventId(timestamp);
	}
	
	public int cardinality() {
		return transitions.length;
	}

	@Override
	public String toString() {
		return String.valueOf(timestamp);
	}

	@Override
	public int compareTo(Event other) {
		return Long.compare(timestamp, other.timestamp);
	}
	
	@Override
	public Iterator<Transition> iterator() {
		return Arrays.asList(transitions).iterator();
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(timestamp);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Event && equalTo((Event) obj);
	}

	public boolean equalTo(Event other) {
		return timestamp == other.timestamp;
	}

}
