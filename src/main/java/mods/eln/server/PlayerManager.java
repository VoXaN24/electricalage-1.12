package mods.eln.server;

import mods.eln.misc.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerManager {

    private Map<EntityPlayerMP, PlayerMetadata> metadataHash = new Hashtable<EntityPlayerMP, PlayerMetadata>();

    public PlayerManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public class PlayerMetadata {
        private int timeout;
        public boolean interactEnable = false;
        public boolean interactRise = false, interactRiseBuffer = false;
        EntityPlayer player;

        public PlayerMetadata(EntityPlayer p) {
            timeoutReset();
            this.player = p;
        }

        public boolean needDelete() {
            return timeout == 0;
        }

        public void timeoutReset() {
            timeout = 20 * 120;
        }

        public void timeoutDec() {
            timeout--;
            if (timeout < 0)
                timeout = 0;
        }

        public void setInteractEnable(boolean interactEnable) {
            if (!this.interactEnable && interactEnable) {
                interactRiseBuffer = true;
                Utils.println("interactRiseBuffer");
            }
            this.interactEnable = interactEnable;

            timeoutReset();
            Utils.println("interactEnable : " + interactEnable);
        }

        public boolean getInteractEnable() {
            timeoutReset();
            return interactEnable;
            //return player.isSneaking();
        }

		/*public boolean getInteractRise() {
            timeoutReset();
			return interactRise;
		}*/
    }

    public void clear() {
        metadataHash.clear();
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        if (event.phase != Phase.START) return;
        for (Entry<EntityPlayerMP, PlayerMetadata> entry : metadataHash.entrySet()) {
            PlayerMetadata p = entry.getValue();

            p.interactRise = p.interactRiseBuffer;
            p.interactRiseBuffer = false;

            if (p.needDelete()) {
                metadataHash.remove(entry.getKey());
            }
        }
    }

    public PlayerMetadata get(EntityPlayerMP player) {
        PlayerMetadata metadata = metadataHash.get(player);
        if (metadata != null)
            return metadata;
        metadataHash.put(player, new PlayerMetadata(player));
        return metadataHash.get(player);
    }

    public PlayerMetadata get(EntityPlayer player) {
        return get((EntityPlayerMP) player);
    }
}
