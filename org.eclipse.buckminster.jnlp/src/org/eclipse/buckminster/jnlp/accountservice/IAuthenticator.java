/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.jnlp.accountservice;

import org.apache.commons.httpclient.HttpClient;

/**
 * Enables authentication of an user
 * 
 * @author kaja
 */
public interface IAuthenticator
{
	public static final int LOGIN_FAILED = -1;
	public static final int LOGIN_OK 	= 1;
	
	public static final int REGISTER_OK = 1;
	public static final int REGISTER_LOGIN_EXISTS = -1;
	public static final int REGISTER_LOGIN_TOO_SHORT = -2;
	public static final int REGISTER_PASSWORD_TOO_SHORT = -3;
	public static final int REGISTER_EMAIL_FORMAT_ERROR = -4;
	public static final int REGISTER_FAIL = -99;

	public void initialize(String serviceURL) throws Exception;
	
	/**
	 * Gets HttpClient which was used for authentication. It should be used for getting password protected content after authentication.
	 * 
	 * @return
	 */
	public HttpClient getHttpClient();
	
	public int login(String userName, String password) throws Exception;

	public int logout() throws Exception;
	
	public boolean isLoggedIn() throws Exception;
	
	public int register(String userName, String password, String email) throws Exception;
	
	/**
	 * Gets provider name
	 * 
	 * @return provider name
	 */
	public String getProvider();
	
	/**
	 * Gets provider URL
	 * 
	 * @return provider URL
	 */
	public String getProviderURL();
}
