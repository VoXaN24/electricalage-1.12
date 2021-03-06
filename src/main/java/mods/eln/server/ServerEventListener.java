package mods.eln.server;

import mods.eln.item.electricalitem.TreeCapitation;
import mods.eln.misc.Coordinate;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import mods.eln.Eln;
import mods.eln.misc.Utils;
import mods.eln.node.NodeManager;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.LinkedList;

public class ServerEventListener {

    private LinkedList<EntityLightningBolt> lightningListNext = new LinkedList<EntityLightningBolt>();
    private LinkedList<EntityLightningBolt> lightningList = new LinkedList<EntityLightningBolt>();

    public ServerEventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        if (event.phase != Phase.END) return;

        lightningList = lightningListNext;
        lightningListNext = new LinkedList<EntityLightningBolt>();

        TreeCapitation.INSTANCE.process(0.05);
    }

    @SubscribeEvent
    public void onNewEntity(EntityConstructing event) {
        if (event.getEntity() instanceof EntityLightningBolt) {
            lightningListNext.add((EntityLightningBolt) event.getEntity());
        }
    }

    public void clear() {
        lightningList.clear();
    }

    public double getLightningClosestTo(Coordinate c) {
        double best = 10000000;
        for (EntityLightningBolt l : lightningList) {
            if (c.world() != l.world) continue;
            double d = l.getDistance(c.pos.getX(), c.pos.getY(), c.pos.getZ());
            if (d < best) best = d;
        }
        return best;
    }


    private HashSet<Integer> loadedWorlds = new HashSet<Integer>();

    @SubscribeEvent
    public void onWorldLoad(Load e) {
        World w = e.getWorld();
        if (w.isRemote) return;
        loadedWorlds.add(w.provider.getDimension());
        FileNames fileNames = new FileNames(e);

        try {
            readSave(fileNames.worldSave);
        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                System.out.println("Using BACKUP Electrical Age save: " + fileNames.backupSave);
                readSave(fileNames.backupSave);
            } catch (Exception ex2) {
                ex2.printStackTrace();
                System.out.println("Failed to read backup save!");
                ElnWorldStorage storage = ElnWorldStorage.forWorld(w);
            }
        }
    }

    private void readSave(Path worldSave) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(worldSave));
        NBTTagCompound nbt = CompressedStreamTools.readCompressed(inputStream);
        readFromEaWorldNBT(nbt);
    }

    @SubscribeEvent
    public void onWorldUnload(Unload e) {
        World w = e.getWorld();
        int dim = w.provider.getDimension();
        if (w.isRemote) return;
        loadedWorlds.remove(dim);
        try {
            NodeManager.instance.unload(dim);
            Eln.ghostManager.unload(dim);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SubscribeEvent
    public void onWorldSave(Save e) {
        World w = e.getWorld();
        int dim = w.provider.getDimension();
        if (w.isRemote) return;
        if (!loadedWorlds.contains(dim)) {
            //System.out.println("I hate you minecraft");
            return;
        }
        try {
            NBTTagCompound nbt = new NBTTagCompound();
            writeToEaWorldNBT(nbt, dim);

            FileNames fileNames = new FileNames(e);

            // Write a new save to a temporary file.
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream(512 * 1024);
            CompressedStreamTools.writeCompressed(nbt, bytes);
            Files.write(fileNames.tempSave, bytes.toByteArray());

            // Replace backup save with old save, and old save with new one.
            if (Files.exists(fileNames.worldSave))
                replaceFile(fileNames.worldSave, fileNames.backupSave);
            replaceFile(fileNames.tempSave, fileNames.worldSave);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void replaceFile(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }


    static void readFromEaWorldNBT(NBTTagCompound nbt) {
        try {
            NodeManager.instance.loadFromNbt(nbt.getCompoundTag("nodes"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Eln.ghostManager.loadFromNBT(nbt.getCompoundTag("ghost"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void writeToEaWorldNBT(NBTTagCompound nbt, int dim) {
        try {
            NodeManager.instance.saveToNbt(Utils.newNbtTagCompund(nbt, "nodes"), dim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Eln.ghostManager.saveToNBT(Utils.newNbtTagCompund(nbt, "ghost"), dim);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class FileNames {
        final Path worldSave;
        final Path tempSave;
        final Path backupSave;

        FileNames(WorldEvent e) {
            String saveName = getEaWorldSaveName(e.getWorld());
            worldSave = FileSystems.getDefault().getPath(saveName);
            tempSave = FileSystems.getDefault().getPath(saveName + ".tmp");
            backupSave = FileSystems.getDefault().getPath(saveName + ".bak");
        }

        private String getEaWorldSaveName(World w) {
            return Utils.getMapFolder() + "data/electricalAgeWorld" + w.provider.getDimension() + ".dat";
        }
    }
}
