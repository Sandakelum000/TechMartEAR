package lk.techmart.ejb.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.*;
import lk.techmart.core.entity.InventoryTransaction;
import lk.techmart.core.entity.Product;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.event.InventoryUpdateEvent;
import lk.techmart.core.service.InventoryService;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.ejb.jms.producer.InventoryEventProducer;
import lk.techmart.ejb.jms.producer.InventoryTopicPublisher;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class InventoryServiceBean implements InventoryService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Inject
    private InventoryTopicPublisher topicPublisher;

    @EJB
    private InventoryEventProducer producer;

    @Inject
    private Event<String> loggerEvent;

    @Override
    public ServiceResponse<Void> receiveStock(InventoryTransactionEvent event) {
        producer.sendInventoryEvent(event);
        return ServiceResponse.<Void>builder()
                .success(true)
                .message("Inventory event queued")
                .build();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processInventoryTransaction(InventoryTransactionEvent event) {
        Stock stock = entityManager.find(Stock.class, event.getStockId());
        if(stock == null) return;

        switch (event.getType()){
            case RECEIVE:
                stock.setQty(stock.getQty()+ event.getQty());
                break;

            case SALE, DAMAGED:
                stock.setQty(stock.getQty() - event.getQty());
                break;

            case ADJUSTMENT:
                stock.setQty(event.getQty());
                break;
        }

        //save transaction record
        InventoryTransaction transaction = InventoryTransaction.builder()
                .stock(stock)
                .type(event.getType())
                .qty(event.getQty())
                .reference(event.getReason()).build();

        if (stock.getInventoryTransactions() == null) {
            stock.setInventoryTransactions(new ArrayList<>());
        }
        stock.getInventoryTransactions().add(transaction);

        entityManager.persist(transaction);
        entityManager.flush();

        loggerEvent.fire("Processing Inventory transaction - StockId:  "+event.getStockId()+" "+event.getType());

        //notify web socket
        topicPublisher.publishInventoryUpdate(
                InventoryUpdateEvent.builder()
                        .stockId(stock.getId())
                        .newQty(stock.getQty())
                        .build());

        loggerEvent.fire("Processing Inventory transaction - notify websocket");
    }

    @Override
    public ServiceResponse<List<StockDataDTO>> getInventoryData(String sort, String order) {
        try{

            String orderBy = "s.qty";

            switch (sort){
                case "price":
                    orderBy = "s.price";
                    break;
                case "id":
                    orderBy = "s.id";
                    break;
                case "createdAt":
                    orderBy = "s.createdAt";
                    break;
                case "updatedAt":
                    orderBy = "s.updatedAt";
                    break;
                case "qty":
                default:
                    orderBy = "s.qty";
            }
            String direction = order.equalsIgnoreCase("desc") ? "DESC" : "ASC";

            List<Stock> stocks = entityManager
                    .createQuery("SELECT DISTINCT s " +
                            "FROM Stock s " +
                            "LEFT JOIN FETCH s.product " +
                            "LEFT JOIN FETCH s.warehouse " +
                            "LEFT JOIN FETCH s.inventoryTransactions " +
                            "ORDER BY "+orderBy+" "+direction, Stock.class).getResultList();


            List<StockDataDTO> stockDTOList = new ArrayList<>();
            for(Stock stock : stocks){
                //product details
                Product product = stock.getProduct();
                ProductDTO productDTO = ProductDTO.builder()
                        .productId(product.getId())
                        .title(product.getTitle())
                        .categoryName(product.getCategory().getName())
                        .brandName(product.getBrand().getName()).build();

                //warehouse
                WarehouseDTO warehouseDTO = WarehouseDTO.builder()
                        .id(stock.getWarehouse().getId())
                        .name(stock.getWarehouse().getName())
                        .location(stock.getWarehouse().getLocation()).build();

                //inventory transaction
                List<InventoryTransactionDTO> transactionDTOList = new ArrayList<>();
                for(InventoryTransaction transaction :  stock.getInventoryTransactions()){
                    transactionDTOList.add(
                            InventoryTransactionDTO.builder()
                                    .id(transaction.getId())
                                    .qty(transaction.getQty())
                                    .type(transaction.getType())
                                    .reference(transaction.getReference())
                                    .createdAt(transaction.getCreatedAt())
                                    .updatedAt(transaction.getUpdatedAt()).build()
                    );
                }
                stockDTOList.add(
                        StockDataDTO.builder()
                                .stockId(stock.getId())
                                .price(stock.getPrice())
                                .qty(stock.getQty())
                                .status(stock.isStatus())
                                .product(productDTO)
                                .warehouse(warehouseDTO)
                                .inventoryTransactionDTOList(transactionDTOList)
                                .build()
                );
            }
            return ServiceResponse.<List<StockDataDTO>>builder()
                    .success(true)
                    .message("Inventory loaded successfully.")
                    .data(stockDTOList)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<List<StockDataDTO>>builder()
                    .success(false)
                    .message("Failed to load inventory.")
                    .build();
        }
    }

    @Override
    public ServiceResponse<StockDataDTO> getInventoryDataById(int id) {
        Stock stock = entityManager.find(Stock.class, id);
        if (stock == null) {
            return ServiceResponse.<StockDataDTO>builder()
                    .success(false)
                    .message("Stock not found: " + id)
                    .build();
        }

        Product product = stock.getProduct();
        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .categoryName(product.getCategory().getName())
                .brandName(product.getBrand().getName()).build();

        WarehouseDTO warehouseDTO = WarehouseDTO.builder()
                .id(stock.getWarehouse().getId())
                .name(stock.getWarehouse().getName())
                .location(stock.getWarehouse().getLocation())
                .build();

        List<InventoryTransactionDTO> inventoryTrDTOList = new ArrayList<>();

        if(stock.getInventoryTransactions() != null){
            for(InventoryTransaction transaction : stock.getInventoryTransactions()){
                inventoryTrDTOList.add(
                  InventoryTransactionDTO.builder()
                          .id(transaction.getId())
                          .qty(transaction.getQty())
                          .type(transaction.getType())
                          .reference(transaction.getReference())
                          .createdAt(transaction.getCreatedAt())
                          .updatedAt(transaction.getUpdatedAt()).build()
                );
            }
        }
        StockDataDTO dto = StockDataDTO.builder()
                .stockId(stock.getId())
                .price(stock.getPrice())
                .qty(stock.getQty())
                .status(stock.isStatus())
                .product(productDTO)
                .warehouse(warehouseDTO)
                .inventoryTransactionDTOList(inventoryTrDTOList)
                .build();

        return ServiceResponse.<StockDataDTO>builder()
                .success(true)
                .data(dto)
                .build();
    }
}
