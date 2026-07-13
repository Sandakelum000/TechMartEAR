package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.InventoryTransactionEvent;
import lk.techmart.core.dto.StockDataDTO;
import lk.techmart.core.util.ServiceResponse;

import java.util.List;

@Remote
public interface InventoryService {
    ServiceResponse<Void> receiveStock(InventoryTransactionEvent event);
    void processInventoryTransaction(InventoryTransactionEvent event);
    ServiceResponse<List<StockDataDTO>> getInventoryData(String sort, String order);
    ServiceResponse<StockDataDTO> getInventoryDataById(int id);
}