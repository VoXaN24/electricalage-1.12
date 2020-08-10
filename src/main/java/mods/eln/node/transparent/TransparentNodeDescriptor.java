package mods.eln.node.transparent;

import mods.eln.generic.GenericItemBlockUsingDamageDescriptor;
import mods.eln.ghost.GhostGroup;
import mods.eln.misc.*;
import mods.eln.node.transparent.TransparentNode.FrontType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static mods.eln.i18n.I18N.tr;

public class TransparentNodeDescriptor extends GenericItemBlockUsingDamageDescriptor {
    public Class ElementClass, RenderClass;
    protected VoltageLevelColor voltageLevelColor = VoltageLevelColor.None;

    public final EntityMetaTag tileEntityMetaTag;

    public TransparentNodeDescriptor(
        String name,
        Class ElementClass,
        Class RenderClass,
        EntityMetaTag tag) {
        super(name);
        this.ElementClass = ElementClass;
        this.RenderClass = RenderClass;
        this.tileEntityMetaTag = tag;
    }


    protected GhostGroup ghostGroup = null;

    public TransparentNodeDescriptor(String name, Class ElementClass, Class RenderClass) {
        this(name, ElementClass, RenderClass, EntityMetaTag.Basic);
    }

    // TODO(1.10): Items rendering.
//    @Override
//    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
//        return voltageLevelColor != VoltageLevelColor.None;
//    }
//
//    @Override
//    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
//        return false;
//    }
//
//    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
//        return false;
//    }
//
//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
//        if (getIcon() == null)
//            return;
//
//        voltageLevelColor.drawIconBackground(type);
//
//        // remove "eln:" to add the full path replace("eln:", "textures/blocks/") + ".png";
//        String icon = getIcon().getIconName().substring(4);
//        UtilsClient.drawIcon(type, new ResourceLocation("eln", "textures/blocks/" + icon + ".png"));
//    }

    public void objItemScale(Obj3D obj) {
        if (obj == null) return;
        float factor = obj.yDim * 0.6f;
        //factor = obj.dimMaxInv*1.0f;
        factor = Math.max(factor, (Math.max(obj.zMax, -obj.xMin) + Math.max(obj.xMax, -obj.zMin)) * 0.7f);
        factor = 1f / factor;

        GL11.glScalef(factor, factor, factor);
        //GL11.glTranslatef((Math.max(obj.zMax,-obj.xMin) - Math.max(obj.xMax,-obj.zMin))*0.5f,-((obj.yMax + Math.max(-obj.xMin,obj.zMax)*0.3f) + (obj.yMin + Math.min(obj.zMin,-obj.xMax)*0.3f))*0.4f,0.0f);
        GL11.glTranslatef((Math.min(obj.zMin, obj.xMin) + Math.max(obj.xMax, obj.zMax)) / 2 - (obj.xMax + obj.xMin) / 2, 1.0f - (obj.xMax + obj.xMin) / 2 - (obj.zMax + obj.zMin) / 2 - (obj.yMax + obj.yMin) / 2, 0.0f);
    }

    public FrontType getFrontType() {
        return FrontType.PlayerViewHorizontal;
    }

    public boolean mustHaveFloor() {
        return true;
    }

    public boolean mustHaveCeiling() {
        return false;
    }

    public boolean mustHaveWall() {
        return false;
    }

    public boolean mustHaveWallFrontInverse() {
        return false;
    }

    //TODO: REFACTOR
    public String checkCanPlace(Coordinate coord, Direction front) {
        Block block;
        boolean needDestroy = false;
        if (mustHaveFloor()) {
            Coordinate temp = new Coordinate(coord);
            temp.move(Direction.YN);
            block = temp.getBlockState().getBlock();
            if (block == null || ((!block.isOpaqueCube(temp.getBlockState())) && block instanceof BlockHopper == false))
                return tr("You can't place this block at this side");
        }
        if (mustHaveCeiling()) {
            Coordinate temp = new Coordinate(coord);
            temp.move(Direction.YP);
            block = temp.getBlockState().getBlock();
            if (block == null || !block.isOpaqueCube(temp.getBlockState())) return tr("You can't place this block at this side");
        }
        if (mustHaveWallFrontInverse()) {
            Coordinate temp = new Coordinate(coord);
            temp.move(front.getInverse());
            block = temp.getBlockState().getBlock();
            if (block == null || !block.isOpaqueCube(temp.getBlockState())) return tr("You can't place this block at this side");
        }
        if (mustHaveWall()) {
            Coordinate temp;
            boolean wall = false;
            temp = new Coordinate(coord);
            temp.move(Direction.XN);
            block = temp.getBlockState().getBlock();
            if (block != null && block.isOpaqueCube(temp.getBlockState())) wall = true;
            temp = new Coordinate(coord);
            temp.move(Direction.XP);
            block = temp.getBlockState().getBlock();
            if (block != null && block.isOpaqueCube(temp.getBlockState())) wall = true;
            temp = new Coordinate(coord);
            temp.move(Direction.ZN);
            block = temp.getBlockState().getBlock();
            if (block != null && block.isOpaqueCube(temp.getBlockState())) wall = true;
            temp = new Coordinate(coord);
            temp.move(Direction.ZP);
            block = temp.getBlockState().getBlock();;
            if (block != null && block.isOpaqueCube(temp.getBlockState())) wall = true;

            if (!wall) return tr("You can't place this block at this side");
        }

        GhostGroup ghostGroup = getGhostGroup(front);
        if (ghostGroup != null && ghostGroup.canBePloted(coord) == false) return tr("Not enough space for this block");
        return null;
    }


    public Direction getFrontFromPlace(Direction side, EntityLivingBase entityLiving) {
        Direction front = Direction.XN;
        switch (getFrontType()) {
            case BlockSide:
                front = side;
                break;
            case BlockSideInv:
                front = side.getInverse();
                break;
            case PlayerView:
                front = Utils.entityLivingViewDirection(entityLiving).getInverse();
                break;
            case PlayerViewHorizontal:
                front = Utils.entityLivingHorizontalViewDirection(entityLiving).getInverse();
                break;

        }
        return front;
    }

    public boolean hasGhostGroup() {
        return ghostGroup != null;
    }

    public GhostGroup getGhostGroup(Direction front) {
        if (ghostGroup == null) return null;
        return ghostGroup.newRotate(front);
    }

    public int getGhostGroupUuid() {

        return -1;
    }

    public int getSpawnDeltaX() {

        return 0;
    }

    public int getSpawnDeltaY() {

        return 0;
    }

    public int getSpawnDeltaZ() {

        return 0;
    }

    public void addCollisionBoxesToList(AxisAlignedBB par5AxisAlignedBB, List<AxisAlignedBB> list, BlockPos pos) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        if (par5AxisAlignedBB.intersects(bb)) list.add(bb);
    }

    public void setGhostGroup(GhostGroup ghostGroup) {
        this.ghostGroup = ghostGroup;
    }
}
