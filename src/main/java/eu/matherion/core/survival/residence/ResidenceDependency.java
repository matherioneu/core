package eu.matherion.core.survival.residence;

import com.bekvon.bukkit.residence.Residence;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.dependency.CoreDependency;

public class ResidenceDependency extends CoreDependency<Residence> {

    private final CoreApplication coreApplication;

    public ResidenceDependency(CoreApplication coreApplication) {
        super("residence", coreApplication);
        this.coreApplication = coreApplication;
    }

    @Override
    public Residence construct(CoreApplication coreApplication) {
        return Residence.getInstance();
    }

    @Override
    public void init() {
        coreApplication.getLogger().info("Registering Residence command blocker listener.");
        coreApplication.getServer().getPluginManager().registerEvents(new ResidenceCommandBlockerListener(coreApplication, get()), coreApplication);
    }

}
