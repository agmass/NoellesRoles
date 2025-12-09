package org.agmas.noellesroles;

import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.index.tag.TMMItemTags;
import dev.doctor4t.trainmurdermystery.item.RevolverItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.item.FakeKnifeItem;

public class ModItems {
    public static void init() {
        GameConstants.ITEM_COOLDOWNS.put(FAKE_REVOLVER, 10);
    }

    public static final Item FAKE_KNIFE = register(
            new FakeKnifeItem(new Item.Settings().maxCount(1)),
            "fake_knife"
    );
    public static final Item FAKE_REVOLVER = register(
            new RevolverItem(new Item.Settings().maxCount(1)),
            "fake_revolver"
    );
    public static final Item MASTER_KEY = register(
            new Item(new Item.Settings().maxCount(1)),
            "master_key"
    );
    public static final Item DELUSION_VIAL = register(
            new Item(new Item.Settings().maxCount(1)),
            "delusion_vial"
    );
    public static final Item DEFENSE_VIAL = register(
            new Item(new Item.Settings().maxCount(1)),
            "defense_vial"
    );
    public static final Item ROLE_MINE = register(
            new Item(new Item.Settings().maxCount(1)),
            "role_mine"
    );
    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(Noellesroles.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

}
