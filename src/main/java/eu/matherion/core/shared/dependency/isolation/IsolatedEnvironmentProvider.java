package eu.matherion.core.shared.dependency.isolation;

import eu.matherion.core.shared.dependency.CoreDependencies;
import eu.matherion.core.shared.dependency.CoreDependencyClassProvider;

public abstract class IsolatedEnvironmentProvider {

    private final Class<? extends CoreDependencyClassProvider> dependencyClassProvider;

    public IsolatedEnvironmentProvider(Class<? extends CoreDependencyClassProvider> dependencyClassProvider) {
        this.dependencyClassProvider = dependencyClassProvider;
    }

    public abstract Class<? extends IsolatedEnvironment<?, ?>> getEnvironmentClass();

    public boolean isRunnable() {
        return CoreDependencies.canLoad(dependencyClassProvider);
    }
}