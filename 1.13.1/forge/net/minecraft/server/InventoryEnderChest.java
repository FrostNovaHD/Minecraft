package net.minecraft.server;

public class InventoryEnderChest extends InventorySubcontainer {
    private TileEntityEnderChest a;

    public InventoryEnderChest() {
        super(new ChatMessage("container.enderchest", new Object[0]), 27);
    }

    public void a(TileEntityEnderChest tileentityenderchest) {
        this.a = tileentityenderchest;
    }

    public void a(NBTTagList nbttaglist) {
        for(int i = 0; i < this.getSize(); ++i) {
            this.setItem(i, ItemStack.a);
        }

        for(int k = 0; k < nbttaglist.size(); ++k) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(k);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 0 && j < this.getSize()) {
                this.setItem(j, ItemStack.a(nbttagcompound));
            }
        }

    }

    public NBTTagList i() {
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < this.getSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.save(nbttagcompound);
                nbttaglist.add((NBTBase)nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.a != null && !this.a.a(entityhuman) ? false : super.a(entityhuman);
    }

    public void startOpen(EntityHuman entityhuman) {
        if (this.a != null) {
            this.a.c();
        }

        super.startOpen(entityhuman);
    }

    public void closeContainer(EntityHuman entityhuman) {
        if (this.a != null) {
            this.a.d();
        }

        super.closeContainer(entityhuman);
        this.a = null;
    }
}