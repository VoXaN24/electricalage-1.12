package mods.eln.sixnode.electricaldatalogger;

import mods.eln.generic.GenericItemUsingDamageDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DataLogsPrintDescriptor extends GenericItemUsingDamageDescriptor {

    public DataLogsPrintDescriptor(String name) {
        super(name);
    }

    public void initializeStack(ItemStack stack, DataLogs logs) {
        NBTTagCompound nbt = new NBTTagCompound();
        logs.writeToNBT(nbt, "");//.setByteArray("logs", logs.copyLog());
        stack.setTagCompound(nbt);
    }

    // TODO(1.10): Fix item rendering.
//    @Override
//    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
//        return true;
//    }
//
//    @Override
//    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
//        return true;
//    }
//
//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
//        NBTTagCompound nbt = item.getTagCompound();
//        //byte [] logsArray = nbt.getByteArray("logs");
//        //if(logsArray != null) {
//
//        GL11.glLineWidth(1f);
//        GL11.glColor4f(1f, 0f, 0f, 1f);
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glPushMatrix();
//
//        //	GL11.glScalef(1f, -1f, 1f);
//        //	GL11.glTranslatef(0.f, -0.5f, 0.5f);
//        //GL11.glRotatef(90, 0f, 1f, 0f);
//        GL11.glTranslatef(-0.5f, -0.5f, 0.1f);
//        DataLogs.draw(nbt, 1f, 1f, "");
//        //	DataLogs.draw(logsArray, logsArray.length);
//        GL11.glPopMatrix();
//        GL11.glEnable(GL11.GL_LIGHTING);
//        //}
//    }
}
