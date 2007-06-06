/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.ui.editor.cspec;

import java.util.List;

import org.eclipse.buckminster.core.cspec.builder.CSpecBuilder;
import org.eclipse.buckminster.core.cspec.builder.GeneratorBuilder;
import org.eclipse.buckminster.core.helpers.TextUtils;
import org.eclipse.buckminster.ui.general.editor.IValidator;
import org.eclipse.buckminster.ui.general.editor.ValidatorException;
import org.eclipse.buckminster.ui.general.editor.simple.SimpleTable;

/**
 * @author Karel Brezina
 *
 */
public class GeneratorsTable extends SimpleTable<GeneratorBuilder>
{
	private CSpecBuilder m_cspecBuilder;
	
	public GeneratorsTable(List<GeneratorBuilder> data, CSpecBuilder cspecBuilder)
	{
		super(data);
		m_cspecBuilder = cspecBuilder;
	}

	public String[] getColumnHeaders()
	{
		return new String[]{"Name", "Attribute", "Component"};
	}

	public int[] getColumnWeights()
	{
		return new int[]{40, 20, 20};
	}

	public Object[] toRowArray(GeneratorBuilder t)
	{
		Object[] array = new Object[getColumns()];
		
		array[0] = t.getName();
		array[1] = t.getAttribute();
		array[2] = t.getComponent();
		
		return array;
	}

	public GeneratorBuilder toRowClass(Object[] args) throws ValidatorException
	{
		GeneratorBuilder builder = m_cspecBuilder.createGeneratorBuilder();
		
		builder.setName(TextUtils.notEmptyString((String) args[0]));
		builder.setAttribute(TextUtils.notEmptyString((String) args[1]));
		builder.setComponent(TextUtils.notEmptyString((String) args[2]));
		
		return builder;
	}

	@Override
	public IValidator getFieldValidator(int idx)
	{
		switch(idx)
		{
		case 0:
			return SimpleTable.createNotEmptyStringValidator("Generator name can not be empty");
		case 1:
			return SimpleTable.createNotEmptyStringValidator("Attribute can not be empty");
		case 2:
			return SimpleTable.createNotEmptyStringValidator("Component name can not be empty");
		default:
			return SimpleTable.getEmptyValidator();
		}
	}
}
