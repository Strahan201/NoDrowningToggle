package com.sylvcraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import com.sylvcraft.NoDrowningToggle;

public class DrownToggle implements TabExecutor {
  NoDrowningToggle plugin;
  
  public DrownToggle(NoDrowningToggle plugin) {
    this.plugin = plugin;
  }
  
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (!sender.hasPermission("nodrowningtoggle.use")) return null;
    
    List<String> ret = new ArrayList<String>(Arrays.asList(""));
    switch (args.length) {
    case 1:
      if (sender.hasPermission("nodrowningtoggle.use.timed")) ret.add("## (seconds for temp disable)");
      if (sender.hasPermission("nodrowningtoggle.use.others")) {
        for (Player olp : plugin.getServer().getOnlinePlayers()) ret.add(olp.getName());
      }
      return getMatchedAsType(args[0], ret);

    case 2:
      if (!sender.hasPermission("nodrowningtoggle.use.others.timed")) return ret;
      
      Player target = plugin.getServer().getPlayer(args[0]);
      if (target == null) {
        ret.add("** Invalid player in prior arg!");
        return ret;
      }

      ret.add("## (seconds for temp disable)");
      return getMatchedAsType(args[1], ret);
    
    default:
      return ret;
    }
  }

  List<String> getMatchedAsType(String typed, List<String> values) {
    List<String> ret = new ArrayList<String>();
    for (String element : values) if (element.startsWith(typed)) ret.add(element);
    return ret;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      plugin.msg("player-only", sender);
      return true;
    }
    
    Map<String, String> data = new HashMap<String, String>();
    Player p = (Player)sender;
    Player target = null;

    if (!p.hasPermission("nodrowningtoggle.use")) {
      plugin.msg("access-denied", sender);
      return true;
    }

    switch (args.length) {
    case 0:
      boolean newStatus = toggleDrowning(p);
      data.put("%status%", newStatus?"enabled":"disabled");
      plugin.msg("toggled", sender, data);
      break;
      
    case 1:
      target = plugin.getServer().getPlayer(args[0]);
      
      if (args[0].matches("[0-9]+") && target == null) {
        if (!p.hasPermission("nodrowningtoggle.use.timed")) {
          plugin.msg("access-denied", sender);
          return true;
        }

        toggleDrowning(p, Integer.valueOf(args[0]));
        data.put("%status%", "disabled");
        data.put("%interval%", args[0]);
        plugin.msg("toggled-time", sender, data);
        return true;
      }
      
      if (!p.hasPermission("nodrowningtoggle.use.others")) {
        plugin.msg("access-denied", sender);
        return true;
      }

      if (target == null) {
        plugin.msg("invalid-player", sender);
        return true;
      }
      
      boolean statusForPlayer = toggleDrowning(target);
      data.put("%status%", statusForPlayer?"enabled":"disabled");
      data.put("%player%", target.getName());
      plugin.msg("toggled-others", sender, data);
      break;
      
    case 2:
      if (!p.hasPermission("nodrowningtoggle.use.others") &&
          !p.hasPermission("nodrowningtoggle.use.others.timed")) {
        plugin.msg("access-denied", sender);
        return true;
      }

      target = plugin.getServer().getPlayer(args[0]);
      if (target == null) {
        plugin.msg("invalid-player", sender);
        return true;
      }

      if (!args[1].matches("[0-9]+")) {
        plugin.msg("invalid-time", sender);
        return true;
      }

      toggleDrowning(target, Integer.valueOf(args[1]));
      data.put("%status%", "disabled");
      data.put("%player%", target.getName());
      data.put("%interval%", args[0]);
      plugin.msg("toggled-time-others", sender, data);
      break;
    }
    return true;
  }
  
  private boolean toggleDrowning(Player p) {
    boolean newStatus = !plugin.getConfig().getBoolean("drownstatus." + p.getUniqueId().toString() + ".candrown", true);
    plugin.getConfig().set("drownstatus." + p.getUniqueId().toString() + ".candrown", newStatus);
    plugin.saveConfig();
    return newStatus;
  }

  private void toggleDrowning(Player p, int interval) {
    plugin.getConfig().set("drownstatus." + p.getUniqueId().toString() + ".candrown", false);
    plugin.getConfig().set("drownstatus." + p.getUniqueId().toString() + ".expires", System.currentTimeMillis() + (interval * 1000));    
    plugin.saveConfig();
  }
}
