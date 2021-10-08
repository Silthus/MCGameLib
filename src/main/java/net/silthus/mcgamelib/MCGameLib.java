package net.silthus.mcgamelib;

import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.mcgamelib.event.GameEventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@PluginMain
public class MCGameLib extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static MCGameLib instance;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private GameEventHandler gameEventHandler;

    public MCGameLib() {
        instance = this;
    }

    public MCGameLib(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    @Override
    public void onEnable() {

        gameEventHandler = new GameEventHandler(this);
    }

    public void registerEvents(Game game, Listener listener) {

        getGameEventHandler().registerEvents(game, listener);
    }
}
