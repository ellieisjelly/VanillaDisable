import java.nio.file.Path;
import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.decompiler.fernflower.FernflowerDecompiler;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import io.github.coolcrabs.brachyura.processing.ProcessorChain;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {
    @Override
    public VersionMeta createMcVersion() {
        // Minecraft Version
        return Minecraft.getVersion("1.18.1");
    }

    @Override
    public MappingTree createMappings() {
        // Uses Mojang Official Mappings
        return createMojmap();
    }

    @Override
    public FabricLoader getLoader() {
        // Fabric Loader Version
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.13.2"));
    }

    @Override
    public String getModId() {
        // Mod Name
        return "vanilla_disable";
    }

    @Override
    public String getVersion() {
        // Mod Version
        return "1.3.1";
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Fabric API
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-resource-loader-v0", "0.4.11+3ac43d9514"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-game-rule-api-v1", "1.0.10+3ac43d9514"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-lifecycle-events-v1", "1.4.10+c15ca33565"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-api-base", "0.4.1+b4f4f6cd14"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        // CaffeineConfig
        d.addMaven("https://jitpack.io", new MavenId("com.github.FlashyReese:CaffeineConfig:383ee33be5"), ModDependencyFlag.COMPILE, ModDependencyFlag.JIJ, ModDependencyFlag.RUNTIME);
        // DataBreaker
        d.addMaven("https://maven.gegy.dev/", new MavenId("supercoder79:databreaker:0.2.8"), ModDependencyFlag.RUNTIME);
        // Tiefix
        //d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth:tiefix:1.9.0"), ModDependencyFlag.RUNTIME);
    }

    @Override
    public int getJavaVersion() {
        // Default is Java 8
        return 17;
    }

    @Override
    public BrachyuraDecompiler decompiler() {
        // Uses QuiltFlower instead of CFR
        return new FernflowerDecompiler(Maven.getMavenJarDep("https://maven.quiltmc.org/repository/release", new MavenId("org.quiltmc:quiltflower:1.7.0"))); 
    };

    @Override
    public Path getBuildJarPath() {
        // Changes the jar file name
        return getBuildLibsDir().resolve(getModId() + "-" + "mc" + createMcVersion().version + "-" + getVersion() + ".jar");
    }

    @Override
    public ProcessorChain resourcesProcessingChain() {
        // Overrides the version tag in fabric.mod.json
        return new ProcessorChain(super.resourcesProcessingChain(), new FmjVersionFixer(this));
    }
}
