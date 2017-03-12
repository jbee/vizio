package vizio.model;

@UseCode
public enum Motive {

	/**
	 * Something is broken, wrong. Change is required to make something work as
	 * described.
	 */
	defect,

	/**
	 * In contrast to a plan and planing the intention just expresses that
	 * something particular should happen. The event itself is important, not to
	 * predict the moment or plan the way.
	 */
	intention,
	
	/**
	 * A specific change of how things should be is proposed.
	 */
	proposal
}
