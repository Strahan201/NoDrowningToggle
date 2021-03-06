package com.sylvcraft;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.sylvcraft.commands.DrownToggle;
import com.sylvcraft.events.EntityDamage;

public class NoDrowningToggle extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();
    getServer().getPluginManager().registerEvents(new EntityDamage(this), this);
    getCommand("drowntoggle").setExecutor(new DrownToggle(this));
  }
  
  public void msg(String msgCode, CommandSender sender) {
    if (getConfig().getString("messages." + msgCode) == null) return;
    msgTransmit(getConfig().getString("messages." + msgCode), sender);
  }

  public void msg(String msgCode, CommandSender sender, Map<String, String> data) {
    if (getConfig().getString("messages." + msgCode) == null) return;
    String tmp = getConfig().getString("messages." + msgCode, msgCode);
    for (Map.Entry<String, String> mapData : data.entrySet()) {
      tmp = tmp.replace(mapData.getKey(), mapData.getValue());
    }
    msgTransmit(tmp, sender);
  }
  
  public void msgTransmit(String msg, CommandSender sender) {
    for (String m : (msg + " ").split("%br%")) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
    }
  }

}
