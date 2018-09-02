package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class BlockStatePredicate implements Predicate<IBlockData> {
    public static final Predicate<IBlockData> a = (var0) -> {
        return true;
    };
    private final BlockStateList<Block, IBlockData> b;
    private final Map<IBlockState<?>, Predicate<Object>> c = Maps.newHashMap();

    private BlockStatePredicate(BlockStateList<Block, IBlockData> blockstatelist) {
        this.b = blockstatelist;
    }

    public static BlockStatePredicate a(Block block) {
        return new BlockStatePredicate(block.getStates());
    }

    public boolean a(@Nullable IBlockData iblockdata) {
        if (iblockdata != null && iblockdata.getBlock().equals(this.b.getBlock())) {
            if (this.c.isEmpty()) {
                return true;
            } else {
                for(Entry entry : this.c.entrySet()) {
                    if (!this.a(iblockdata, (IBlockState)entry.getKey(), (Predicate)entry.getValue())) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    protected <T extends Comparable<T>> boolean a(IBlockData iblockdata, IBlockState<T> iblockstate, Predicate<Object> predicate) {
        Comparable comparable = iblockdata.get(iblockstate);
        return predicate.test(comparable);
    }

    public <V extends Comparable<V>> BlockStatePredicate a(IBlockState<V> iblockstate, Predicate<Object> predicate) {
        if (!this.b.d().contains(iblockstate)) {
            throw new IllegalArgumentException(this.b + " cannot support property " + iblockstate);
        } else {
            this.c.put(iblockstate, predicate);
            return this;
        }
    }

    // $FF: synthetic method
    public boolean test(@Nullable Object object) {
        return this.a((IBlockData)object);
    }
}