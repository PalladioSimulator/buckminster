/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/

package org.eclipse.buckminster.jnlp.p2.bootstrap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * @author Filip Hrbek
 * 
 *         JNLP descriptor parser. No validation is performed since XSD is not available for JNLP files.
 */
public class JNLPResource
{
	private Document m_document;

	private Date m_lastModified;

	private String m_content;

	public JNLPResource(URL jnlp) throws JNLPException
	{
		parseJNLP(jnlp);
	}

	public String getContent()
	{
		return m_content;
	}

	public Document getDocument()
	{
		return m_document;
	}

	public Date getLastModified()
	{
		return m_lastModified;
	}

	public void parseJNLP(URL jnlp) throws JNLPException
	{
		try
		{
			URLConnection conn = jnlp.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayOutputStream jnlpBuffer = new ByteArrayOutputStream();
			Utils.streamCopy(conn.getInputStream(), jnlpBuffer);
			jnlpBuffer.close();
			m_document = builder.parse(new ByteArrayInputStream(jnlpBuffer.toByteArray()));
			m_lastModified = new Date(conn.getLastModified());
			m_content = jnlpBuffer.toString();
		}
		catch(Exception e)
		{
			throw new JNLPException(
					Messages.getString("can_not_read_materialization_wizard_resource"), Messages.getString("try_again_and_if_the_problem_persists_please_report_the_problem"), //$NON-NLS-1$ //$NON-NLS-2$
					BootstrapConstants.ERROR_CODE_JNLP_SAX_EXCEPTION, e);
		}
	}
}
