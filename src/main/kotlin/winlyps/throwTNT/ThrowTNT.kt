package winlyps.throwTNT

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class ThrowTNT : JavaPlugin(), Listener {

    override fun onEnable() {
        // Register event listener
        server.pluginManager.registerEvents(this, this)
        
        // Log plugin startup
        logger.info("ThrowTNT plugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("ThrowTNT plugin has been disabled!")
    }
    
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        
        // Check if the player is holding TNT
        if (item.type == Material.TNT) {
            // Right-click in air = throw TNT
            if (event.action == Action.RIGHT_CLICK_AIR) {
                event.isCancelled = true
                
                // Reduce TNT count by 1
                if (player.gameMode != org.bukkit.GameMode.CREATIVE) {
                    // Get current amount
                    val currentAmount = player.inventory.itemInMainHand.amount
                    
                    if (currentAmount > 1) {
                        // Create a new item with reduced amount
                        val newItem = ItemStack(Material.TNT, currentAmount - 1)
                        player.inventory.setItemInMainHand(newItem)
                    } else {
                        // Remove the item completely
                        player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                    }
                }
                
                // Spawn primed TNT at player's location
                val tnt = player.world.spawnEntity(
                    player.eyeLocation, 
                    EntityType.PRIMED_TNT
                ) as TNTPrimed
                
                // Set TNT properties
                tnt.fuseTicks = 40 // 2 seconds
                
                // Calculate velocity based on player's looking direction
                val direction = player.location.direction
                val velocity = direction.multiply(1.5) // Adjust throw strength here
                tnt.velocity = velocity
                
                // Play throw sound
                player.world.playSound(
                    player.location,
                    org.bukkit.Sound.ENTITY_TNT_PRIMED,
                    1.0f,
                    1.0f
                )
            }
            // For right-click on block, let the default Minecraft behavior handle it
            // This allows normal TNT placement
        }
    }
}
