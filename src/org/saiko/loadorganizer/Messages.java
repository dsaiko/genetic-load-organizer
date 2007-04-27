/**
 * $LastChangedDate: 2006-05-01 00:23:31 +0200 (po, 01 V 2006) $
 * $LastChangedRevision: 13 $
 * $LastChangedBy: saigon $
 * $HeadURL: https://www.saiko.cz/svn/dsaiko/loadorganizer/src/org/saiko/loadorganizer/Messages.java $
 * $Id: Messages.java 13 2006-04-30 22:23:31Z saigon $
 * 
 * Load Organizer - ussage of genetic algorithm
 * (c) 2006 Dusan Saiko, dusan@saiko.cz
 */

package org.saiko.loadorganizer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * resource bundle functionality 
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.saiko.loadorganizer.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME,Locale.ENGLISH);

	private Messages() {
		//disable creating of the class
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
