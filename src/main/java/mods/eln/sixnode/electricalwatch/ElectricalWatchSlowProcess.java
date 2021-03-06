package mods.eln.sixnode.electricalwatch;

import mods.eln.item.electricalitem.BatteryItem;
import mods.eln.misc.INBTTReady;
import mods.eln.sim.IProcess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ElectricalWatchSlowProcess implements IProcess, INBTTReady {

    ElectricalWatchElement element;

    boolean upToDate = false;
    long oldDate = 1379;

    public ElectricalWatchSlowProcess(ElectricalWatchElement element) {
        this.element = element;
    }

    double getBatteryLevel() {
        ItemStack batteryStack = element.getInventory().getStackInSlot(ElectricalWatchContainer.batteryId);
        BatteryItem battery = (BatteryItem) BatteryItem.getDescriptor(batteryStack);
        if (battery != null) {
            return battery.getEnergy(batteryStack) / battery.getEnergyMax(batteryStack);
        } else {
            return 0;
        }
    }

    @Override
    public void process(double time) {
        ItemStack batteryStack = element.getInventory().getStackInSlot(ElectricalWatchContainer.batteryId);
        BatteryItem battery = (BatteryItem) BatteryItem.getDescriptor(batteryStack);
        double energy;
        if (battery == null || (energy = battery.getEnergy(batteryStack)) < element.descriptor.powerConsumtion * time * 4) {
            if (upToDate) {
                upToDate = false;
                oldDate = element.sixNode.coordinate.world().getWorldTime();
                if (batteryStack != null) battery.setEnergy(batteryStack, 0);
                element.needPublish();
            }
        } else {
            if (!upToDate) {
                upToDate = true;
                element.needPublish();
            }
            battery.setEnergy(batteryStack, energy - element.descriptor.powerConsumtion * time);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        upToDate = nbt.getBoolean(str + "upToDate");
        oldDate = nbt.getLong(str + "oldDate");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt, String str) {
        nbt.setBoolean(str + "upToDate", upToDate);
        nbt.setLong(str + "oldDate", oldDate);
        return nbt;
    }
}
