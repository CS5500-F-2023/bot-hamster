package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Builder
@Data
@AllArgsConstructor
public class InventoryItem {
    ObjectId objectId;

    @Nonnull final InventoryItemType inventoryItemType;

    int quantity;
}
