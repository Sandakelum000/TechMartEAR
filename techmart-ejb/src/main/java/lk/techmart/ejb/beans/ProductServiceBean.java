package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lk.techmart.core.dto.*;
import lk.techmart.core.entity.*;
import lk.techmart.core.mapper.AttributeMapper;
import lk.techmart.core.service.ProductService;
import lk.techmart.core.util.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProductServiceBean implements ProductService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<ProductResponseDTO> getAllProductAttribute() {

        try {
            //load brands
            List<Brand> brands = entityManager.createQuery("SELECT b FROM Brand b", Brand.class).getResultList();
            List<BrandDTO> brandDTOList = AttributeMapper.brandToDTO(brands);

            //load Categories
            List<Category> categories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            List<CategoryDTO> categoryDTOList = AttributeMapper.categoryToDTO(categories);

            //load warehouses
            List<Warehouse> warehouses = entityManager.createQuery("SELECT w FROM Warehouse w", Warehouse.class).getResultList();
            List<WarehouseDTO> warehouseDTOList = AttributeMapper.warehouseToDTO(warehouses);

            return ServiceResponse.<ProductResponseDTO>builder()
                    .success(true)
                    .message("attributes loading successful.")
                    .data(
                            ProductResponseDTO.builder()
                                    .brandList(brandDTOList)
                                    .categoryList(categoryDTOList)
                                    .warehouseList(warehouseDTOList)
                                    .build()
                    ).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceResponse.<ProductResponseDTO>builder()
                .success(false)
                .message("Data retrieving failed due to System error")
                .data(null)
                .build();
    }

    @Override
    public ServiceResponse<Integer> addNewProduct(ProductDTO productDTO) {
        try {
            if (productDTO.getBrandId() <= 0) {
                return ServiceResponse.<Integer>builder().success(false).message("Invalid Brand Type").build();
            }
            if (productDTO.getCategoryId() <= 0) {
                return ServiceResponse.<Integer>builder().success(false).message("Invalid Category Type").build();
            }
            if (productDTO.getTitle() == null || productDTO.getTitle().isBlank()){
                return ServiceResponse.<Integer>builder().success(false).message("Product title can't be empty").build();
            }
            if(productDTO.getDescription() == null || productDTO.getDescription().isBlank()){
                return ServiceResponse.<Integer>builder().success(false).message("Product description can't be empty").build();
            }
            if(productDTO.getPrice() <= 0){
                return ServiceResponse.<Integer>builder().success(false).message("Invalid Product price").build();
            }
            if(productDTO.getQty() <= 0){
                return ServiceResponse.<Integer>builder().success(false).message("Invalid Product quantity").build();
            }
            if(productDTO.getWarehouseId() <= 0){
                return ServiceResponse.<Integer>builder().success(false).message("Invalid product warehouse").build();
            }

            Brand brand = entityManager.find(Brand.class, productDTO.getBrandId());
            if(brand == null){
                return ServiceResponse.<Integer>builder().success(false).message("Brand not found").build();
            }
            Category category = entityManager.find(Category.class, productDTO.getCategoryId());
            if(category ==  null){
                return ServiceResponse.<Integer>builder().success(false).message("Category not found").build();
            }
            Warehouse warehouse = entityManager.find(Warehouse.class, productDTO.getWarehouseId());
            if(warehouse == null){
                return ServiceResponse.<Integer>builder().success(false).message("Warehouse not found").build();
            }

            Product existing = entityManager.createQuery(
                            "FROM Product p " +
                                    "WHERE LOWER(TRIM(p.title)) = LOWER(:title) " +
                                    "AND p.brand.id = :brandId " +
                                    "AND p.category.id = :categoryId",
                            Product.class)
                    .setParameter("title", productDTO.getTitle().trim())
                    .setParameter("brandId", productDTO.getBrandId())
                    .setParameter("categoryId", productDTO.getCategoryId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if(existing != null){
                return ServiceResponse.<Integer>builder().success(false).message("Product already exists").build();
            }

            //add product
            Product product = Product.builder()
                    .title(productDTO.getTitle().trim())
                    .brand(brand)
                    .category(category)
                    .description(productDTO.getDescription()).build();

            entityManager.persist(product);
            entityManager.flush();

            Integer productId = product.getId();

            //add stock
            Stock stock = Stock.builder()
                    .product(product)
                    .price(productDTO.getPrice())
                    .warehouse(warehouse)
                    .qty(productDTO.getQty())
                    .status(true).build();
            entityManager.persist(stock);
            entityManager.flush();

            //add stock reference
            InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                    .stock(stock)
                    .qty(productDTO.getQty())
                    .type(InventoryTransaction.InventoryType.RECEIVE)
                    .reference("Initial Stock")
                    .build();
            entityManager.persist(inventoryTransaction);

            return ServiceResponse.<Integer>builder().success(true).message("Product added successfully").data(productId).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<Integer>builder().success(false).message("Product adding failed. Please try again later.").build();
        }
    }

    @Override
    public boolean isProductExists(int productId) {
        return entityManager.find(Product.class, productId) != null;
    }

    @Override
    public ServiceResponse<Void> addProductImages(int productId, List<String> urls) {
        Product product = entityManager.find(Product.class, productId);
        if(product == null){
            return ServiceResponse.<Void>builder()
                    .success(false)
                    .message("Product not found")
                    .build();
        }

        if(product.getImages() == null){
            product.setImages(new ArrayList<>());
        }
        product.getImages().addAll(urls); //managed entity ekak nisa re merging one na

        return ServiceResponse.<Void>builder()
                .success(true)
                .message("Product images uploaded successfully.")
                .build();
    }


}
