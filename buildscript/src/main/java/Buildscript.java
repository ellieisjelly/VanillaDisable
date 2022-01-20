import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.util.AtomicFile;
import io.github.coolcrabs.brachyura.util.Util;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {
    @Override
    public String getMcVersion() {
        // Minecraft Version
        return "1.18.1";
    }

    @Override
    public MappingTree createMappings() {
        // Yarn Mappings Version
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.18.1+build.22")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        // Fabric Loader Version
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.12.12"));
    }

    @Override
    public String getModId() {
        // Mod Name
        return "vanilla_disable";
    }

    @Override
    public String getVersion() {
        // Mod Version
        return "1.0.4";
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Fabric API
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-resource-loader-v0", "0.4.11+3ac43d9514"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-game-rule-api-v1", "1.0.10+3ac43d9514"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-lifecycle-events-v1", "1.4.10+c15ca33565"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-api-base", "0.4.1+b4f4f6cd14"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        // DataBreaker
        d.addMaven("https://maven.gegy.dev/", new MavenId("supercoder79:databreaker:0.2.8"), ModDependencyFlag.RUNTIME);
        // Tiefix (modrinth special lol)
        //d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth:tiefix:1.7.0"), ModDependencyFlag.RUNTIME);
        try {
            Path target = getLocalBrachyuraPath().resolve("tiefix-1.7.1.jar");
            if (!Files.exists(target)) {
                try (
                    AtomicFile f = new AtomicFile(target);
                    InputStream is = new URL("https://api.modrinth.com/maven/maven/modrinth/tiefix/1.7.1/tiefix-1.7.1.jar").openStream();
                ) {
                    Files.copy(is, f.tempPath, StandardCopyOption.REPLACE_EXISTING);
                    f.commit();
                    System.out.println("INFO: Remapped tiefix-1.7.1-sources.jar");
                }
            }
            d.add(new JavaJarDependency(target, null, null), ModDependencyFlag.RUNTIME);
        } catch (Exception e) {
            Util.sneak(e);
        }
    }

    @Override
    public int getJavaVersion() {
        return 17;
    }
}
