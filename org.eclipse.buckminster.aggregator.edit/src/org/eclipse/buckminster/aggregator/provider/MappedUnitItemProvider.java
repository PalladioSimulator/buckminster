/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.buckminster.aggregator.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.buckminster.aggregator.AggregatorPackage;
import org.eclipse.buckminster.aggregator.CustomCategory;
import org.eclipse.buckminster.aggregator.Feature;
import org.eclipse.buckminster.aggregator.MappedRepository;
import org.eclipse.buckminster.aggregator.MappedUnit;
import org.eclipse.buckminster.aggregator.p2.InstallableUnit;
import org.eclipse.buckminster.aggregator.p2.MetadataRepository;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;

/**
 * This is the item provider adapter for a {@link org.eclipse.buckminster.aggregator.MappedUnit} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class MappedUnitItemProvider extends AggregatorItemProviderAdapter implements IEditingDomainItemProvider,
		IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource

{
	/**
	 * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public MappedUnitItemProvider(AdapterFactory adapterFactory)
	{
		super(adapterFactory);
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
	{
		if(childrenFeatures == null)
		{
			super.getChildrenFeatures(object);
			childrenFeatures.add(AggregatorPackage.Literals.MAPPED_UNIT__INSTALLABLE_UNIT);
		}
		return childrenFeatures;
	}

	/**
	 * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
	{
		if(itemPropertyDescriptors == null)
		{
			super.getPropertyDescriptors(object);

			addEnabledPropertyDescriptor(object);
			addInstallableUnitPropertyDescriptor(object);
			addValidConfigurationsPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator()
	{
		return AggregatorEditPlugin.INSTANCE;
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object)
	{
		MappedUnit mappedUnit = (MappedUnit)object;
		return getString("_UI_MappedUnit_type") + " " + mappedUnit.isEnabled();
	}

	/**
	 * @generated NOT
	 */
	@Override
	public void notifyChanged(Notification notification)
	{
		notifyChangedGen(notification);
		
		boolean updateContent = false;
		boolean updateLabel = true;

		switch(notification.getFeatureID(MappedUnit.class))
		{
		case AggregatorPackage.MAPPED_UNIT__ENABLED:
			updateContent = true;
			updateLabel = false;
			// no break here, it is intentional
		case AggregatorPackage.MAPPED_UNIT__INSTALLABLE_UNIT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), updateContent, updateLabel));

			Set<EObject> affectedNodes = new HashSet<EObject>();

			// Go through all direct ancestors first
			EObject container = ((EObject)notification.getNotifier()).eContainer();
			while(container != null)
			{
				affectedNodes.add(container);
				container = container.eContainer();
			}

			// And now, find all categories which may contain the feature just being enabled/disabled
			if(notification.getNotifier() instanceof Feature)
				for(CustomCategory category : ((Feature)notification.getNotifier()).getCategories())
					affectedNodes.add(category);

			for(EObject affectedNode : affectedNodes)
				fireNotifyChanged(new ViewerNotification(notification, affectedNode, false, true));

			return;
		}
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating
	 * a viewer notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public void notifyChangedGen(Notification notification)
	{
		updateChildren(notification);

		switch(notification.getFeatureID(MappedUnit.class))
		{
		case AggregatorPackage.MAPPED_UNIT__ENABLED:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case AggregatorPackage.MAPPED_UNIT__INSTALLABLE_UNIT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds a property descriptor for the Enabled feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addEnabledPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_MappedUnit_enabled_feature"), getString("_UI_PropertyDescriptor_description",
						"_UI_MappedUnit_enabled_feature", "_UI_MappedUnit_type"),
				AggregatorPackage.Literals.MAPPED_UNIT__ENABLED, true, false, false,
				ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Installable Unit feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	protected void addInstallableUnitPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add(new ItemPropertyDescriptor(
				((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_MappedUnit_installableUnit_feature"), getString("_UI_PropertyDescriptor_description",
						"_UI_MappedUnit_installableUnit_feature", "_UI_MappedUnit_type"),
				AggregatorPackage.Literals.MAPPED_UNIT__INSTALLABLE_UNIT, true, false, true, null, null, null)
		{
			@SuppressWarnings("unchecked")
			public Collection<?> getChoiceOfValues(Object object)
			{
				MappedUnit self = (MappedUnit)object;
				MappedRepository container = (MappedRepository)self.eContainer();
				MetadataRepository repo = container.getMetadataRepository();
				if(repo == null)
					return Collections.singleton(null);

				// Build a list of IU's that correspond to the given type of MappedUnit
				//
				Collector collector = repo.query(getInstallableUnitQuery(), new Collector(), null);
				if(collector.isEmpty())
					return Collections.singleton(null);

				List<InstallableUnit> result = new ArrayList<InstallableUnit>();
				result.add(null);

				Collection availableUnits = collector.toCollection();

				// if current installable unit is not among the newly retrieved ones,
				// add it to the choice values so that user would not be surprised by
				// disappearing current choice after clicking on the property value
				if(!availableUnits.contains(self.getInstallableUnit()))
					result.add(self.getInstallableUnit());

				result.addAll(availableUnits);

				// Exclude IU's that are already mapped
				//
				for(MappedUnit mu : getContainerChildren(container))
				{
					if(mu == self)
						continue;

					InstallableUnit iu = mu.getInstallableUnit();
					if(iu == null)
						continue;

					int idx = result.indexOf(iu);
					if(idx >= 0)
						result.remove(idx);
				}
				return result;
			}
		});
	}

	/**
	 * This adds a property descriptor for the Valid Configurations feature. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	protected void addValidConfigurationsPropertyDescriptor(Object object)
	{
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_MappedUnit_validConfigurations_feature"), getString(
						"_UI_PropertyDescriptor_description", "_UI_MappedUnit_validConfigurations_feature",
						"_UI_MappedUnit_type"), AggregatorPackage.Literals.MAPPED_UNIT__VALID_CONFIGURATIONS, true,
				false, true, null, null, null));
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created
	 * under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
	{
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child)
	{
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	protected List<? extends MappedUnit> getContainerChildren(MappedRepository container)
	{
		throw new UnsupportedOperationException();
	}

	// Must be implemented by subclass.
	protected Query getInstallableUnitQuery()
	{
		throw new UnsupportedOperationException();
	}

}
