package com.sylvcraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.sylvcraft.NoDrowningToggle;

public class EntityDamage implements Listener {
  NoDrowningToggle plugin;
  
  public EntityDamage(NoDrowningToggle plugin) {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (e.getCause() != DamageCause.DROWNING) return;
    if (!(e.getEntity() instanceof Player)) return;
    
    Player p = (Player)e.getEntity();
    if (plugin.getConfig().getBoolean("drownstatus." + p.getUniqueId().toString() + ".candrown", true) &&
        !p.hasPermission(new Permission("nodrowningtoggle.neverdrown", PermissionDefault.FALSE))) return;
    
    Long expiration = plugin.getConfig().getLong("drownstatus." + p.getUniqueId().toString() + ".expires");
    if (System.currentTimeMillis() > expiration && expiration > 0) {
      plugin.getConfig().set("drownstatus." + p.getUniqueId().toString() + ".expires", null);
      plugin.getConfig().set("drownstatus." + p.getUniqueId().toString() + ".candrown", null);
      plugin.saveConfig();
      return;
    }
    
    e.setCancelled(true);
  }
}
