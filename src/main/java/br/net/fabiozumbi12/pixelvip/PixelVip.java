package br.net.fabiozumbi12.pixelvip;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;

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
	
	String version = "0.0.1";

	private SpongeExecutorService executor;
	public SpongeExecutorService getExecutor(){
		return this.executor;
	}
		
	private PVCommands cmds;
	public PVCommands getCmds(){
		return this.cmds;
	}
	
	@Listener
    public void onServerStart(GameStartedServerEvent event) {
		logger.info("Init utils module...");
		this.util = new PVUtil(this);
		
		logger.info("Init config module...");
		configManager = HoconConfigurationLoader.builder().setFile(defConfig).build();				
		this.config = new PVConfig(this, configDir, defConfig);
		
		logger.info("Init perms module...");
		this.perms = new PermsAPI(game);
		
		logger.info("Init commands module...");
		this.cmds = new PVCommands(this);
		CommandSpec spongevip = CommandSpec.builder()
			    .description(Text.of("Use to see the plugin info and reload."))
			    .permission("pixelvip.cmd.reload")
			    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("reload"))))
			    .executor((src, args) -> { {
			    	if (args.hasAny("reload")){
			    		this.config = new PVConfig(this, configDir, defConfig);
			    		this.cmds.reload();
			    		logger.info(util.toColor("&aPixelVip reloaded"));
			    	} else {
			    		src.sendMessage(util.toText("&a> PixelVip "+version+" by &6FabioZumbi12"));
			    	}
			    	return CommandResult.success();
			    }			    	
			    })
			    .build();
		Sponge.getCommandManager().register(this, spongevip, "pixelvip");
						
		logger.info("Init scheduler module...");
		this.executor = game.getScheduler().createSyncExecutor(this);		
		
		executor.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				getLogger().warn("Running vip scheduler... i am a debug message!");
				getConfig().getVipList().forEach((uuid,value)->{
					Optional<User> p = util.getUser(uuid);
					getLogger().info("UUID: " + uuid);
					
					getConfig().getVipList().get(uuid).forEach((vipInfo)->{
						long dur = new Long(vipInfo[0]);
						getLogger().info("Duration: " + dur);
						getLogger().info("Now: " + util.getNowMillis());
						if (dur >= util.getNowMillis() && p.isPresent()){
							getConfig().removeVip(p.get(), Optional.of(vipInfo[1]));
							if (p.get().isOnline()){
								p.get().getPlayer().get().sendMessage(util.toText(config.getLang("_pluginTag","vipEnded").replace("{vip}", vipInfo[1])));
							}
							getLogger().info(util.toColor(config.getLang("_pluginTag")+" &bThe vip &6" + vipInfo[1] + "&b of player &6" + p.get().getName() + " &bhas ended!"));
						}
					});
				});				
			}			
		}, 10, 60, TimeUnit.SECONDS);
		
		logger.info(util.toColor("We have &6"+config.getVipList().size()+" &ractive Vips"));
		logger.info(util.toColor("&aPixelVip enabled!&r"));
	}
	
	@Listener
	public void onStopServer(GameStoppingServerEvent e) {
		executor.shutdown();
		config.save();
		logger.info(util.toColor("&aPixelVip disabled!&r"));
	}
}