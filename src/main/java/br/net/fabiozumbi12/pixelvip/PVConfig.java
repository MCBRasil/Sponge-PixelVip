package br.net.fabiozumbi12.pixelvip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import com.google.common.reflect.TypeToken;

public class PVConfig {	
		
	private CommentedConfigurationNode config;
	private PixelVip plugin;
	
	public PVConfig(PixelVip plugin, Path defDir, File defConfig){
		this.plugin = plugin;
		try {
			Files.createDirectories(defDir);
			if (!defConfig.exists()){
				plugin.getLogger().info("Creating config file...");
				defConfig.createNewFile();
			}
			
	        config = plugin.getCfManager().load();	
	        
	        config.getNode("groups").setComment("Put your PEX(or your permission plugin) vip group names here. Case sensitive!");
	        if (!config.getNode("groups").hasMapChildren()){
	        	config.getNode("groups","vip1","commands").setComment(
	        			"Add the commands to run when the player use the key for activation \n"
		        		+ "You can use the variables:\n"
		        		+ "{p} = Player name, {vip} = Vip group, {days} = Vip days, {playergroup} = Player group before activate vip");
	        	config.getNode("groups","vip1","commands").setValue(Arrays.asList("broadcast &aThe player &6{p} &ahas acquired your &6{vip} &afor &6{days} &adays","give {p} minecraft:diamond 10", "eco give {p} 10000"));
	        }
	        
	        config.getNode("activeVips").setComment("Your active vips will be listed here!");
	        if (!config.getNode("activeVips").hasMapChildren()){
	        	config.getNode("activeVips").setValue(new ArrayList<String>());
	        }
	        
	        config.getNode("configs","key-size").setComment("Sets the length of your vip keys.");
	        config.getNode("configs","key-size").setValue(getInt(10,"configs","key-size"));
	        
	        config.getNode("configs","cmdOnRemoveVip").setComment("Command to run when a vip is removed by command.");
	        config.getNode("configs","cmdOnRemoveVip").setValue(getString("pex user {p} parent delete group {vip}","configs","cmdOnRemoveVip"));
	        	
	        config.getNode("configs","commandsToRunOnVipFinish").setComment(
	        		"Run this commands when the vip of a player finish.\n"
	        		+ "Variables: {p} get the player name, {vip} get the actual vip, {playergroup} get the group before the player activate your vip.");
	        config.getNode("configs","commandsToRunOnVipFinish").setValue(getListString("configs","commandsToRunOnVipFinish"));
	        
	        config.getNode("configs","commandsToRunOnChangeVip").setComment(
	        		"Run this commands on player change your vip to other.\n"
	        		+ "Variables: {p} get the player name, {newvip} get the new vip, {oldvip} get the vip group before change.");
	        config.getNode("configs","commandsToRunOnChangeVip").setValue(getListString("configs","commandsToRunOnChangeVip"));
	        
	        config.getNode("keys").setComment("All available keys will be here.");
	        if (!config.getNode("keys").hasMapChildren()){
	        	config.getNode("keys").setValue(new ArrayList<String>());
	        }
	        	        
	        if (getListString("configs","commandsToRunOnVipFinish").size() == 0){	        	
	        	config.getNode("configs","commandsToRunOnVipFinish")
	        	.setValue(Arrays.asList("pex user {p} parent delete group {vip}","pex user {p} parent add group {playergroup}","pex reload"));
	        }   
	        
	        if (getListString("configs","commandsToRunOnChangeVip").size() == 0){	        	
	        	config.getNode("configs","commandsToRunOnChangeVip")
	        	.setValue(Arrays.asList("pex user {p} parent add group {newvip}","pex user {p} parent delete group {oldvip}","pex reload"));
	        }
	        
	        //strings
	        config.getNode("strings","_pluginTag").setValue(getString("&7[&6PixelVip&7] ","strings","_pluginTag"));	
	        config.getNode("strings","noPlayersByName").setValue(getString("&cTheres no players with this name!","strings","noPlayersByName"));	
	        config.getNode("strings","onlyPlayers").setValue(getString("&cOnly players ca use this command!","strings","onlyPlayers"));	
	        config.getNode("strings","noKeys").setValue(getString("&aTheres no available keys! Use &6/newkey &ato generate one.","strings","noKeys"));
	        config.getNode("strings","listKeys").setValue(getString("&aList of Keys:","strings","listKeys"));
	        config.getNode("strings","vipInfoFor").setValue(getString("&aVip info for ","strings","vipInfoFor"));
	        config.getNode("strings","playerNotVip").setValue(getString("&cThis player(or you) is not VIP!","strings","playerNotVip"));
	        config.getNode("strings","moreThanZero").setValue(getString("&cThe days need to be more than 0","strings","moreThanZero"));
	        config.getNode("strings","noGroups").setValue(getString("&cTheres no groups with name ","strings","noGroups"));
	        config.getNode("strings","keyGenerated").setValue(getString("&aGenerated a key with the following:","strings","keyGenerated"));
	        config.getNode("strings","invalidKey").setValue(getString("&cThis key is invalid or not exists!","strings","invalidKey"));
	        config.getNode("strings","vipActivated").setValue(getString("&aVip activated with success:","strings","vipActivated"));
	        config.getNode("strings","activeVip").setValue(getString("&b- Vip: &6{vip}","strings","activeVip"));
	        config.getNode("strings","activeDays").setValue(getString("&b- Days: &6{days} &bdays","strings","activeDays"));	
	        config.getNode("strings","timeLeft").setValue(getString("&b- Time left: &6","strings","timeLeft"));	        
	        config.getNode("strings","totalTime").setValue(getString("&b- Days: &6","strings","totalTime"));
	        config.getNode("strings","timeKey").setValue(getString("&b- Key: &6","strings","timeKey"));	        	        
	        config.getNode("strings","timeGroup").setValue(getString("&b- Vip: &6","strings","timeGroup"));
	        config.getNode("strings","timeActive").setValue(getString("&b- In Use: &6","strings","timeActive"));
	        config.getNode("strings","activeVipSetTo").setValue(getString("&aYour active VIP is ","strings","activeVipSetTo"));
	        config.getNode("strings","noGroups").setValue(getString("&cNo groups with name &6","strings","noGroups"));
	        config.getNode("strings","days").setValue(getString(" &bdays","strings","days"));
	        config.getNode("strings","hours").setValue(getString(" &bhours","strings","hours"));
	        config.getNode("strings","minutes").setValue(getString(" &bminutes","strings","minutes"));
	        config.getNode("strings","and").setValue(getString(" &band","strings","and"));
	        config.getNode("strings","vipEnded").setValue(getString(" &bYour vip &6{vip} &bhas ended. &eWe hope you enjoyed your Vip time &a:D","strings","vipEnded"));
	        config.getNode("strings","lessThan").setValue(getString("&6Less than one minute to end your vip...","strings","lessThan"));
			
	        save();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	public void save(){
		try {
			plugin.getCfManager().save(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addKey(String key, String group, long millis){		
		try {
			config.getNode("keys",key, "group").setValue(TypeToken.of(String.class), group);
			config.getNode("keys",key, "duration").setComment("Duration in days: "+plugin.getUtil().millisToDay(millis));
			config.getNode("keys",key, "duration").setValue(TypeToken.of(String.class), String.valueOf(millis));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		save();
	}
	
	public boolean delKey(String key){		
		boolean removed = config.getNode("keys").removeChild(key);
		save();
		return removed;
	}
	
	public String[] getKeyInfo(String key){	
		if (config.getNode("keys",key).hasMapChildren()){
			return new String[]{getString("","keys",key,"group"),getString("","keys",key,"duration")};
		}
		return new String[0];
	}
		
	public CommandResult activateVip(Player p, String key) throws CommandException {
		if (getKeyInfo(key).length == 2){
			String[] keyinfo = getKeyInfo(key);
			delKey(key);
			long expires = new Long(keyinfo[1])+plugin.getUtil().getNowMillis();
			String group = keyinfo[0];
			String pGroup = plugin.getPerms().getGroup(p);
			String pdGroup = plugin.getPerms().getGroup(p);
			List<String[]> vips = plugin.getConfig().getVipInfo(p.getUniqueId().toString());
			if (!vips.isEmpty()){
				pGroup = vips.get(0)[2];
			}
			
			getListString("groups",group,"commands").forEach((cmd)->{
				plugin.getExecutor().execute(new Runnable(){
					@Override
					public void run() {
						plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), 
								cmd.replace("{p}", p.getName())
								.replace("{vip}", group)
								.replace("{playergroup}", pdGroup)
								.replace("{days}", String.valueOf(plugin.getUtil().millisToDay(keyinfo[1]))));
					}				
				});
			});								
			try {
				config.getNode("activeVips",group,p.getUniqueId().toString(),"playerGroup").setValue(TypeToken.of(String.class), pGroup);
				config.getNode("activeVips",group,p.getUniqueId().toString(),"duration").setValue(TypeToken.of(Long.class), expires);
				setActive(p.getUniqueId().toString(),group,pdGroup);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}						
			p.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","vipActivated")));
			p.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("activeVip").replace("{vip}", group)));
			p.sendMessage(plugin.getUtil().toText(plugin.getConfig().getLang("activeDays").replace("{days}", String.valueOf(plugin.getUtil().millisToDay(keyinfo[1])))));			
			return CommandResult.success();
		}
		throw new CommandException(plugin.getUtil().toText(plugin.getConfig().getLang("_pluginTag","invalidKey")));	
	}
	
	public void setActive(String uuid, String group, String pgroup){
		String newVip = group;
		String oldVip = pgroup;
		
		for (Object key:getGroupList()){
			if (config.getNode("activeVips",key,uuid).hasMapChildren()){
				try {
					if (key.toString().equals(group)){						
						if (!getBoolean(true, "activeVips",key.toString(),uuid,"active")){
							newVip = key.toString();
							long total = getLong(0,"activeVips",key.toString(),uuid,"duration")+plugin.getUtil().getNowMillis();
							config.getNode("activeVips",key,uuid,"duration").setValue(TypeToken.of(Long.class), total);
						}
						config.getNode("activeVips",key,uuid,"active").setValue(TypeToken.of(Boolean.class), true);						
					} else {	
						if (getBoolean(false, "activeVips",key.toString(),uuid,"active")){
							oldVip = key.toString();
							long total = getLong(0,"activeVips",key.toString(),uuid,"duration")-plugin.getUtil().getNowMillis();
							config.getNode("activeVips",key,uuid,"duration").setValue(TypeToken.of(Long.class), total);
						}
						config.getNode("activeVips",key,uuid,"active").setValue(TypeToken.of(Boolean.class), false);
					}					
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
			}
		}	
		
		String newVipf = newVip;
		String oldVipf = oldVip;
		for (String cmd:plugin.getConfig().getListString("configs","commandsToRunOnChangeVip")){
			String cmdf = cmd.replace("{p}", plugin.getUtil().getUser(UUID.fromString(uuid)).get().getName());
			if (!oldVip.equals("") && cmdf.contains("{oldvip}")){
				plugin.getExecutor().schedule(new Runnable(){
					@Override
					public void run() {
						plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), cmdf.replace("{oldvip}", oldVipf));
					}					
				}, 1, TimeUnit.SECONDS);				
			} else
			if (!newVip.equals("") && cmd.contains("{newvip}")){
				plugin.getExecutor().schedule(new Runnable(){
					@Override
					public void run() {
						plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), cmdf.replace("{newvip}", newVipf));
					}					
				}, 1, TimeUnit.SECONDS);
			} else {
				plugin.getExecutor().schedule(new Runnable(){
					@Override
					public void run() {
						plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), cmdf.replace("{newvip}", newVipf));
					}					
				}, 1, TimeUnit.SECONDS);
			}
		}
		save();
	}
	
	private void removeVip(User p, String group){
		config.getNode("activeVips",group).removeChild(p.getUniqueId().toString());
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), getString("","configs","cmdOnRemoveVip").replace("{p}", p.getName()).replace("{vip}", group));
	}
	
	public void removeVip(User p, Optional<String> optg){
		String uuid = p.getUniqueId().toString();
		List<String[]> vipInfo = plugin.getConfig().getVipInfo(uuid);
		boolean id = false;
		String oldGroup = "";
		if (vipInfo.size() > 0){
			for (String[] key:vipInfo){
				String group = key[1];
				oldGroup = key[2];
				if (vipInfo.size() > 1 ){
					if (optg.isPresent()){
						if (optg.get().equals(group)){
							plugin.getConfig().removeVip(p, group);			    							
						} else if (!id){
							plugin.getConfig().setActive(uuid, group, "");
							id = true;
						}			    						
	    			} else {	    				
    					plugin.getConfig().removeVip(p, group);
	    			}
				} else {
					plugin.getConfig().removeVip(p, group);
				}		
			}			    			
		}
		if (plugin.getConfig().getVipInfo(uuid).size() == 0){			
			for (String cmd:getListString("configs","commandsToRunOnVipFinish")){
				if (cmd.contains("{vip}")){continue;}		
				final String oldGroupf = oldGroup;
				plugin.getExecutor().schedule(new Runnable(){
					@Override
					public void run() {
						String cmdf = cmd.replace("{p}", p.getName()).replace("{playergroup}", oldGroupf);
						plugin.getGame().getCommandManager().process(Sponge.getServer().getConsole(), cmdf);
					}					
				}, 1, TimeUnit.SECONDS);				
			}
		}
		save();
	}
	
	public long getLong(int def, String...node){
		return config.getNode((Object[])node).getLong(def);
	}
	
	public int getInt(int def, String...node){
		return config.getNode((Object[])node).getInt(def);
	}
	
	public String getString(String def, String...node){
		return config.getNode((Object[])node).getString(def);
	}
	
	public boolean getBoolean(boolean def, String...node){
		return config.getNode((Object[])node).getBoolean(def);
	}
	
	public List<String> getListString(String...node){
		List<String> keyList = new ArrayList<String>();
		try {
			keyList.addAll(config.getNode((Object[])node).getList(TypeToken.of(String.class)));
			return keyList;
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public String getLang(String...nodes){
		StringBuilder msg = new StringBuilder();
		for (String node:nodes){
			try {
				msg.append(config.getNode("strings",node).getValue(TypeToken.of(String.class)));
			} catch (ObjectMappingException e) {
				msg.append("No strings found for node &6"+node);
			}
		}
		return msg.toString();
	}

	public boolean groupExists(String group) {
		return config.getNode("groups",group).hasMapChildren();
	}

	public Set<Object> getListKeys() {
		return config.getNode("keys").getChildrenMap().keySet();
	}
	
	public Set<Object> getGroupList(){		
		return config.getNode("groups").getChildrenMap().keySet();
	}
	
	public HashMap<String,List<String[]>> getVipList(){
		HashMap<String,List<String[]>> vips = new HashMap<String,List<String[]>>();		
		for (Object groupobj:getGroupList()){
			for (Object uuidobj:config.getNode("activeVips",groupobj).getChildrenMap().keySet()){
				String uuid = uuidobj.toString();
				plugin.getLogger().info("UUID "+uuid); 
				
				List<String[]> vipInfo = getVipInfo(uuid);
				List<String[]> activeVips = new ArrayList<String[]>();
				for (String[] active:vipInfo){
					if (active[3].equals("true")){
						activeVips.add(active);
						plugin.getLogger().info("Active Group "+active[1]); 
					}
				}
				if (activeVips.size() > 0){
					vips.put(uuid, activeVips);
				}
			}
		}
		return vips;
	}
	
	/**Return player's vip info.<p>
	 * [0] = Duration, [1] = Vip Group, [2] = Player Group, [3] = Is Active
	 * @param puuid Player UUID as string.
	 * @return {@code List<String[4]>}
	 */
	public List<String[]> getVipInfo(String puuid){
		List<String[]> vips = new ArrayList<String[]>();
		for (Object key:getGroupList()){			
			if (config.getNode("activeVips",key.toString(),puuid).hasMapChildren()){
				vips.add(new String[]{getString("","activeVips",key.toString(),puuid,"duration"), key.toString(), getString("","activeVips",key.toString(),puuid,"playerGroup"), getString("","activeVips",key.toString(),puuid,"active")});
			}
		}		
		return vips;
	}
	
	public HashMap<String, String> getCmdChoices(){
		HashMap<String, String> choices = new HashMap<String, String>();
		getGroupList().forEach((k)->{
			choices.put(k.toString(), k.toString());
		});
		return choices;
	}
}
