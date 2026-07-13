package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lk.techmart.core.dto.*;
import lk.techmart.core.entity.Brand;
import lk.techmart.core.entity.Category;
import lk.techmart.core.entity.Product;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.mapper.AttributeMapper;
import lk.techmart.core.service.AdvanceSearchService;
import lk.techmart.core.util.AppUtil;
import lk.techmart.core.util.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class AdvanceSearchBean implements AdvanceSearchService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Override
    public ServiceResponse<ProductResponseDTO> getAllProducts() {

        try {
            //loads brand
            List<Brand> brands = entityManager.createQuery("SELECT b FROM Brand b", Brand.class).getResultList();
            List<BrandDTO> brandDTOList = AttributeMapper.brandToDTO(brands);

            //load Categories
            List<Category> categories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            List<CategoryDTO> categoryDTOList = AttributeMapper.categoryToDTO(categories);

            Object[] minMax = (Object[]) entityManager.createQuery(
                    "SELECT MIN(s.price), MAX(s.price) " +
                            "FROM Stock s WHERE s.status = true"
            ).getSingleResult();

            double minPrice = minMax[0] != null ? (Double) minMax[0] : 0.0;
            double maxPrice = minMax[1] != null ? (Double) minMax[1] : 0.0;

            //load stock
            TypedQuery<Stock> stockTypedQuery = entityManager.createQuery(
                    "FROM Stock s JOIN FETCH s.product WHERE s.status = true ORDER BY s.id ASC", Stock.class);

            Long totalProducts = entityManager.createQuery(
                    "SELECT COUNT(s) FROM Stock s WHERE s.status = true", Long.class).getSingleResult();

            stockTypedQuery.setFirstResult(AppUtil.FIRST_RESULT_VALUE);
            stockTypedQuery.setMaxResults(AppUtil.MAX_RESULT_VALUE);

            List<ProductDTO> productDTOList = AttributeMapper.productToDTO(stockTypedQuery.getResultList());

            return ServiceResponse.<ProductResponseDTO>builder()
                    .success(true)
                    .message("successfully loaded")
                    .data(
                            ProductResponseDTO.builder()
                                    .brandList(brandDTOList)
                                    .categoryList(categoryDTOList)
                                    .productList(productDTOList)
                                    .minPrice(minPrice)
                                    .maxPrice(maxPrice)
                                    .maxResult(AppUtil.MAX_RESULT_VALUE)
                                    .allProductCount(totalProducts)
                                    .build()
                    ).build();

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResponse.<ProductResponseDTO>builder()
                    .success(false)
                    .message("Data retrieving failed due to Syatem error")
                    .data(null)
                    .build();
        }

    }

    @Override
    public ServiceResponse<ProductResponseDTO> advanceSearch(SearchRequestDTO request) {
        try {
            if(request == null){
                return ServiceResponse.<ProductResponseDTO>builder().success(false).message("Invalid Request").data(null).build();
            }

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
            Root<Stock> stock = query.from(Stock.class);

            Join<Stock, Product> product = stock.join("product", JoinType.INNER);
            Join<Stock, Product> brand = product.join("brand", JoinType.LEFT);
            Join<Stock, Product> category = product.join("category", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(stock.get("status"), true));

            //search eka
            if(request.getSearchText() != null && !request.getSearchText().isBlank()){
                predicates.add(
                  cb.like(
                          cb.lower(product.get("title")),"%"+request.getSearchText().trim().toLowerCase()+"%"
                  )
                );
            }

            //brand eka
            if (request.getBrandName() != null) {
                predicates.add(cb.equal(brand.get("name"), request.getBrandName()));
            }

            //cat eka
            if (request.getCategoryName() != null) {
                predicates.add(cb.equal(category.get("name"), request.getCategoryName()));
            }

            //price range eka
            if (request.getPriceStart() != null && request.getPriceEnd() != null) {
                predicates.add(cb.between(
                        stock.get("price"), request.getPriceStart(), request.getPriceEnd()
                ));
            }

            query.where(predicates.toArray(new Predicate[0]));

            //sorting part
            if (request.getSortBy() != null) {
                switch (request.getSortBy()) {
                    case "LATEST":
                        query.orderBy(cb.desc(stock.get("createdAt")));
                        break;

                    case "OLDEST":
                        query.orderBy(cb.asc(stock.get("createdAt")));
                        break;

                    case "NAME":
                        query.orderBy(cb.asc(product.get("title")));
                        break;

                    case "PRICE_LOW":
                        query.orderBy(cb.asc(stock.get("price")));
                        break;

                    case "PRICE_HIGH":
                        query.orderBy(cb.desc(stock.get("price")));
                        break;

                    default:
                        query.orderBy(cb.desc(stock.get("id")));
                }
            }

            TypedQuery<Stock> executeQuery = entityManager.createQuery(query);

            //pagination part eka
            executeQuery.setFirstResult(request.getOffset());
            executeQuery.setMaxResults(AppUtil.MAX_RESULT_VALUE);

            List<Stock> stocks = executeQuery.getResultList();
            List<ProductDTO> productDTOList = AttributeMapper.productToDTO(stocks);

            //product count
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Stock> countRoot = countQuery.from(Stock.class);

            Join<Stock, Product> countProduct = countRoot.join("product", JoinType.INNER);
            Join<Product, Brand> countBrand = countProduct.join("brand", JoinType.LEFT);
            Join<Product, Category> countCategory = countProduct.join("category", JoinType.LEFT);

            List<Predicate> countPredicates = new ArrayList<>();
            countPredicates.add(cb.equal(countRoot.get("status"), true));

            if (request.getBrandName() != null) {
                countPredicates.add(cb.equal(countBrand.get("name"), request.getBrandName()));
            }
            if (request.getCategoryName() != null) {
                countPredicates.add(cb.equal(countCategory.get("name"), request.getCategoryName()));
            }
            if (request.getPriceStart() != null && request.getPriceEnd() != null) {
                countPredicates.add(cb.between(
                        countRoot.get("price"),
                        request.getPriceStart(),
                        request.getPriceEnd()
                ));
            }

            countQuery.select(cb.count(countRoot));
            countQuery.where(countPredicates.toArray(new Predicate[0]));

            Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

            return ServiceResponse.<ProductResponseDTO>builder()
                    .success(true)
                    .message("Advanced search success")
                    .data(
                            ProductResponseDTO.builder()
                                    .productList(productDTOList)
                                    .allProductCount(totalCount)
                                    .maxResult(AppUtil.MAX_RESULT_VALUE)
                                    .build()
                    ).build();
        } catch (Exception e) {
           e.printStackTrace();
            return ServiceResponse.<ProductResponseDTO>builder().success(false).message("Advanced search failed").data(null).build();
        }

    }

}
