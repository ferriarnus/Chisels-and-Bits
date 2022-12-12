package mod.chiselsandbits.client.registrars;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import mod.chiselsandbits.client.colors.BitBagItemColor;
import mod.chiselsandbits.client.colors.BitItemItemColor;
import mod.chiselsandbits.client.colors.ChiseledBlockBlockColor;
import mod.chiselsandbits.client.colors.ChiseledBlockItemItemColor;
import mod.chiselsandbits.item.ChiseledBlockItem;
import mod.chiselsandbits.registrars.ModBlocks;
import mod.chiselsandbits.registrars.ModItems;
import net.minecraft.world.level.block.Block;

public final class ItemColors
{

    private ItemColors()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ItemColors. This is a utility class");
    }

    public static void onClientConstruction()
    {
        IColorManager.getInstance().setupItemColors(
          configuration -> {
              configuration
                .register(new ChiseledBlockItemItemColor(), ModItems.MATERIAL_TO_ITEM_CONVERSIONS.values().stream().map(IRegistryObject::get).toArray(ChiseledBlockItem[]::new));
              configuration
                .register(new BitItemItemColor(), ModItems.ITEM_BLOCK_BIT.get());
              configuration
                .register(new BitBagItemColor(), ModItems.ITEM_BIT_BAG_DEFAULT.get(), ModItems.ITEM_BIT_BAG_DYED.get());
          }
        );
    }
}