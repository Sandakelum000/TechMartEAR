package lk.techmart.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InventorySyncService {
    private final Map<Integer,Integer> inventoryCache ;
    public InventorySyncService(){
        inventoryCache = new ConcurrentHashMap<>();
    }
    public void updateStock(Integer stockId, Integer qty){
        inventoryCache.put(stockId,qty);
    }
    public Integer getStock(Integer stockId){
        return inventoryCache.get(stockId);
    }
}
