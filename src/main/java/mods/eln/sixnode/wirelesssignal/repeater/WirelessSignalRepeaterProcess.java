package mods.eln.sixnode.wirelesssignal.repeater;

import mods.eln.misc.Coordinate;
import mods.eln.misc.Utils;
import mods.eln.sim.IProcess;
import mods.eln.sixnode.wirelesssignal.IWirelessSignalSpot;
import mods.eln.sixnode.wirelesssignal.IWirelessSignalTx;
import mods.eln.sixnode.wirelesssignal.WirelessUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class WirelessSignalRepeaterProcess implements IProcess, IWirelessSignalSpot {

    private WirelessSignalRepeaterElement rx;

    double sleepTimer = 0;
    IWirelessSignalSpot spot;

    boolean boot = true;

    public WirelessSignalRepeaterProcess(WirelessSignalRepeaterElement rx) {
        this.rx = rx;
    }

    @Override
    public void process(double time) {
        sleepTimer -= time;
        if (sleepTimer < 0) {
            sleepTimer += Utils.rand(1.2, 2);

            spot = WirelessUtils.buildSpot(rx.getCoordinate(), null, rx.descriptor.range);

            if (boot) {
                boot = false;
                //IWirelessSignalSpot.spots.add(this);
            }
        }
    }

    @Override
    public HashMap<String, ArrayList<IWirelessSignalTx>> getTx() {
        return spot.getTx();
    }

    @Override
    public ArrayList<IWirelessSignalSpot> getSpot() {
        return spot.getSpot();
    }

    @Override
    public Coordinate getCoordinate() {
        return rx.getCoordinate();
    }

    @Override
    public int getRange() {
        return rx.descriptor.range;
    }
}
