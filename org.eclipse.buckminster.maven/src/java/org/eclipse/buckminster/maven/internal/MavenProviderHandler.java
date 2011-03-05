/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.maven.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.buckminster.core.rmap.model.BidirectionalTransformer;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.rmap.parser.ProviderHandler;
import org.eclipse.buckminster.sax.AbstractHandler;
import org.eclipse.buckminster.sax.ChildHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MavenProviderHandler extends ProviderHandler {
	private final MappingsHandler mappingsHandler = new MappingsHandler(this);
	private final ScopesHandler scopesHandler = new ScopesHandler(this);

	private Map<String, MapEntry> mappings;

	private List<BidirectionalTransformer> rules;

	private Map<String, Scope> scopes;

	private boolean transitive;

	public MavenProviderHandler(AbstractHandler parent) {
		super(parent);
	}

	@Override
	public void childPopped(ChildHandler child) throws SAXException {
		if (child instanceof MappingsHandler) {
			mappings = ((MappingsHandler) child).getEntriesAndClear();
			rules = ((MappingsHandler) child).getRuleAndClear();
		} else if (child instanceof ScopesHandler) {
			scopes = ((ScopesHandler) child).getScopesAndClear();
		} else
			super.childPopped(child);
	}

	@Override
	public ChildHandler createHandler(String uri, String localName, Attributes attrs) throws SAXException {
		ChildHandler ch;
		if (MavenProvider.ELEM_MAPPINGS.equals(localName))
			ch = mappingsHandler;
		else if (MavenProvider.ELEM_SCOPES.equals(localName))
			ch = scopesHandler;
		else
			ch = super.createHandler(uri, localName, attrs);
		return ch;
	}

	@Override
	public Provider getProvider() {
		return new MavenProvider(getSearchPath(), getReaderType(), getComponentTypes(), getVersionConverter(), getUriFormat(), getResolutionFilter(),
				getProperties(), getDocumentation(), mappings, rules, scopes, transitive);
	}

	@Override
	public void handleAttributes(Attributes attrs) throws SAXException {
		super.handleAttributes(attrs);
		mappings = null;
		rules = null;
		scopes = null;
		transitive = getOptionalBooleanValue(attrs, MavenProvider.ATTR_TRANSITIVE, true);
	}
}
