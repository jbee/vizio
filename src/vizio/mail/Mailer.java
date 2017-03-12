package vizio.mail;

import vizio.model.Mail;

/**
 * Abstraction for a "mailing system" (not to confuse with a mail server).
 * 
 * The task of the system is to aggregate mails to the moment when it is fitting to deliver them. 
 */
public interface Mailer {

	/**
	 * Enqueues a new email for delivery as specified. 
	 */
	void deliver(Mail mail);
}