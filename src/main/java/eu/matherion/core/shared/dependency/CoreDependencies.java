package eu.matherion.core.shared.dependency;

import com.google.common.collect.Maps;
import eu.matherion.core.CoreApplication;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class CoreDependencies {

    @SafeVarargs
    public static Map<Class<? extends CoreDependency<?>>, CoreDependency<?>> resolveDependencies(Class<? extends CoreDependencyClassProvider>... classProviders) {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        Map<Class<? extends CoreDependency<?>>, CoreDependency<?>> dependencies = Maps.newHashMap();
        for (Class<? extends CoreDependencyClassProvider> classProviderClass : classProviders) {
            CoreDependencyClassProvider classProvider;
            try {
                classProvider = classProviderClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                coreApplication.getLogger().severe("Dependency: Error while creating provider instance of " + classProviderClass.getSimpleName() + "!");
                e.printStackTrace();
                continue;
            }
            Class<? extends CoreDependency<?>> dependencyClass = classProvider.getDependencyClass();
            boolean canLoad = classProvider.canLoad(coreApplication);
            if (!canLoad) {
                coreApplication.getLogger().warning("Dependency " + dependencyClass.getSimpleName() + " could not be loaded.");
                continue;
            }
            try {
                CoreDependency<?> dependency = dependencyClass.getConstructor(CoreApplication.class).newInstance(coreApplication);
                dependency.init();
                dependencies.put(dependencyClass, dependency);
            } catch (Exception e) {
                coreApplication.getLogger().warning("Dependency " + dependencyClass.getSimpleName() + " could not be loaded.");
                e.printStackTrace();
            }
        }
        return dependencies;
    }

}
