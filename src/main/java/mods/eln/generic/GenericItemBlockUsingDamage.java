package mods.eln.generic;

import mods.eln.misc.Utils;
import mods.eln.misc.UtilsClient;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GenericItemBlockUsingDamage<Descriptor extends GenericItemBlockUsingDamageDescriptor> extends ItemBlock {

    public Hashtable<Integer, Descriptor> subItemList = new Hashtable<Integer, Descriptor>();
    public ArrayList<Integer> orderList = new ArrayList<Integer>();
    public ArrayList<Descriptor> descriptors = new ArrayList<Descriptor>();

    public Descriptor defaultElement = null;

    public GenericItemBlockUsingDamage(Block b) {
        super(b);
        setHasSubtypes(true);
    }

    public void setDefaultElement(Descriptor descriptor) {
        defaultElement = descriptor;
    }

    public void doubleEntry(int src, int dst) {
        subItemList.put(dst, subItemList.get(src));
    }

    public void addDescriptor(int damage, Descriptor descriptor) {
        subItemList.put(damage, descriptor);
        ItemStack stack = new ItemStack(this, 1, damage);
        stack.setTagCompound(descriptor.getDefaultNBT());
        //LanguageRegistry.addName(stack, descriptor.name);
        orderList.add(damage);
        descriptors.add(descriptor);
        descriptor.setParent(this, damage);
        // TODO(1.10): Mumble mumble.
        throw new IllegalStateException("This code is fucked.");
//        GameRegistry.register(descriptor.parentItem);
    }

    public void addWithoutRegistry(int damage, Descriptor descriptor) {
        subItemList.put(damage, descriptor);
        ItemStack stack = new ItemStack(this, 1, damage);
        stack.setTagCompound(descriptor.getDefaultNBT());
        descriptor.setParent(this, damage);
    }

    public Descriptor getDescriptor(int damage) {
        return subItemList.get(damage);
    }

    public Descriptor getDescriptor(ItemStack itemStack) {
        if (itemStack == null) return defaultElement;
        if (itemStack.getItem() != this) return defaultElement;
        return getDescriptor(itemStack.getItemDamage());
    }

	/*
    @Override
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int damage) {
		return getDescriptor(damage).getIconId();
		
	}
	//caca1.5.1
	@Override
	public String getTextureFile () {
		return CommonProxy.ITEMS_PNG;
	}
	@Override
	public String getItemNameIS(ItemStack itemstack) {
		return getItemName() + "." + getDescriptor(itemstack).name;
	}
	*/

	/*@Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
		Descriptor desc = getDescriptor(par1ItemStack);
		if(desc == null) return "Unknown";
        return desc.getName(par1ItemStack);
    }*/

    @Override
    public String getTranslationKey(ItemStack stack) {
        Descriptor desc = getDescriptor(stack);
        if (desc == null) {
            return this.getClass().getName();
        } else {
            return desc.name.replaceAll("\\s+", "_");
        }
    }

    // TODO(1.10): Fix item rendering.
//    @Override
//    public IIcon getIconFromDamage(int damage) {
//        Descriptor desc = getDescriptor(damage);
//        if (desc == null) return null;
//        return desc.getIcon();
//    }
//
//    @Override
//    @SideOnly(value = Side.CLIENT)
//    public void registerIcons(IIconRegister iconRegister) {
//        for (GenericItemBlockUsingDamageDescriptor descriptor : subItemList.values()) {
//            descriptor.updateIcons(iconRegister);
//        }
//    }


    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> items) {
        // TODO(1.12)
        throw new IllegalStateException("Don't get me started.");

        // You can also take a more direct approach and do each one individual but I prefer the lazy / right way
        //for(Entry<Integer, Descriptor> entry : subItemList.entrySet()

//        for (int id : orderList) {
//            ItemStack stack = Utils.newItemStack(itemID, 1, id);
//            stack.setTagCompound(subItemList.get(id).getDefaultNBT());
//            list.add(stack);
//        }
    }

    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        Descriptor desc = getDescriptor(itemStack);
        if (desc == null) return;
        List listFromDescriptor = new ArrayList();
        desc.addInformation(itemStack, entityPlayer, listFromDescriptor, par4);
        UtilsClient.showItemTooltip(listFromDescriptor, list);
    }

//    public boolean onEntityItemUpdate(EntityItem entityItem) {
//        Descriptor desc = getDescriptor(entityItem.getEntityItem());
//        if (desc != null) return desc.onEntityItemUpdate(entityItem);
//        return false;
//    }
//
//    //TODO: Maybe its onItemUse only?
//    @Override
//    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
//        Descriptor desc = getDescriptor(stack);
//        if (desc != null) return desc.onItemUse(stack, player);
//        return EnumActionResult.FAIL;
//    }
}
