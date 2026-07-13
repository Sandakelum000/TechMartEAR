package lk.techmart.ejb.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.techmart.core.dto.*;
import lk.techmart.core.entity.*;
import lk.techmart.core.service.CheckoutService;
import lk.techmart.core.service.ConfigService;
import lk.techmart.core.service.OrderService;
import lk.techmart.core.util.PayHereUtil;
import lk.techmart.core.util.ServiceResponse;
import lk.techmart.core.util.Validator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Stateless
public class CheckoutServiceBean implements CheckoutService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @EJB
    private OrderService orderService;

    @EJB
    private ConfigService configService;

    private static final String FILE_BASE_URL =
            "http://localhost:8080/techmart/api/files/";

    @Override
    public ServiceResponse<PayHereDTO> processCheckout(CheckoutRequestDTO requestDTO, UserSessionDTO sessionUser) {
        if(sessionUser == null){
            return ServiceResponse.<PayHereDTO>builder().success(false).message("Session expired. Please login again").build();
        }
        if(requestDTO == null){
            return ServiceResponse.<PayHereDTO>builder().success(false).message("Invalid checkout request").build();
        }

        User user = entityManager.find(User.class, sessionUser.getId());

        if(requestDTO.isCurrentAddress()){
            Address address = entityManager.createQuery("FROM Address a WHERE a.user=:user AND a.isPrimary=:primary", Address.class)
                    .setParameter("user", user)
                    .setParameter("primary", requestDTO.isCurrentAddress())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if(address == null){
                return ServiceResponse.<PayHereDTO>builder().success(false).message("Address not found. Please check again").build();
            }

            OrderDTO pendingOrder = orderService.createPendingOrder(sessionUser);

            if(pendingOrder == null){
                return ServiceResponse.<PayHereDTO>builder().success(false).message("Cart is empty").build();
            }

            PayHereDTO paymentDetails = createPaymentDetails(pendingOrder);
            return ServiceResponse.<PayHereDTO>builder().success(true).message("paymentDetails").data(paymentDetails).build();
        }

        // second
        if(requestDTO.getFirstName().isBlank()){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("First Name is required!").build();
        }
        if(requestDTO.getLastName().isBlank()){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Last Name is required!").build();
        }
        if(requestDTO.getLineOne().isBlank()){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Address line one is required!").build();
        }
        if(requestDTO.getPostalCode().isBlank()){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Postal code is required!").build();
        }
        if(!requestDTO.getPostalCode().matches(Validator.POSTAL_CODE_VALIDATION)){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Enter a valid postal code!").build();
        }
        if(requestDTO.getMobile().isBlank()){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Mobile number is required!").build();
        }
        if(!requestDTO.getMobile().matches(Validator.MOBILE_VALIDATION)){
            return ServiceResponse.<PayHereDTO>builder().success(false).data(null).message("Enter a valid mobile number.").build();
        }

        Address existingPrimary = entityManager.createQuery("FROM Address a WHERE a.user=:user AND a.isPrimary=:primary", Address.class)
                .setParameter("user", user)
                .setParameter("primary", true)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if(existingPrimary != null){
            existingPrimary.setPrimary(false);
            entityManager.merge(existingPrimary);
        }

        Address address = new Address();
        address.setPrimary(true);
        address.setLineOne(requestDTO.getLineOne());
        address.setLineTwo(requestDTO.getLineTwo());
        address.setPostalCode(requestDTO.getPostalCode());
        address.setMobile(requestDTO.getMobile());
        address.setUser(user);
        entityManager.persist(address);

        OrderDTO pendingOrder = orderService.createPendingOrder(sessionUser);
        if(pendingOrder == null){
            return ServiceResponse.<PayHereDTO>builder().success(false).message("Cart is empty").build();
        }

        PayHereDTO paymentDetails = createPaymentDetails(pendingOrder);
        return ServiceResponse.<PayHereDTO>builder().success(true).message("paymentDetails").data(paymentDetails).build();

    }

    @Override
    public ServiceResponse<CheckoutDataDTO> getCheckoutData(UserSessionDTO sessionUser) {
        if(sessionUser == null){
            return ServiceResponse.<CheckoutDataDTO>builder().success(false).message("Please login first!").build();
        }
        Address primaryAddress = entityManager.createQuery("FROM Address a WHERE a.user.id=:userId AND a.isPrimary=:primary", Address.class)
                .setParameter("userId", sessionUser.getId())
                .setParameter("primary", true)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if(primaryAddress == null){
            return ServiceResponse.<CheckoutDataDTO>builder().success(false).message("Your primary address is empty. Please check your profile first").build();
        }
        AddressDTO addressDTO = getAddressDTO(primaryAddress);
        List<Cart> cartList = entityManager.createQuery("FROM Cart c WHERE c.user.id=:userId", Cart.class)
                .setParameter("userId", sessionUser.getId())
                .getResultList();

        if(cartList.isEmpty()){
            return ServiceResponse.<CheckoutDataDTO>builder().success(false).message("Your cart is empty. Please add items first!").build();
        }
        List<CartItemDTO> cartItemDTOList = generateCartDTOs(cartList);
        return ServiceResponse.<CheckoutDataDTO>builder()
                .success(true)
                .message("Checkout data loaded successfully")
                .data(
                        CheckoutDataDTO.builder().addressDTO(addressDTO).cartItemDTOList(cartItemDTOList).build()
                ).build();

    }

    private PayHereDTO createPaymentDetails(OrderDTO orderDTO){
        System.out.println("orderId "+orderDTO.getId());

        String orderId = "000" + orderDTO.getId();
        System.out.println(configService.get("app.public.url"));

        String returnURL = configService.get("app.public.url") + "/api/payments/return";
        String cancelURL = configService.get("app.public.url") + "/api/payments/cancel";
        String notifyURL = configService.get("app.public.url") + "/api/payments/notify";

        User user = entityManager.find(User.class, orderDTO.getUserId());
        if (user == null) return null;

        Address address = entityManager.createQuery("FROM Address a WHERE a.user=:user AND a.isPrimary=:primary", Address.class)
                .setParameter("user", user)
                .setParameter("primary", true)
                .getSingleResult();

        if(address == null) return  null;

        StringBuilder userAddress = new StringBuilder(address.getLineOne());
        if(address.getLineTwo() != null && !address.getLineTwo().isBlank()){
            userAddress.append(",").append(address.getLineTwo());
        }

        StringBuilder items = new StringBuilder();
        double amount = 0;

        //List<OrderItem> orderItems = reqOrder.getOrderItems();

        for(OrderItemDTO orderItem  : orderDTO.getOrderItems()){
            if (!items.isEmpty()) {
                items.append(",");
            }
            items.append(orderItem.getProductTitle())
                    .append(" x ")
                    .append(orderItem.getQty());

            amount += orderItem.getPrice() * orderItem.getQty();
        }

        String formattedAmount = String.format(Locale.US, "%.2f", amount);

        String hashValue = PayHereUtil.generateHash(orderId, formattedAmount);
        PayHereDTO payHereDTO = new PayHereDTO();
        payHereDTO.setSandbox(true);
        payHereDTO.setMerchant_id(PayHereUtil.getMerchantId());
        payHereDTO.setReturn_url(returnURL);
        payHereDTO.setCancel_url(cancelURL);
        payHereDTO.setNotify_url(notifyURL);

        payHereDTO.setOrder_id(orderId);
        payHereDTO.setItems(items.toString());
        payHereDTO.setAmount(formattedAmount);
        payHereDTO.setCurrency(PayHereUtil.APP_CURRENCY);
        payHereDTO.setHash(hashValue);
        payHereDTO.setFirst_name(user.getFirstName());
        payHereDTO.setLast_name(user.getLastName());
        payHereDTO.setEmail(user.getEmail());
        payHereDTO.setPhone(address.getMobile());
        payHereDTO.setAddress(userAddress.toString());
        payHereDTO.setCity("Kandy");
        payHereDTO.setCountry(PayHereUtil.APP_COUNTRY);

        return payHereDTO;
    }

    private AddressDTO getAddressDTO(Address primaryAddress) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(primaryAddress.getId());
        addressDTO.setFirstName(primaryAddress.getUser().getFirstName());
        addressDTO.setLastName(primaryAddress.getUser().getLastName());
        addressDTO.setLineOne(primaryAddress.getLineOne());
        addressDTO.setLineTwo(primaryAddress.getLineTwo());
        addressDTO.setPostalCode(primaryAddress.getPostalCode());
        addressDTO.setMobile(primaryAddress.getMobile());
        addressDTO.setPrimary(primaryAddress.isPrimary());
        return addressDTO;
    }

    public List<CartItemDTO> generateCartDTOs(List<Cart> cartList) {
        List<CartItemDTO> cartDTOList = new ArrayList<>();
        for (Cart cart : cartList) {
            Stock stock = cart.getStock();

            CartItemDTO cartDTO = new CartItemDTO();
            cartDTO.setCartId(cart.getId());
            cartDTO.setStockId(stock.getId());
            cartDTO.setProductTitle(stock.getProduct().getTitle());
            cartDTO.setImages(
                    stock.getProduct().getImages() == null
                            ? Collections.emptyList()
                            : stock.getProduct().getImages().stream()
                            .map(img -> {
                                String cleaned = img.startsWith("/uploads/")
                                        ?img.substring("/uploads/".length())
                                        :img;
                                return FILE_BASE_URL + "/" + cleaned;
                            })
                            .toList()
            );
            cartDTO.setQty(cart.getQty());
            cartDTO.setPrice(stock.getPrice());
            cartDTOList.add(cartDTO);
        }
        return cartDTOList;
    }

}
