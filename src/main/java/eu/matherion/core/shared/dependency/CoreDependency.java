package eu.matherion.core.shared.dependency;

import eu.matherion.core.CoreApplication;
import org.jetbrains.annotations.Nullable;

public abstract class CoreDependency<T> {

    private final String name;
    private final CoreApplication coreApplication;
    private T object;

    public CoreDependency(String name, CoreApplication coreApplication) {
        this.name = name;
        this.coreApplication = coreApplication;
        T constructed = construct(coreApplication);
        if (constructed == null) {
            coreApplication.getLogger().warning("Dependency " + name + " could not be constructed.");
            return;
        }
        object = constructed;
    }

    @Nullable
    public abstract T construct(CoreApplication coreApplication);
    public abstract void init();

    public T reconstruct() {
        object = construct(coreApplication);
        return object;
    }

    public T get() {
        return object;
    }

    public String getName() {
        return name;
    }
}
