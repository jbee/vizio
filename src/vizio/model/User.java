package vizio.model;

import static java.lang.Math.max;
import static vizio.model.Date.date;

import java.util.EnumMap;

public final class User extends Entity<User> {

	/**
	 * The idea of a fine grained setting is to reduce mails or mail handling by
	 * allowing users to configure away messages by using
	 * {@link Mail.Delivery#never} and at the same time give the user a way to
	 * adopt mails to their usage pattern.
	 * 
	 * In general one get mails for all tasks involved or watched. So watching
	 * is mostly a way to trigger notifications for task one isn't involved in.
	 */
	@UseCode
	public static enum Notifications {
		// product
		constituted(Mail.Delivery.daily),
		// area
		opened(Mail.Delivery.daily),    // for user that are origin maintainers
		left(Mail.Delivery.daily),      // by a maintainer (to other maintainers)
		// version
		tagged(Mail.Delivery.daily),    // for user that are origin maintainers
		// poll
		polled(Mail.Delivery.hourly),   // in an area the user is maintainer (can vote)
		vote(Mail.Delivery.hourly),     // for a poll where user can vote (is maintainer)
		// task
		reported(Mail.Delivery.hourly), // new tasks (in maintained area)
		forked(Mail.Delivery.daily),    // derived (from an task the user is involved)
		moved(Mail.Delivery.daily),     // where user is involved
		solved(Mail.Delivery.hourly),   // where user is involved
		attached(Mail.Delivery.hourly)  // where user is involved
		;
		
		public final Mail.Delivery def;
		
		Notifications(Mail.Delivery def) {
			this.def = def;
		}
	}
	
	private static final int MINIMUM_WATCH_LIMIT = 20;

	public Name name;
	// account
	public Email email;
	public int authenticated; // count
	public byte[] token; // mem only
	public byte[] encryptedToken; // persisted
	public long millisTokenExprired;
	
	// user data
	public Names sites;
	public int watches; // n tasks
	public EnumMap<Notifications,Mail.Delivery> notifications;
	
	// change log
	public long millisLastActive;
	
	// activity statistics
	public int xp;
	public int absolved;
	public int resolved;
	public int dissolved;
	public Names contributesToProducts;
	
	// voting tasks
	public long millisEmphasised;
	public int emphasisedToday;

	public User(int version) {
		super(version);
	}
	
	/**
	 * User entity is not cloned to but changed in place an explicitly modified
	 * by touching.
	 */
	public void touch(long now) {
		millisLastActive = now;
		modified();
	}
	
	@Override
	public ID computeID() {
		return ID.userId(name);
	}
	
	@Override
	public Name product() {
		return Name.ORIGIN;
	}
	
	public boolean isAnonymous() {
		return name.isEmail();
	}

	public int emphDelay() {
		return max(60000, (int)( 3600000f / (1f+(xp/50f))));
	}

	public int emphPerDay() {
		return 10 + (xp/5);
	}

	public boolean canEmphasise(long now) {
		return isAuthenticated()
			&&	now - millisEmphasised > emphDelay()
			&& (emphasisedToday < emphPerDay() || date(now).after(date(millisEmphasised)));
	}

	public boolean isAuthenticated() {
		return authenticated > 0 && name.isEditable();
	}

	public void emphasised(long now) {
		if (date(now).after(date(millisEmphasised))) {
			emphasisedToday = 1;
		} else {
			emphasisedToday++;
		}
		millisEmphasised = now;
	}

	public boolean hasSite(Name name) {
		return sites.contains(name);
	}

	public boolean canWatch() {
		return watches < MINIMUM_WATCH_LIMIT + (xp / 10);
	}

}
