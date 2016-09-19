package br.net.fabiozumbi12.pixelvip;

import java.io.File;
import java.nio.file.Path;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.Game;

import br.net.fabiozumbi12.pixelvip.cmds.PVCommands;

import com.google.inject.Inject;

@Plugin(id = "pixelvip", 
name = "PixelVip", 
version = "1.0.0",
authors="FabioZumbi12", 
description="Plugin to give VIP to your players.")
public class PixelVip {
	
	@Inject private Logger logger;
	public Logger getLogger(){	
		return logger;
	}
	
	@Inject
	@ConfigDir(sharedRoot = true)
	private Path configDir;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private File defConfig;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	public ConfigurationLoader<CommentedConfigurationNode> getCfManager(){
		return configManager;
	}
	
	private PVConfig config;
	public PVConfig getConfig(){
		return config;
	}
	
	PermsAPI perms;
	public PermsAPI getPerms(){
		return this.perms;
	}
	
	@Inject private Game game;
	public Game getGame(){
		return this.game;
	}
	
	private PVUtil util;
	public PVUtil getUtil(){
		return this.util;
	}
	
	String version = "1.0.0";
	
	@Listener
    public void onServerStart(GameStartedServerEvent event) {
		logger.info("Init utils...");
		this.util = new PVUtil(this);
		
		logger.info("Init config...");
		configManager = HoconConfigurationLoader.builder().setFile(defConfig).build();				
		this.config = new PVConfig(this, configDir, defConfig);
		
		logger.info("Init perms...");
		this.perms = new PermsAPI(game);
		
		logger.info("Init commands...");
		new PVCommands(this);
		CommandSpec spongevip = CommandSpec.builder()
			    .description(Text.of("Use to see the plugin info and reload."))
			    .permission("spongevip.cmd.reload")
			    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("reload"))))
			    .executor((src, args) -> { {
			    	if (args.hasAny("reload")){
			    		this.config = new PVConfig(this, configDir, defConfig);
			    		logger.info("SpongeVip reloaded");
			    	} else {
			    		src.sendMessage(util.toText("&a> SpongeVip "+version+" by &6FabioZumbi12"));
			    	}
			    	return CommandResult.success();
			    }			    	
			    })
			    .build();
		Sponge.getCommandManager().register(this, spongevip, "spongevip");
		
		
		logger.info(util.toColor("&aSpongeVip enabled!&r"));
	}
	
	@Listener
	public void onStopServer(GameStoppingServerEvent e) {
		config.save();
		logger.info(util.toColor("&cSpongeVip disabled!&r"));
	}
}