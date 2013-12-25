package com.dynious.blex.tileentity;

import com.dynious.blex.config.Filter;
import com.dynious.blex.helper.ItemStackHelper;
import cpw.mods.fml.common.Optional;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;

public class TileAdvancedFilteredBlockExtender extends TileBlockExtender
{
    public boolean spreadItems = false;
    private byte[] insertDirection = { 1, 1, 1, 1, 1, 1, 1};
    private int bestSlot;
    private boolean shouldUpdateBestSlot = true;
    private int lastSlotSide;
    private ItemStack lastStack;
    public boolean blacklist = true;
    public Filter filter = new Filter();
    private byte maxStackSize = 64;
    public boolean restrictExtraction = false;

    public byte[] getInsertDirections()
    {
        return insertDirection;
    }

    public void setInsertDirection(int from, int value)
    {
        if (value > 5)
            value = 0;
        insertDirection[from] = (byte) value;
    }

    public void setConnectedSide(int connectedSide)
    {
        super.setConnectedSide(connectedSide);
        if (connectedDirection != ForgeDirection.UNKNOWN)
        {
            for (int i = 0; i < ForgeDirection.values().length; i++)
            {
                insertDirection[i] = (byte)connectedDirection.getOpposite().ordinal();
            }
        }
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, int i2)
    {
        if (spreadItems)
        {
            if (lastSlotSide != getInputSide(ForgeDirection.getOrientation(i2)).ordinal() || !ItemStackHelper.areItemStacksEqual(itemStack, lastStack) || shouldUpdateBestSlot)
            {
                updateBestSlot(i2, itemStack);
                shouldUpdateBestSlot = false;
            }
            if (i != bestSlot || !super.canInsertItem(bestSlot, itemStack, getInputSide(ForgeDirection.getOrientation(i2)).ordinal()))
            {
                return false;
            }
            shouldUpdateBestSlot = true;
            return blacklist ? !filter.passesFilter(itemStack) : filter.passesFilter(itemStack);
        }
        else
        {
            return super.canInsertItem(i, itemStack, getInputSide(ForgeDirection.getOrientation(i2)).ordinal()) && (blacklist ? !filter.passesFilter(itemStack) : filter.passesFilter(itemStack));
        }
    }

    private void updateBestSlot(int side, ItemStack itemStack)
    {
        int bestSize = Integer.MAX_VALUE;
        for (int slot = 0; slot < getSizeInventory(); slot++)
        {
            ItemStack stack = getStackInSlot(slot);
            if (!super.canInsertItem(slot, itemStack, side))
            {
                continue;
            }
            if (stack == null)
            {
                bestSlot = slot;
                break;
            }
            if (ItemStackHelper.areItemStacksEqual(itemStack, stack) && stack.stackSize < bestSize)
            {
                bestSlot = slot;
                bestSize = stack.stackSize;
            }
        }
        lastSlotSide = side;
        lastStack = itemStack;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, int i2)
    {
        return (super.canExtractItem(i, itemStack, i2) && !(restrictExtraction && blacklist ? !filter.passesFilter(itemStack) : filter.passesFilter(itemStack)));
    }

    public void setMaxStackSize(byte maxStackSize)
    {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getInventoryStackLimit()
    {
        if (inventory != null)
        {
            return Math.min(super.getInventoryStackLimit(), maxStackSize);
        }
        return maxStackSize;
    }

    @Override
    public ForgeDirection getInputSide(ForgeDirection side)
    {
        return ForgeDirection.getOrientation(insertDirection[side.ordinal()]);
    }

    /*
    ComputerCraft interaction
    */

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public String getType()
    {
        return "advanced_filtered_block_extender";
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public String[] getMethodNames()
    {
        return ArrayUtils.addAll(super.getMethodNames(), "getMaxStackSize", "setMaxStackSize", "getSpread", "setSpread", "getInputSide", "setInputSide", "isFilterEnabled", "setFilterEnabled");
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
    {
        Object[] superArr = super.callMethod(computer, context, method, arguments);
        if (superArr != null)
        {
            return superArr;
        }
        switch (method)
        {
            case 2:
                return new Integer[]{(int)maxStackSize};
            case 3:
                if (arguments.length > 0 && arguments[0] instanceof Double)
                {
                    double arg = (Double)arguments[0];
                    if (arg >= 0 && arg <= Byte.MAX_VALUE)
                    {
                        setMaxStackSize((byte)arg);
                        return new Boolean[]{true};
                    }
                }
                return new Boolean[]{false};
            case 4:
                return new Boolean[]{spreadItems};
            case 5:
                if (arguments.length > 0 && arguments[0] instanceof Boolean)
                {
                    spreadItems = (Boolean)arguments[0];
                    return new Boolean[]{true};
                }
                return new Boolean[]{false};
            case 6:
                if (arguments.length > 0 && arguments[0] instanceof Double)
                {
                    double arg = (Double)arguments[0];
                    if (arg >= 0 && arg < ForgeDirection.values().length)
                        return new Integer[]{(int)insertDirection[(byte)arg]};
                }
                return new Boolean[]{false};
            case 7:
                if (arguments.length > 1 && arguments[0] instanceof Double && arguments[1] instanceof Double)
                {
                    double side = (Double)arguments[0];
                    double value = (Double)arguments[1];
                    if (side >= 0 && side < ForgeDirection.values().length && value >= 0 && value < ForgeDirection.values().length)
                    {
                        insertDirection[(byte)side] = (byte)value;
                        return new Boolean[]{true};
                    }
                }
                return new Boolean[]{false};
            case 8:
                if (arguments.length > 0 && arguments[0] instanceof Double)
                {
                    double arg = (Double)arguments[0];
                    if (arg >= 0 && arg < filter.getSize())
                    {
                        return new Boolean[]{filter.getValue((int)arg)};
                    }
                    return null;
                }
            case 9:
                if (arguments.length > 1 && arguments[0] instanceof Double && arguments[1] instanceof Boolean)
                {
                    double arg = (Double)arguments[0];
                    if (arg >= 0 && arg < filter.getSize())
                    {
                        filter.setValue((int)arg, (Boolean)arguments[1]);
                        return new Boolean[]{true};
                    }
                }
                return new Boolean[]{false};
        }
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        spreadItems = compound.getBoolean("spreadItems");
        insertDirection = compound.getByteArray("insertDirection");
        blacklist = compound.getBoolean("blacklist");
        filter.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("spreadItems", spreadItems);
        compound.setByteArray("insertDirection", insertDirection);
        compound.setBoolean("blacklist", blacklist);
        filter.writeToNBT(compound);
    }
}
