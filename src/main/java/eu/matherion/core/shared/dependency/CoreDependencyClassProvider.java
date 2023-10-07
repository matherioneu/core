package eu.matherion.core.shared.dependency;

import eu.matherion.core.CoreApplication;

public interface CoreDependencyClassProvider {

   Class<? extends CoreDependency<?>> getDependencyClass();

   boolean canLoad(CoreApplication coreApplication);

}
