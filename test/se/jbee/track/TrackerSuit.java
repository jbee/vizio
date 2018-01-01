package se.jbee.track;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.jbee.track.cache.TestCriteria;
import se.jbee.track.cache.TestCriterium;
import se.jbee.track.cache.TestTaskSet;
import se.jbee.track.engine.TestConvert;
import se.jbee.track.engine.TestLMDB;
import se.jbee.track.engine.TestOTP;
import se.jbee.track.model.TestBytes;
import se.jbee.track.model.TestGist;
import se.jbee.track.model.TestURL;

@RunWith(Suite.class)
@SuiteClasses({ TestTracker.class, TestConvert.class, TestLMDB.class,
		TestCriteria.class, TestOTP.class, TestUseCode.class,
		TestCriterium.class, TestGist.class, TestTaskSet.class,
		TestBytes.class, TestURL.class })
public class TrackerSuit {
	// run all tests...
}