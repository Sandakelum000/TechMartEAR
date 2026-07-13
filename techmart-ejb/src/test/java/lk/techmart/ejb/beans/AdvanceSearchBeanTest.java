package lk.techmart.ejb.beans;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lk.techmart.core.dto.ProductResponseDTO;
import lk.techmart.core.dto.SearchRequestDTO;
import lk.techmart.core.entity.Brand;
import lk.techmart.core.entity.Category;
import lk.techmart.core.entity.Product;
import lk.techmart.core.entity.Stock;
import lk.techmart.core.util.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvanceSearchBeanTest {

    @InjectMocks
    private AdvanceSearchBean bean;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Brand> brandQuery;

    @Mock
    private TypedQuery<Category> categoryQuery;

    @Mock
    private TypedQuery<Stock> stockQuery;



    private void injectEntityManager() throws Exception {
        Field field = AdvanceSearchBean.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(bean, entityManager);
    }

    @Test
    void shouldReturnAllProductsSuccessfully() throws Exception {

        injectEntityManager();

        when(entityManager.createQuery(anyString(), eq(Brand.class)))
                .thenReturn(brandQuery);
        when(brandQuery.getResultList()).thenReturn(List.of(new Brand()));

        when(entityManager.createQuery(anyString(), eq(Category.class)))
                .thenReturn(categoryQuery);
        when(categoryQuery.getResultList()).thenReturn(List.of(new Category()));


        when(entityManager.createQuery(anyString(), eq(Stock.class)))
                .thenReturn(stockQuery);
        when(stockQuery.setFirstResult(anyInt())).thenReturn(stockQuery);
        when(stockQuery.setMaxResults(anyInt())).thenReturn(stockQuery);

        Product product = new Product();
        product.setId(1);
        product.setTitle("Test Product");

        Brand brand = new Brand();
        brand.setName("Samsung");

        Category category = new Category();
        category.setName("Phone");

        product.setBrand(brand);
        product.setCategory(category);

        Stock stock = new Stock();
        stock.setId(1);
        stock.setPrice(250.0);
        stock.setStatus(true);
        stock.setProduct(product);
        when(stockQuery.getResultList())
                .thenReturn(List.of(stock));

        TypedQuery<Object[]> minMaxQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(
                eq("SELECT MIN(s.price), MAX(s.price) FROM Stock s WHERE s.status = true")
        )).thenReturn(minMaxQuery);

        when(minMaxQuery.getSingleResult())
                .thenReturn(new Object[]{100.0, 500.0});


        TypedQuery<Long> countQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(
                eq("SELECT COUNT(s) FROM Stock s WHERE s.status = true"),
                eq(Long.class)
        )).thenReturn(countQuery);

        when(countQuery.getSingleResult()).thenReturn(10L);

        ServiceResponse<?> response = bean.getAllProducts();

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    void shouldReturnFailureWhenGetAllProductsThrowsException() throws Exception {

        injectEntityManager();

        when(entityManager.createQuery(anyString(), eq(Brand.class)))
                .thenThrow(new RuntimeException("DB fail"));

        ServiceResponse<?> response = bean.getAllProducts();

        assertFalse(response.isSuccess());
        assertEquals("Data retrieving failed due to Syatem error", response.getMessage());
    }

    @Test
    void shouldFailWhenRequestIsNull() throws Exception {

        injectEntityManager();

        ServiceResponse<?> response = bean.advanceSearch(null);

        assertFalse(response.isSuccess());
        assertEquals("Invalid Request", response.getMessage());
    }


}
