package mods.eln.sim.nbt;

import mods.eln.Eln;
import mods.eln.init.Cable;
import mods.eln.misc.Utils;

public class NbtElectricalGateInput extends NbtElectricalLoad {

    public NbtElectricalGateInput(String name) {
        super(name);
        Cable.Companion.getSignal().descriptor.applyTo(this);
    }

    public String plot(String str) {
        return Utils.plotSignal(getU(), getI()); // str  + " "+ Utils.plotVolt("", getU()) + Utils.plotAmpere("", getCurrent());
    }

    public boolean stateHigh() {
        return getU() > Cable.SVU * 0.6;
    }

    public boolean stateLow() {
        return getU() < Cable.SVU * 0.2;
    }

    public double getNormalized() {
        double norm = getU() / Cable.SVU;
        if (norm < 0.0) norm = 0.0;
        if (norm > 1.0) norm = 1.0;
        return norm;
    }

    public double getBornedU() {
        double U = this.getU();
        if (U < 0.0) U = 0.0;
        if (U > Cable.SVU) U = Cable.SVU;
        return U;
    }
}
