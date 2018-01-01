package se.jbee.track.ui.http;

import java.io.PrintWriter;
import java.util.Map;

@FunctionalInterface
public interface HttpAdapter {

	String KEY_SESSION_USER = "SESSION_USER";

	/**
	 * Responds the request by writing to output stream.
	 *
	 * @param path <samp>/</samp>, <samp>/foo/</samp>, ...
	 * @param params
	 * @param out
	 * @return HTTP status code
	 */
	int respond(String path, Map<String, String> params, PrintWriter out);

}