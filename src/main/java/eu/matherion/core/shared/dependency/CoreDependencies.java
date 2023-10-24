package eu.matherion.core.shared.dependency;

import com.google.common.collect.Maps;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.isolation.IsolatedEnvironment;
import eu.matherion.core.shared.dependency.isolation.IsolatedEnvironmentProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class CoreDependencies {

    @SafeVarargs
    public static Map<Class<? extends CoreDependencyClassProvider>, CoreDependency<?>> resolveDependencies(Class<? extends CoreDependencyClassProvider>... classProviders) {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        Map<Class<? extends CoreDependencyClassProvider>, CoreDependency<?>> dependencies = Maps.newHashMap();
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
                dependencies.put(classProviderClass, dependency);
            } catch (Exception e) {
                coreApplication.getLogger().warning("Dependency " + dependencyClass.getSimpleName() + " could not be loaded.");
                e.printStackTrace();
            }
        }
        return dependencies;
    }

    public static boolean canLoad(Class<? extends CoreDependencyClassProvider> classProviderClass) {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        CoreDependencyClassProvider classProvider;
        try {
            classProvider = classProviderClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            coreApplication.getLogger().severe("Dependency: Error while creating provider instance of " + classProviderClass.getSimpleName() + "!");
            e.printStackTrace();
            return false;
        }
        return classProvider.canLoad(coreApplication);
    }

    @Nullable
    public static <A, R> R solveIsolation(Class<? extends IsolatedEnvironmentProvider> clazz, A apply) {
        CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
        IsolatedEnvironmentProvider isolatedEnvironmentProvider;
        try {
            isolatedEnvironmentProvider = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            coreApplication.getLogger().severe("Dependency Isolation: Error while creating provider instance of " + clazz.getSimpleName() + "!");
            e.printStackTrace();
            return null;
        }
        Class<? extends IsolatedEnvironment<A, R>> environmentClass = (Class<? extends IsolatedEnvironment<A, R>>) isolatedEnvironmentProvider.getEnvironmentClass();
        if (!isolatedEnvironmentProvider.isRunnable()) {
            coreApplication.getLogger().warning("Dependency Isolation: Environment " + environmentClass.getSimpleName() + " is not runnable.");
            return null;
        }
        try {
            IsolatedEnvironment<A, R> isolatedEnvironment = environmentClass.getDeclaredConstructor().newInstance();
            return isolatedEnvironment.run(apply);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
