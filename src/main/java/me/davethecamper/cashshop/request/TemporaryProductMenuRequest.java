package me.davethecamper.cashshop.request;

import lombok.Builder;
import lombok.Data;
import me.davethecamper.cashshop.objects.ProductConfig;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@Builder
public class TemporaryProductMenuRequest {

    @Builder.Default
    private String identifier = UUID.randomUUID().toString();

    private int valor;
    private int delay;

    private ProductConfig product;

    private ItemStack item;

    private boolean allowBonus;

    private boolean money;

}
