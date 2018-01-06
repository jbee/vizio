package se.jbee.track.ui.http;

import java.io.PrintWriter;

import se.jbee.track.ui.ctrl.Params;

@FunctionalInterface
public interface HttpAdapter {

	/**
	 * Responds the request by writing to output stream.
	 *
	 * @param params
	 * @param out
	 * @return HTTP status code
	 */
	int respond(Params params, PrintWriter out);

}