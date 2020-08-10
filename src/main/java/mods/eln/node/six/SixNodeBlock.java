package mods.eln.node.six;

import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.NodeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SixNodeBlock extends NodeBlock {
    // public static ArrayList<Integer> repertoriedItemStackId = new ArrayList<Integer>();

    // private IIcon icon;
    public SixNodeBlock(Material material, Class tileEntityClass) {
        super(material, tileEntityClass, 0);

        // setBlockTextureName("eln:air");
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        SixNodeEntity entity = (SixNodeEntity) world.getTileEntity(pos);
        if (entity != null) {
            SixNodeElementRender render = entity.elementRenderList[Direction.fromFacing(target.sideHit).getInt()];
            if (render != null) {
                return render.sixNodeDescriptor.newItemStack();
            }
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    // TODO(1.10): Fix item render.
//    @Override
//    public void registerBlockIcons(IIconRegister r) {
//        super.registerBlockIcons(r);
//        this.blockIcon = r.registerIcon("eln:air");
//    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, BlockPos pos) {
        if (nodeHasCache(par1World, pos) || hasVolume(par1World, pos))
            return super.getCollisionBoundingBox(par1World.getBlockState(pos), par1World, pos);
        else
            return null;
    }


    public boolean hasVolume(World world, BlockPos pos) {
        SixNodeEntity entity = getEntity(world, pos);
        if (entity == null) return false;
        return entity.hasVolume(world, pos.getX(), pos.getY(), pos.getZ());

    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)  {
        return 0.3f;
    }

    //@Override
    public int getDamageValue(World world, BlockPos pos) {
        if (world == null)
            return 0;
        SixNodeEntity entity = getEntity(world, pos);
        return entity == null ? 0 : entity.getDamageValue(world, pos.getX(), pos.getY(), pos.getZ());
    }

    SixNodeEntity getEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof SixNodeEntity)
            return (SixNodeEntity) tileEntity;
        Utils.println("ASSERTSixNodeEntity getEntity() null");
        return null;

    }

    // TODO(1.12) Whatever this was, it's broken now.
    // @SideOnly(Side.CLIENT)
//    public void getSubBlocks(Items par1, CreativeTabs tab, List subItems) {
//        Eln.sixNodeItem.getSubItems(par1, tab, subItems);
//    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getRenderType();
    }

	/*
	 * @Override public int getLightOpacity(World world, int x, int y, int z) {
	 * 
	 * return 255; }
	 */

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {

        return null;
    }

    public int quantityDropped(Random par1Random) {
        return 0;
    }

    // TODO(1.10): Fix item rendering.
//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
//        TileEntity e = w.getTileEntity(x, y, z);
//        if (e == null) return blockIcon;
//        SixNodeEntity sne = (SixNodeEntity) e;
//        Block b = sne.sixNodeCacheBlock;
//        if (b == ModBlock.air) return blockIcon;
//        // return b.getIcon(w, x, y, z, side);
//        try {
//            return b.getIcon(side, sne.sixNodeCacheBlockMeta);
//        } catch (Exception e2) {
//            return blockIcon;
//        }
//
//        // return ModBlock.sand.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_, p_149673_5_);
//        // return ModBlock.stone.getIcon(w, x, y, z, side);
//    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World par1World, BlockPos pos, EnumFacing facing) {
		/* see canPlaceBlockAt; it needs changing if this method is fixed */
        return true;/*
					 * if(par1World.isRemote) return true; SixNodeEntity tileEntity = (SixNodeEntity) par1World.getBlockTileEntity(par2, par3, par4); if(tileEntity == null || (tileEntity instanceof SixNodeEntity) == false) return true; Direction direction = Direction.fromIntMinecraftSide(par5); SixNode node = (SixNode) tileEntity.getNode(); if(node == null) return true; if(node.getSideEnable(direction))return false;
					 * 
					 * return true;
					 */
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, BlockPos pos) {
		/* This should probably call canPlaceBlockOnSide with each
		 * appropriate side to see if it can go somewhere.
		 * (cf. BlockLever, BlockTorch, etc)

		 * Currently, canPlaceBlockOnSide returns true and defers
		 * check to other code.  The rest of the sixnode code isn't
		 * expecting blind canPlaceBlockAt to work, so things that
		 * call it (e.g. Rannuncarpus) confuse it terribly and leak
		 * cables and nodepieces.

		 * So for now, make the Rannuncarpus et al ignore it.
		 */
		return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {

    }

    /*
     * @Override public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int minecraftSide, float vx, float vy, float vz) { SixNodeEntity tileEntity = (SixNodeEntity) world.getBlockTileEntity(x, y, z);
     *
     * return tileEntity.onBlockActivated(entityPlayer, Direction.fromIntMinecraftSide(minecraftSide),vx,vy,vz); }
     */

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos,  EntityPlayer entityPlayer, boolean willHarvest) {
        if (world.isRemote) return false;

        SixNodeEntity tileEntity = (SixNodeEntity) world.getTileEntity(pos);

        RayTraceResult raytrace = collisionRayTrace(world, pos, entityPlayer);
        if (raytrace == null) return false;

        SixNode sixNode = (SixNode) tileEntity.getNode();
        if (sixNode == null) return true;
        if (sixNode.sixNodeCacheBlock.isAir(world.getBlockState(pos), world, pos)) {

            if (!(Utils.isCreative((EntityPlayerMP) entityPlayer))) {
                ItemStack stack = new ItemStack(sixNode.sixNodeCacheBlock, 1, sixNode.sixNodeCacheBlockMeta);
                sixNode.dropItem(stack);
            }

            sixNode.sixNodeCacheBlock = Blocks.AIR;

            Chunk chunk = world.getChunk(pos);
            Utils.generateHeightMap(chunk);
            // TODO(1.10): Was this important?
            //Utils.updateSkylight(chunk);
            //chunk.generateSkylightMap();
            //Utils.updateAllLightTypes(world, pos);

            sixNode.setNeedPublish(true);
            return false;
        }
        if (!sixNode.playerAskToBreakSubBlock((EntityPlayerMP) entityPlayer, Direction.fromIntMinecraftSide(raytrace.sideHit.getIndex())))
            return false;

        if (sixNode.getIfSideRemain()) return true;

        return super.removedByPlayer(world.getBlockState(pos), world,  pos, entityPlayer, willHarvest);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, Block par5, int par6) {

        if (!world.isRemote) {
            SixNodeEntity tileEntity = (SixNodeEntity) world.getTileEntity(pos);
            SixNode sixNode = (SixNode) tileEntity.getNode();
            if (sixNode == null) return;

            for (Direction direction : Direction.values()) {
                if (sixNode.getSideEnable(direction)) {
                    sixNode.deleteSubBlock(null, direction);
                }
            }
        }
        super.breakBlock(world, pos, par5, par6);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        SixNodeEntity tileEntity = (SixNodeEntity) world.getTileEntity(pos);
        SixNode sixNode = (SixNode) tileEntity.getNode();
        if (sixNode == null) return;

        for (Direction direction : Direction.values()) {
            if (sixNode.getSideEnable(direction)) {
                if (!getIfOtherBlockIsSolid(world, pos, direction)) {
                    sixNode.deleteSubBlock(null, direction);
                }
            }

        }

        if (!sixNode.getIfSideRemain()) {
            // TODO(1.10): Set this block to air.
        } else {
            super.onNeighborChange(world, pos, neighbor);
        }
    }

    double w = 0.0;

    boolean[] booltemp = new boolean[6];

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        // TODO(??): Pretty sure this can be improved. Do we even want to use collisionRayTrace?
        final int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (nodeHasCache(world, pos)) return super.collisionRayTrace(blockState, world, pos, start, end);
        SixNodeEntity tileEntity = (SixNodeEntity) world.getTileEntity(pos);
        if (tileEntity == null) return null;
        if (world.isRemote) {
            booltemp[0] = tileEntity.getSyncronizedSideEnable(Direction.XN);
            booltemp[1] = tileEntity.getSyncronizedSideEnable(Direction.XP);
            booltemp[2] = tileEntity.getSyncronizedSideEnable(Direction.YN);
            booltemp[3] = tileEntity.getSyncronizedSideEnable(Direction.YP);
            booltemp[4] = tileEntity.getSyncronizedSideEnable(Direction.ZN);
            booltemp[5] = tileEntity.getSyncronizedSideEnable(Direction.ZP);
            SixNodeEntity entity = getEntity(world, pos);
            if (entity != null) {
                SixNodeElementRender element = entity.elementRenderList[Direction.YN.getInt()];
                // setBlockBounds(0, 0, 0, 1, 1, 1);
                if (element != null && element.sixNodeDescriptor.hasVolume()) {
                    // We're going to assume this is a lamp socket on the roof? Okay.
                    return new RayTraceResult(new Vec3d(0.5, 0.5, 0.5), Direction.YN.toForge(), pos);
                }
            }

        } else {
            SixNode sixNode = (SixNode) tileEntity.getNode();
            if (sixNode == null) return null;
            booltemp[0] = sixNode.getSideEnable(Direction.XN);
            booltemp[1] = sixNode.getSideEnable(Direction.XP);
            booltemp[2] = sixNode.getSideEnable(Direction.YN);
            booltemp[3] = sixNode.getSideEnable(Direction.YP);
            booltemp[4] = sixNode.getSideEnable(Direction.ZN);
            booltemp[5] = sixNode.getSideEnable(Direction.ZP);
            SixNodeEntity entity = getEntity(world, pos);
            if (entity != null) {
                NodeBase node = entity.getNode();
                if (node != null && node instanceof SixNode) {
                    SixNodeElement element = ((SixNode) node).sideElementList[Direction.YN.getInt()];
                    if (element != null && element.sixNodeElementDescriptor.hasVolume())
                        // Yup, still a lamp socket.
                        return new RayTraceResult(new Vec3d(0.5, 0.5, 0.5), Direction.YN.toForge(), pos);
                }
            }

        }
        // XN

        if (isIn(x, end.x, start.x) && booltemp[0]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (x - start.x) / (end.x - start.x);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitY, y + w, y + 1 - w) && isIn(hitZ, z + w, z + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.XN.toForge(), pos);
            }
        }
        // XP
        if (isIn(x + 1, start.x, end.x) && booltemp[1]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (x + 1 - start.x) / (end.x - start.x);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitY, y + w, y + 1 - w) && isIn(hitZ, z + w, z + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.XP.toForge(), pos);
            }
        }
        // YN
        if (isIn(y, end.y, start.y) && booltemp[2]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (y - start.y) / (end.y - start.y);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitX, x + w, x + 1 - w) && isIn(hitZ, z + w, z + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.YN.toForge(), pos);
            }

        }
        // YP
        if (isIn(y + 1, start.y, end.y) && booltemp[3]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (y + 1 - start.y) / (end.y - start.y);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitX, x + w, x + 1 - w) && isIn(hitZ, z + w, z + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.YP.toForge(), pos);
            }
        }
        // ZN
        if (isIn(z, end.z, start.z) && booltemp[4]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (z - start.z) / (end.z - start.z);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitY, y + w, y + 1 - w) && isIn(hitX, x + w, x + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.ZN.toForge(), pos);

            }
        }
        // ZP
        if (isIn(z + 1, start.z, end.z) && booltemp[5]) {
            double hitX, hitY, hitZ, ratio;
            ratio = (z + 1 - start.z) / (end.z - start.z);
            if (ratio <= 1.1) {
                hitX = start.x + ratio * (end.x - start.x);
                hitY = start.y + ratio * (end.y - start.y);
                hitZ = start.z + ratio * (end.z - start.z);
                if (isIn(hitY, y + w, y + 1 - w) && isIn(hitX, x + w, x + 1 - w))
                    return new RayTraceResult(new Vec3d(hitX, hitY, hitZ), Direction.ZP.toForge(), pos);
            }
        }

        return null;
    }

    private static boolean isIn(double value, double min, double max) {
        return value >= min && value <= max;
    }

    private RayTraceResult collisionRayTrace(World world, BlockPos pos, EntityPlayer entityLiving) {
        double distanceMax = 5.0;
        Vec3d start = new Vec3d(entityLiving.posX, entityLiving.posY, entityLiving.posZ);

        // TODO(1.10): Really?
        if (!world.isRemote)
            start = start.add(0, 1.62, 0);
        Vec3d var5 = entityLiving.getLook(0.5f);
        Vec3d end = start.add(var5.x * distanceMax, var5.y * distanceMax, var5.z * distanceMax);

        return collisionRayTrace(world.getBlockState(pos), world, pos, start, end);
    }

    boolean getIfOtherBlockIsSolid(IBlockAccess world, BlockPos pos, Direction direction) {
        pos = direction.applied(pos, 1);

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(state, world, pos)) return false;
        return state.isOpaqueCube();
    }

    private boolean nodeHasCache(IBlockAccess world, BlockPos pos) {
        if (Utils.isRemote(world)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && tileEntity instanceof SixNodeEntity)
                return ((SixNodeEntity) tileEntity).sixNodeCacheBlock != Blocks.AIR;
            else
                Utils.println("ASSERT B public boolean nodeHasCache(World world, int x, int y, int z) ");

        } else {
            SixNodeEntity tileEntity = (SixNodeEntity) world.getTileEntity(pos);
            SixNode sixNode = (SixNode) tileEntity.getNode();
            if (sixNode != null)
                return sixNode.sixNodeCacheBlock != Blocks.AIR;
            else
                Utils.println("ASSERT A public boolean nodeHasCache(World world, int x, int y, int z) ");
        }
        return false;
    }

    // TODO(1.10): This has to be done with block-states now.
//    @Override
//    public int getLightOpacity(IBlockAccess w, int x, int y, int z) {
//
//        TileEntity e = w.getTileEntity(x, y, z);
//        if (e == null) return 0;
//        SixNodeEntity sne = (SixNodeEntity) e;
//        Block b = sne.sixNodeCacheBlock;
//        if (b == ModBlock.air) return 0;
//        // return b.getIcon(w, x, y, z, side);
//        try {
//            return b.getLightOpacity();
//        } catch (Exception e2) {
//            return 255;
//        }
//    }

    public String getNodeUuid() {
        return "s";
    }

    // TODO(1.10): Should probably be done by block states.
//    @Override
//    @SideOnly(Side.CLIENT)
//    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
//        if (hasVolume(w, x, y, z)) return super.getSelectedBoundingBoxFromPool(w, x, y, z);
//        MovingObjectPosition col = collisionRayTrace(w, x, y, z, Minecraft.getMinecraft().player);
//        double h = 0.2;
//        double hn = 1 - h;
//
//        double b = 0.02;
//        double bn = 1 - 0.02;
//        if (col != null) {
//            // Utils.println(Direction.fromIntMinecraftSide(col.sideHit));
//            switch (Direction.fromIntMinecraftSide(col.sideHit)) {
//                case XN:
//                    return AxisAlignedBB.getBoundingBox((double) x + b, (double) y, (double) z, (double) x + h, (double) y + 1, (double) z + 1);
//                case XP:
//                    return AxisAlignedBB.getBoundingBox((double) x + hn, (double) y, (double) z, (double) x + bn, (double) y + 1, (double) z + 1);
//                case YN:
//                    return AxisAlignedBB.getBoundingBox((double) x, (double) y + b, (double) z, (double) x + 1, (double) y + h, (double) z + 1);
//                case YP:
//                    return AxisAlignedBB.getBoundingBox((double) x, (double) y + hn, (double) z, (double) x + 1, (double) y + bn, (double) z + 1);
//                case ZN:
//                    return AxisAlignedBB.getBoundingBox((double) x, (double) y, (double) z + b, (double) x + 1, (double) y + 1, (double) z + h);
//                case ZP:
//                    return AxisAlignedBB.getBoundingBox((double) x, (double) y, (double) z + hn, (double) x + 1, (double) y + 1, (double) z + bn);
//
//            }
//        }
//        return AxisAlignedBB.getBoundingBox(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);//super.getSelectedBoundingBoxFromPool(w, x, y, z);
//        // return AxisAlignedBB.getBoundingBox((double)p_149633_2_ , (double)p_149633_3_ , (double)p_149633_4_ + this.minZ+0.2, (double)p_149633_2_ + this.maxX, (double)p_149633_3_ + this.maxY, (double)p_149633_4_ + this.maxZ);
//        // return super.getSelectedBoundingBoxFromPool(w, x, y, z);
//    }
}
