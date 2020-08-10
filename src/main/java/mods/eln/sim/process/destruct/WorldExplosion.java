package mods.eln.sim.process.destruct;

import mods.eln.Eln;
import mods.eln.init.Config;
import mods.eln.misc.Coordinate;
import mods.eln.node.six.SixNodeElement;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.simplenode.energyconverter.EnergyConverterElnToOtherNode;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class WorldExplosion implements IDestructable {

    Object origine;

    Coordinate c;
    float strength;
    String type;

    public WorldExplosion(Coordinate c) {
        this.c = c;
    }

    public WorldExplosion(SixNodeElement e) {
        this.c = e.getCoordinate();
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion(TransparentNodeElement e) {
        this.c = e.coordinate();
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion(EnergyConverterElnToOtherNode e) {
        this.c = e.coordinate;
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion cableExplosion() {
        strength = 1.5f;
        return this;
    }

    public WorldExplosion machineExplosion() {
        strength = 3;
        return this;
    }

    @Override
    public void destructImpl() {
        //NodeManager.instance.removeNode(NodeManager.instance.getNodeFromCoordinate(c));

        if (Config.INSTANCE.getExplosionEnable())
            c.world().createExplosion((Entity) null, c.pos.getX(), c.pos.getY(), c.pos.getZ(), strength, true);
        else
            c.world().setBlockToAir(new BlockPos(c.pos.getX(), c.pos.getY(), c.pos.getZ()));
    }

    @Override
    public String describe() {
        return String.format("%s (%s)", this.type, this.c.toString());
    }
}
