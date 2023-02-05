package org.mooner.seungwoomaster.game.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class DoorOpener implements Listener {
    @EventHandler
    public void open(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) return;
        if(clickedBlock.getBlockData() instanceof TrapDoor trapDoor) {
            if(!trapDoor.isOpen()) return;
            Bukkit.getScheduler().runTaskLater(master, () -> {
                trapDoor.setOpen(true);
                clickedBlock.setBlockData(trapDoor);
            }, 10);
        }
    }
}
