package br.net.fabiozumbi12.pixelvip.cmds;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import br.net.fabiozumbi12.pixelvip.PixelVip;

public class PVCommands {

	private PixelVip plugin;
	
    public PVCommands(PixelVip plugin){
		this.plugin = plugin;
				
		Sponge.getCommandManager().register(plugin, newKey(), "newkey", "genkey", "gerarkey");
		Sponge.getCommandManager().register(plugin, listKeys(), "listkeys", "listarkeys");
		Sponge.getCommandManager().register(plugin, useKey(), "usekey", "usarkey");
		Sponge.getCommandManager().register(plugin, vipTime(), "viptime", "tempovip");
		Sponge.getCommandManager().register(plugin, removeVip(), "removevip", "delvip");
		Sponge.getCommandManager().register(plugin, setActive(), "changevip", "setctive", "trocarvip");
	}    
    
    /**Command to generate new key.
	 * 
	 * @return CommandSpec
	 */
	private CommandSpec newKey() {
		return CommandSpec.builder()
			    .description(Text.of("Generate new vip key for groups."))
			    .permission("spongevip.cmd.newkey")
			    .arguments(GenericArguments.string(Text.of("group")),GenericArguments.longNum(Text.of("days")))
			    .executor((src, args) -> { {
			    	String group = args.<String>getOne("group").get();
			    	long days = args.<Long>getOne("days").get();
			    	
			    	if (days <= 0){
			    		throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","moreThanZero")));
			    	}
			    				    	
			    	if (!plugin.getConfig().groupExists(group)){
			    		throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","noGroups")+group));
			    	}
			    	String key = plugin.getUtil().genKey(plugin.getConfig().getInt(10,"configs","key-size"));
			    	plugin.getConfig().addKey(key, group, plugin.getUtil().dayToMillis(days));	
			    	src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","keyGenerated")));
			    	src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("timeKey")+key));
			    	src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("timeGroup")+group));
			    	src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("totalTime")+days));
			    	return CommandResult.success();			    	
			    }			    	
			    })
			    .build();	    
	}
	
	
	/**Command to list all available keys, and key's info.
	 * 
	 * @return CommandSpec
	 */
	public CommandSpec listKeys() {
		return CommandSpec.builder()
			    .description(Text.of("List all available keys."))
			    .permission("spongevip.cmd.listkeys")
			    .executor((src, args) -> { {
			    	
			    	Collection<Object> keys = plugin.getConfig().getListKeys();
			    	if (keys.size() > 0){
			    		src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","listKeys")));
			    		for (Object key:keys){			    			
			    			String[] keyinfo = plugin.getConfig().getKeyInfo(key.toString());
			    			long days = plugin.getUtil().millisToDay(keyinfo[1]);
			    			src.sendMessage(plugin.getUtil().toText("&b- Key: &6"+key.toString()+"&b | Group: &6"+keyinfo[0]+"&b | Days: &6"+days));
				    	}
			    	} else {
			    		src.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","noKeys")));
			    	}
			    	return CommandResult.success();			    	
			    }			    	
			    })
			    .build();	    
	}
	
	/**Command to activate a vip using a key.
	 * 
	 * @return CommandSpec
	 */
	public CommandSpec useKey() {
		return CommandSpec.builder()
			    .description(Text.of("Use a key to activate the Vip."))
			    .permission("spongevip.cmd.player")
			    .arguments(GenericArguments.string(Text.of("key")))
			    .executor((src, args) -> { {
			    	if (src instanceof Player){
			    		Player p = (Player) src;
			    		String key = args.<String>getOne(Text.of("key")).get();
				    	return plugin.getConfig().activateVip(p, key);
			    	}
			    	throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","onlyPlayers")));	    	
			    }			    	
			    })
			    .build();	    
	}
	
	/**Command to check the vip time.
	 * 
	 * @return CommandSpec
	 */
	public CommandSpec vipTime() {
		return CommandSpec.builder()
			    .description(Text.of("Use to check the vip time."))
			    .permission("spongevip.cmd.player")
			    .arguments(GenericArguments.optional(GenericArguments.user(Text.of("player"))))
			    .executor((src, args) -> { {
			    	if (src instanceof Player){
			    		return plugin.getUtil().sendVipTime(src, ((Player)src).getUniqueId().toString(), ((Player)src).getName());
			    	} else 
			    		if (args.hasAny("player")){
			    			Optional<User> optp = args.<User>getOne("player");
			    			if (optp.isPresent()){
			    				User p = optp.get();
				    			return plugin.getUtil().sendVipTime(src, p.getUniqueId().toString(), p.getName());			    			
				    		} else {
				    			throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","noPlayersByName")));	
				    		}
			    	}
			    	throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","onlyPlayers")));	    	
			    }			    	
			    })
			    .build();	    
	}
	
	/**Command to remove a vip of player.
	 * 
	 * @return CommandSpec
	 */
	public CommandSpec removeVip() {
		return CommandSpec.builder()
			    .description(Text.of("Use to remove a vip of player."))
			    .permission("spongevip.cmd.removevip")
			    .arguments(GenericArguments.user(Text.of("player")),GenericArguments.optional(GenericArguments.string(Text.of("vip"))))
			    .executor((src, args) -> { {			    	
			    	Optional<User> optp = args.<User>getOne("player");
			    	Optional<String> optg = args.<String>getOne("vip");
			    	if (optp.isPresent()){
			    		User p = optp.get();
			    		plugin.getConfig().removeVip(p, optg);
			    		return CommandResult.success();
			    	}
			    	throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","noPlayersByName")));
			    }			    	
			    })
			    .build();	    
	}
	
	/**Command to sets the active vip, if more than one key activated.
	 * 
	 * @return CommandSpec
	 */
	public CommandSpec setActive() {
		return CommandSpec.builder()
			    .description(Text.of("Use to change your active VIP, if more keys activated."))
			    .permission("spongevip.cmd.player")
			    .arguments(GenericArguments.string(Text.of("vip")))
			    .executor((src, args) -> { {			   
			    	if (src instanceof Player){
			    		Player p = (Player) src;
			    		String group = args.<String>getOne("vip").get();
			    		List<String[]> vipInfo = plugin.getConfig().getVipInfo(p.getUniqueId().toString());
				    	if (vipInfo.size() > 0){
				    		for (String[] vip:vipInfo){
				    			if (vip[1].equalsIgnoreCase(group)){
				    				plugin.getConfig().setActive(p.getUniqueId().toString(), group, vip[2]);
				    				p.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","activeVipSetTo")+group));
				    				return CommandResult.success();
				    			}
				    		}
				    	}
				    	throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","noGroups")+group));
			    	}
			    	throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","onlyPlayers")));
			    }			    	
			    })
			    .build();	    
	}
}
