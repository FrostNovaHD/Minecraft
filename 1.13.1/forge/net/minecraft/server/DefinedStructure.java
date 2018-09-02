package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class DefinedStructure {
    private final List<List<DefinedStructure.BlockInfo>> a = Lists.newArrayList();
    private final List<DefinedStructure.EntityInfo> b = Lists.newArrayList();
    private BlockPosition c = BlockPosition.ZERO;
    private String d = "?";

    public DefinedStructure() {
    }

    public BlockPosition a() {
        return this.c;
    }

    public void a(String s) {
        this.d = s;
    }

    public String b() {
        return this.d;
    }

    public void a(World world, BlockPosition blockposition, BlockPosition blockposition1, boolean flag, @Nullable Block block) {
        if (blockposition1.getX() >= 1 && blockposition1.getY() >= 1 && blockposition1.getZ() >= 1) {
            BlockPosition blockposition2 = blockposition.a(blockposition1).a(-1, -1, -1);
            ArrayList arraylist = Lists.newArrayList();
            ArrayList arraylist1 = Lists.newArrayList();
            ArrayList arraylist2 = Lists.newArrayList();
            BlockPosition blockposition3 = new BlockPosition(Math.min(blockposition.getX(), blockposition2.getX()), Math.min(blockposition.getY(), blockposition2.getY()), Math.min(blockposition.getZ(), blockposition2.getZ()));
            BlockPosition blockposition4 = new BlockPosition(Math.max(blockposition.getX(), blockposition2.getX()), Math.max(blockposition.getY(), blockposition2.getY()), Math.max(blockposition.getZ(), blockposition2.getZ()));
            this.c = blockposition1;

            for(BlockPosition.MutableBlockPosition blockposition$mutableblockposition : BlockPosition.b(blockposition3, blockposition4)) {
                BlockPosition blockposition5 = blockposition$mutableblockposition.b(blockposition3);
                IBlockData iblockdata = world.getType(blockposition$mutableblockposition);
                if (block == null || block != iblockdata.getBlock()) {
                    TileEntity tileentity = world.getTileEntity(blockposition$mutableblockposition);
                    if (tileentity != null) {
                        NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());
                        nbttagcompound.remove("x");
                        nbttagcompound.remove("y");
                        nbttagcompound.remove("z");
                        arraylist1.add(new DefinedStructure.BlockInfo(blockposition5, iblockdata, nbttagcompound));
                    } else if (!iblockdata.f(world, blockposition$mutableblockposition) && !iblockdata.g()) {
                        arraylist2.add(new DefinedStructure.BlockInfo(blockposition5, iblockdata, (NBTTagCompound)null));
                    } else {
                        arraylist.add(new DefinedStructure.BlockInfo(blockposition5, iblockdata, (NBTTagCompound)null));
                    }
                }
            }

            ArrayList arraylist3 = Lists.newArrayList();
            arraylist3.addAll(arraylist);
            arraylist3.addAll(arraylist1);
            arraylist3.addAll(arraylist2);
            this.a.clear();
            this.a.add(arraylist3);
            if (flag) {
                this.a(world, blockposition3, blockposition4.a(1, 1, 1));
            } else {
                this.b.clear();
            }

        }
    }

    private void a(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        List list = world.a(Entity.class, new AxisAlignedBB(blockposition, blockposition1), (entity1) -> {
            return !(entity1 instanceof EntityHuman);
        });
        this.b.clear();

        for(Entity entity : list) {
            Vec3D vec3d = new Vec3D(entity.locX - (double)blockposition.getX(), entity.locY - (double)blockposition.getY(), entity.locZ - (double)blockposition.getZ());
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            entity.d(nbttagcompound);
            BlockPosition blockposition2;
            if (entity instanceof EntityPainting) {
                blockposition2 = ((EntityPainting)entity).getBlockPosition().b(blockposition);
            } else {
                blockposition2 = new BlockPosition(vec3d);
            }

            this.b.add(new DefinedStructure.EntityInfo(vec3d, blockposition2, nbttagcompound));
        }

    }

    public Map<BlockPosition, String> a(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        HashMap hashmap = Maps.newHashMap();
        StructureBoundingBox structureboundingbox = definedstructureinfo.j();

        for(DefinedStructure.BlockInfo definedstructure$blockinfo : definedstructureinfo.a(this.a, blockposition)) {
            BlockPosition blockposition1 = a(definedstructureinfo, definedstructure$blockinfo.a).a(blockposition);
            if (structureboundingbox == null || structureboundingbox.b(blockposition1)) {
                IBlockData iblockdata = definedstructure$blockinfo.b;
                if (iblockdata.getBlock() == Blocks.STRUCTURE_BLOCK && definedstructure$blockinfo.c != null) {
                    BlockPropertyStructureMode blockpropertystructuremode = BlockPropertyStructureMode.valueOf(definedstructure$blockinfo.c.getString("mode"));
                    if (blockpropertystructuremode == BlockPropertyStructureMode.DATA) {
                        hashmap.put(blockposition1, definedstructure$blockinfo.c.getString("metadata"));
                    }
                }
            }
        }

        return hashmap;
    }

    public BlockPosition a(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo1, BlockPosition blockposition1) {
        BlockPosition blockposition2 = a(definedstructureinfo, blockposition);
        BlockPosition blockposition3 = a(definedstructureinfo1, blockposition1);
        return blockposition2.b(blockposition3);
    }

    public static BlockPosition a(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        return a(blockposition, definedstructureinfo.b(), definedstructureinfo.c(), definedstructureinfo.d());
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        definedstructureinfo.l();
        this.b(generatoraccess, blockposition, definedstructureinfo);
    }

    public void b(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        this.a(generatoraccess, blockposition, new DefinedStructureProcessorRotation(blockposition, definedstructureinfo), definedstructureinfo, 2);
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, int i) {
        return this.a(generatoraccess, blockposition, new DefinedStructureProcessorRotation(blockposition, definedstructureinfo), definedstructureinfo, i);
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, @Nullable DefinedStructureProcessor definedstructureprocessor, DefinedStructureInfo definedstructureinfo, int i) {
        if (this.a.isEmpty()) {
            return false;
        } else {
            List list = definedstructureinfo.a(this.a, blockposition);
            if ((!list.isEmpty() || !definedstructureinfo.h() && !this.b.isEmpty()) && this.c.getX() >= 1 && this.c.getY() >= 1 && this.c.getZ() >= 1) {
                Block block = definedstructureinfo.i();
                StructureBoundingBox structureboundingbox = definedstructureinfo.j();
                ArrayList arraylist = Lists.newArrayListWithCapacity(definedstructureinfo.m() ? list.size() : 0);
                ArrayList arraylist1 = Lists.newArrayListWithCapacity(list.size());
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MAX_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;
                int k1 = Integer.MIN_VALUE;

                for(DefinedStructure.BlockInfo definedstructure$blockinfo : list) {
                    BlockPosition blockposition1 = a(definedstructureinfo, definedstructure$blockinfo.a).a(blockposition);
                    DefinedStructure.BlockInfo definedstructure$blockinfo1 = definedstructureprocessor != null ? definedstructureprocessor.a(generatoraccess, blockposition1, definedstructure$blockinfo) : definedstructure$blockinfo;
                    if (definedstructure$blockinfo1 != null) {
                        Block block1 = definedstructure$blockinfo1.b.getBlock();
                        if ((block == null || block != block1) && (!definedstructureinfo.k() || block1 != Blocks.STRUCTURE_BLOCK) && (structureboundingbox == null || structureboundingbox.b(blockposition1))) {
                            Fluid fluid = definedstructureinfo.m() ? generatoraccess.b(blockposition1) : null;
                            IBlockData iblockdata = definedstructure$blockinfo1.b.a(definedstructureinfo.b());
                            IBlockData iblockdata1 = iblockdata.a(definedstructureinfo.c());
                            if (definedstructure$blockinfo1.c != null) {
                                TileEntity tileentity = generatoraccess.getTileEntity(blockposition1);
                                if (tileentity instanceof IInventory) {
                                    ((IInventory)tileentity).clear();
                                }

                                generatoraccess.setTypeAndData(blockposition1, Blocks.BARRIER.getBlockData(), 4);
                            }

                            if (generatoraccess.setTypeAndData(blockposition1, iblockdata1, i)) {
                                j = Math.min(j, blockposition1.getX());
                                k = Math.min(k, blockposition1.getY());
                                l = Math.min(l, blockposition1.getZ());
                                i1 = Math.max(i1, blockposition1.getX());
                                j1 = Math.max(j1, blockposition1.getY());
                                k1 = Math.max(k1, blockposition1.getZ());
                                arraylist1.add(Pair.of(blockposition1, definedstructure$blockinfo.c));
                                if (definedstructure$blockinfo1.c != null) {
                                    TileEntity tileentity2 = generatoraccess.getTileEntity(blockposition1);
                                    if (tileentity2 != null) {
                                        definedstructure$blockinfo1.c.setInt("x", blockposition1.getX());
                                        definedstructure$blockinfo1.c.setInt("y", blockposition1.getY());
                                        definedstructure$blockinfo1.c.setInt("z", blockposition1.getZ());
                                        tileentity2.load(definedstructure$blockinfo1.c);
                                        tileentity2.a(definedstructureinfo.b());
                                        tileentity2.a(definedstructureinfo.c());
                                    }
                                }

                                if (fluid != null && iblockdata1.getBlock() instanceof IFluidContainer) {
                                    ((IFluidContainer)iblockdata1.getBlock()).place(generatoraccess, blockposition1, iblockdata1, fluid);
                                    if (!fluid.d()) {
                                        arraylist.add(blockposition1);
                                    }
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

                while(flag && !arraylist.isEmpty()) {
                    flag = false;
                    Iterator iterator = arraylist.iterator();

                    while(iterator.hasNext()) {
                        BlockPosition blockposition2 = (BlockPosition)iterator.next();
                        Fluid fluid1 = generatoraccess.b(blockposition2);

                        for(int j2 = 0; j2 < aenumdirection.length && !fluid1.d(); ++j2) {
                            Fluid fluid2 = generatoraccess.b(blockposition2.shift(aenumdirection[j2]));
                            if (fluid2.f() > fluid1.f() || fluid2.d() && !fluid1.d()) {
                                fluid1 = fluid2;
                            }
                        }

                        if (fluid1.d()) {
                            IBlockData iblockdata4 = generatoraccess.getType(blockposition2);
                            if (iblockdata4.getBlock() instanceof IFluidContainer) {
                                ((IFluidContainer)iblockdata4.getBlock()).place(generatoraccess, blockposition2, iblockdata4, fluid1);
                                flag = true;
                                iterator.remove();
                            }
                        }
                    }
                }

                if (j <= i1) {
                    VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(i1 - j + 1, j1 - k + 1, k1 - l + 1);
                    int l1 = j;
                    int i2 = k;
                    int k2 = l;

                    for(Pair pair : arraylist1) {
                        BlockPosition blockposition3 = (BlockPosition)pair.getFirst();
                        voxelshapebitset.a(blockposition3.getX() - l1, blockposition3.getY() - i2, blockposition3.getZ() - k2, true, true);
                    }

                    voxelshapebitset.a((enumdirection, l3, i4, j4) -> {
                        BlockPosition blockposition5 = new BlockPosition(l1 + l3, i2 + i4, k2 + j4);
                        BlockPosition blockposition6 = blockposition5.shift(enumdirection);
                        IBlockData iblockdata5 = generatoraccess.getType(blockposition5);
                        IBlockData iblockdata6 = generatoraccess.getType(blockposition6);
                        IBlockData iblockdata7 = iblockdata5.updateState(enumdirection, iblockdata6, generatoraccess, blockposition5, blockposition6);
                        if (iblockdata5 != iblockdata7) {
                            generatoraccess.setTypeAndData(blockposition5, iblockdata7, i & -2 | 16);
                        }

                        IBlockData iblockdata8 = iblockdata6.updateState(enumdirection.opposite(), iblockdata7, generatoraccess, blockposition6, blockposition5);
                        if (iblockdata6 != iblockdata8) {
                            generatoraccess.setTypeAndData(blockposition6, iblockdata8, i & -2 | 16);
                        }

                    });

                    for(Pair pair1 : arraylist1) {
                        BlockPosition blockposition4 = (BlockPosition)pair1.getFirst();
                        IBlockData iblockdata2 = generatoraccess.getType(blockposition4);
                        IBlockData iblockdata3 = Block.b(iblockdata2, generatoraccess, blockposition4);
                        if (iblockdata2 != iblockdata3) {
                            generatoraccess.setTypeAndData(blockposition4, iblockdata3, i & -2 | 16);
                        }

                        generatoraccess.update(blockposition4, iblockdata3.getBlock());
                        if (pair1.getSecond() != null) {
                            TileEntity tileentity1 = generatoraccess.getTileEntity(blockposition4);
                            if (tileentity1 != null) {
                                tileentity1.update();
                            }
                        }
                    }
                }

                if (!definedstructureinfo.h()) {
                    this.a(generatoraccess, blockposition, definedstructureinfo.b(), definedstructureinfo.c(), definedstructureinfo.d(), structureboundingbox);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1, @Nullable StructureBoundingBox structureboundingbox) {
        for(DefinedStructure.EntityInfo definedstructure$entityinfo : this.b) {
            BlockPosition blockposition2 = a(definedstructure$entityinfo.b, enumblockmirror, enumblockrotation, blockposition1).a(blockposition);
            if (structureboundingbox == null || structureboundingbox.b(blockposition2)) {
                NBTTagCompound nbttagcompound = definedstructure$entityinfo.c;
                Vec3D vec3d = a(definedstructure$entityinfo.a, enumblockmirror, enumblockrotation, blockposition1);
                Vec3D vec3d1 = vec3d.add((double)blockposition.getX(), (double)blockposition.getY(), (double)blockposition.getZ());
                NBTTagList nbttaglist = new NBTTagList();
                nbttaglist.add((NBTBase)(new NBTTagDouble(vec3d1.x)));
                nbttaglist.add((NBTBase)(new NBTTagDouble(vec3d1.y)));
                nbttaglist.add((NBTBase)(new NBTTagDouble(vec3d1.z)));
                nbttagcompound.set("Pos", nbttaglist);
                nbttagcompound.a("UUID", UUID.randomUUID());

                Entity entity;
                try {
                    entity = EntityTypes.a(nbttagcompound, generatoraccess.getMinecraftWorld());
                } catch (Exception var16) {
                    entity = null;
                }

                if (entity != null) {
                    float f = entity.a(enumblockmirror);
                    f = f + (entity.yaw - entity.a(enumblockrotation));
                    entity.setPositionRotation(vec3d1.x, vec3d1.y, vec3d1.z, f, entity.pitch);
                    generatoraccess.addEntity(entity);
                }
            }
        }

    }

    public BlockPosition a(EnumBlockRotation enumblockrotation) {
        switch(enumblockrotation) {
        case COUNTERCLOCKWISE_90:
        case CLOCKWISE_90:
            return new BlockPosition(this.c.getZ(), this.c.getY(), this.c.getX());
        default:
            return this.c;
        }
    }

    public static BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        boolean flag = true;
        switch(enumblockmirror) {
        case LEFT_RIGHT:
            k = -k;
            break;
        case FRONT_BACK:
            i = -i;
            break;
        default:
            flag = false;
        }

        int l = blockposition1.getX();
        int i1 = blockposition1.getZ();
        switch(enumblockrotation) {
        case COUNTERCLOCKWISE_90:
            return new BlockPosition(l - i1 + k, j, l + i1 - i);
        case CLOCKWISE_90:
            return new BlockPosition(l + i1 - k, j, i1 - l + i);
        case CLOCKWISE_180:
            return new BlockPosition(l + l - i, j, i1 + i1 - k);
        default:
            return flag ? new BlockPosition(i, j, k) : blockposition;
        }
    }

    private static Vec3D a(Vec3D vec3d, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;
        boolean flag = true;
        switch(enumblockmirror) {
        case LEFT_RIGHT:
            d2 = 1.0D - d2;
            break;
        case FRONT_BACK:
            d0 = 1.0D - d0;
            break;
        default:
            flag = false;
        }

        int i = blockposition.getX();
        int j = blockposition.getZ();
        switch(enumblockrotation) {
        case COUNTERCLOCKWISE_90:
            return new Vec3D((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
        case CLOCKWISE_90:
            return new Vec3D((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
        case CLOCKWISE_180:
            return new Vec3D((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
        default:
            return flag ? new Vec3D(d0, d1, d2) : vec3d;
        }
    }

    public BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation) {
        return a(blockposition, enumblockmirror, enumblockrotation, this.a().getX(), this.a().getZ());
    }

    public static BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, int i, int j) {
        --i;
        --j;
        int k = enumblockmirror == EnumBlockMirror.FRONT_BACK ? i : 0;
        int l = enumblockmirror == EnumBlockMirror.LEFT_RIGHT ? j : 0;
        BlockPosition blockposition1 = blockposition;
        switch(enumblockrotation) {
        case COUNTERCLOCKWISE_90:
            blockposition1 = blockposition.a(l, 0, i - k);
            break;
        case CLOCKWISE_90:
            blockposition1 = blockposition.a(j - l, 0, k);
            break;
        case CLOCKWISE_180:
            blockposition1 = blockposition.a(i - k, 0, j - l);
            break;
        case NONE:
            blockposition1 = blockposition.a(k, 0, l);
        }

        return blockposition1;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (this.a.isEmpty()) {
            nbttagcompound.set("blocks", new NBTTagList());
            nbttagcompound.set("palette", new NBTTagList());
        } else {
            ArrayList arraylist = Lists.newArrayList();
            DefinedStructure.a definedstructure$a = new DefinedStructure.a();
            arraylist.add(definedstructure$a);

            for(int i = 1; i < this.a.size(); ++i) {
                arraylist.add(new DefinedStructure.a());
            }

            NBTTagList nbttaglist1 = new NBTTagList();
            List list = (List)this.a.get(0);

            for(int j = 0; j < list.size(); ++j) {
                DefinedStructure.BlockInfo definedstructure$blockinfo = (DefinedStructure.BlockInfo)list.get(j);
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.set("pos", this.a(definedstructure$blockinfo.a.getX(), definedstructure$blockinfo.a.getY(), definedstructure$blockinfo.a.getZ()));
                int k = definedstructure$a.a(definedstructure$blockinfo.b);
                nbttagcompound1.setInt("state", k);
                if (definedstructure$blockinfo.c != null) {
                    nbttagcompound1.set("nbt", definedstructure$blockinfo.c);
                }

                nbttaglist1.add((NBTBase)nbttagcompound1);

                for(int l = 1; l < this.a.size(); ++l) {
                    DefinedStructure.a definedstructure$a1 = (DefinedStructure.a)arraylist.get(l);
                    definedstructure$a1.a(((DefinedStructure.BlockInfo)((List)this.a.get(j)).get(j)).b, k);
                }
            }

            nbttagcompound.set("blocks", nbttaglist1);
            if (arraylist.size() == 1) {
                NBTTagList nbttaglist2 = new NBTTagList();

                for(IBlockData iblockdata : definedstructure$a) {
                    nbttaglist2.add((NBTBase)GameProfileSerializer.a(iblockdata));
                }

                nbttagcompound.set("palette", nbttaglist2);
            } else {
                NBTTagList nbttaglist3 = new NBTTagList();

                for(DefinedStructure.a definedstructure$a2 : arraylist) {
                    NBTTagList nbttaglist4 = new NBTTagList();

                    for(IBlockData iblockdata1 : definedstructure$a2) {
                        nbttaglist4.add((NBTBase)GameProfileSerializer.a(iblockdata1));
                    }

                    nbttaglist3.add((NBTBase)nbttaglist4);
                }

                nbttagcompound.set("palettes", nbttaglist3);
            }
        }

        NBTTagList nbttaglist = new NBTTagList();

        for(DefinedStructure.EntityInfo definedstructure$entityinfo : this.b) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.set("pos", this.a(definedstructure$entityinfo.a.x, definedstructure$entityinfo.a.y, definedstructure$entityinfo.a.z));
            nbttagcompound2.set("blockPos", this.a(definedstructure$entityinfo.b.getX(), definedstructure$entityinfo.b.getY(), definedstructure$entityinfo.b.getZ()));
            if (definedstructure$entityinfo.c != null) {
                nbttagcompound2.set("nbt", definedstructure$entityinfo.c);
            }

            nbttaglist.add((NBTBase)nbttagcompound2);
        }

        nbttagcompound.set("entities", nbttaglist);
        nbttagcompound.set("size", this.a(this.c.getX(), this.c.getY(), this.c.getZ()));
        nbttagcompound.setInt("DataVersion", 1628);
        return nbttagcompound;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.a.clear();
        this.b.clear();
        NBTTagList nbttaglist = nbttagcompound.getList("size", 3);
        this.c = new BlockPosition(nbttaglist.h(0), nbttaglist.h(1), nbttaglist.h(2));
        NBTTagList nbttaglist1 = nbttagcompound.getList("blocks", 10);
        if (nbttagcompound.hasKeyOfType("palettes", 9)) {
            NBTTagList nbttaglist2 = nbttagcompound.getList("palettes", 9);

            for(int i = 0; i < nbttaglist2.size(); ++i) {
                this.a(nbttaglist2.f(i), nbttaglist1);
            }
        } else {
            this.a(nbttagcompound.getList("palette", 10), nbttaglist1);
        }

        NBTTagList nbttaglist5 = nbttagcompound.getList("entities", 10);

        for(int j = 0; j < nbttaglist5.size(); ++j) {
            NBTTagCompound nbttagcompound1 = nbttaglist5.getCompound(j);
            NBTTagList nbttaglist3 = nbttagcompound1.getList("pos", 6);
            Vec3D vec3d = new Vec3D(nbttaglist3.k(0), nbttaglist3.k(1), nbttaglist3.k(2));
            NBTTagList nbttaglist4 = nbttagcompound1.getList("blockPos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist4.h(0), nbttaglist4.h(1), nbttaglist4.h(2));
            if (nbttagcompound1.hasKey("nbt")) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("nbt");
                this.b.add(new DefinedStructure.EntityInfo(vec3d, blockposition, nbttagcompound2));
            }
        }

    }

    private void a(NBTTagList nbttaglist, NBTTagList nbttaglist1) {
        DefinedStructure.a definedstructure$a = new DefinedStructure.a();
        ArrayList arraylist = Lists.newArrayList();

        for(int i = 0; i < nbttaglist.size(); ++i) {
            definedstructure$a.a(GameProfileSerializer.d(nbttaglist.getCompound(i)), i);
        }

        for(int j = 0; j < nbttaglist1.size(); ++j) {
            NBTTagCompound nbttagcompound = nbttaglist1.getCompound(j);
            NBTTagList nbttaglist2 = nbttagcompound.getList("pos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist2.h(0), nbttaglist2.h(1), nbttaglist2.h(2));
            IBlockData iblockdata = definedstructure$a.a(nbttagcompound.getInt("state"));
            NBTTagCompound nbttagcompound1;
            if (nbttagcompound.hasKey("nbt")) {
                nbttagcompound1 = nbttagcompound.getCompound("nbt");
            } else {
                nbttagcompound1 = null;
            }

            arraylist.add(new DefinedStructure.BlockInfo(blockposition, iblockdata, nbttagcompound1));
        }

        this.a.add(arraylist);
    }

    private NBTTagList a(int... aint) {
        NBTTagList nbttaglist = new NBTTagList();

        for(int i : aint) {
            nbttaglist.add((NBTBase)(new NBTTagInt(i)));
        }

        return nbttaglist;
    }

    private NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();

        for(double d0 : adouble) {
            nbttaglist.add((NBTBase)(new NBTTagDouble(d0)));
        }

        return nbttaglist;
    }

    public static class BlockInfo {
        public final BlockPosition a;
        public final IBlockData b;
        public final NBTTagCompound c;

        public BlockInfo(BlockPosition blockposition, IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
            this.a = blockposition;
            this.b = iblockdata;
            this.c = nbttagcompound;
        }
    }

    public static class EntityInfo {
        public final Vec3D a;
        public final BlockPosition b;
        public final NBTTagCompound c;

        public EntityInfo(Vec3D vec3d, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
            this.a = vec3d;
            this.b = blockposition;
            this.c = nbttagcompound;
        }
    }

    static class a implements Iterable<IBlockData> {
        public static final IBlockData a = Blocks.AIR.getBlockData();
        private final RegistryBlockID<IBlockData> b;
        private int c;

        private a() {
            this.b = new RegistryBlockID<IBlockData>(16);
        }

        public int a(IBlockData iblockdata) {
            int i = this.b.getId(iblockdata);
            if (i == -1) {
                i = this.c++;
                this.b.a(iblockdata, i);
            }

            return i;
        }

        @Nullable
        public IBlockData a(int i) {
            IBlockData iblockdata = this.b.fromId(i);
            return iblockdata == null ? a : iblockdata;
        }

        public Iterator<IBlockData> iterator() {
            return this.b.iterator();
        }

        public void a(IBlockData iblockdata, int i) {
            this.b.a(iblockdata, i);
        }
    }
}