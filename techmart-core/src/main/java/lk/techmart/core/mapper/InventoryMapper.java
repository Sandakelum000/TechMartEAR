package lk.techmart.core.mapper;

import lk.techmart.core.dto.InventoryTransactionDTO;
import lk.techmart.core.dto.ProductDTO;
import lk.techmart.core.entity.InventoryTransaction;
import lk.techmart.core.entity.Product;

import java.util.Collections;
import java.util.List;

public class InventoryMapper {
    public static List<InventoryTransactionDTO> inventoryTrToDTO(List<InventoryTransaction> inventoryTransactionList){
        if(inventoryTransactionList == null){
            return Collections.emptyList();
        }
        return inventoryTransactionList.stream()
                .map(it -> InventoryTransactionDTO
                        .builder()
                        .id(it.getId())
                        .type(it.getType())
                        .qty(it.getQty())
                        .reference(it.getReference())
                        .createdAt(it.getCreatedAt())
                        .updatedAt(it.getUpdatedAt())
                        .build())
                .toList();
    }


}
