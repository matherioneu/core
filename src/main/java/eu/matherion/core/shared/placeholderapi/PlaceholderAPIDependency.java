package eu.matherion.core.shared.placeholderapi;

import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;

public class PlaceholderAPIDependency extends CoreDependency<PlaceholdersExtension> {

    private final CoreApplication coreApplication;

    public PlaceholderAPIDependency(CoreApplication coreApplication) {
        super("placeholderapi", coreApplication);
        this.coreApplication = coreApplication;
    }

    @Override
    public PlaceholdersExtension construct(CoreApplication coreApplication) {
        return new PlaceholdersExtension(coreApplication);
    }

    @Override
    public void init() {
        PlaceholdersExtension placeholdersExtension = get();
        placeholdersExtension.register();
        coreApplication.getLogger().info("PlaceholderAPI extension has been registered.");
    }
}
