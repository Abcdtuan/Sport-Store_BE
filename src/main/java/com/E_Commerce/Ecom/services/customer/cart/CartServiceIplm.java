package com.E_Commerce.Ecom.services.customer.cart;

import com.E_Commerce.Ecom.dto.*;
import com.E_Commerce.Ecom.entity.*;
import com.E_Commerce.Ecom.enums.OrderStatus;
import com.E_Commerce.Ecom.enums.PaymentMethod;
import com.E_Commerce.Ecom.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceIplm implements CartService {

    private final OrderRepository orderRepository;

    private final CartItemRepository cartItemRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final CouponRepository couponRepository;

    private final PaymentRepository paymentRepository;


    @Override
    public ResponseEntity<?> addProductsToCart(AddProductInCartDto addProductInCartDto) {

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);

        if (activeOrder == null) {
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            activeOrder = new Order();
            activeOrder.setUser(optionalUser.get());
            activeOrder.setOrderStatus(OrderStatus.PENDING);
            activeOrder.setAmount(0L);
            activeOrder.setTotalAmount(0L);
            activeOrder.setDiscount(0L);
            activeOrder.setCartItems(new ArrayList<>());

            activeOrder = orderRepository.save(activeOrder);
        }

        Optional<CartItems> cartItems = cartItemRepository.findByProductIdAndUserIdAndOrderId(
                addProductInCartDto.getProductId(),
                addProductInCartDto.getUserId(),
                activeOrder.getId()
        );

        if (cartItems.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already in cart");
        }

        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        CartItems cartItems1 = new CartItems();
        cartItems1.setProduct(optionalProduct.get());
        cartItems1.setPrice(optionalProduct.get().getPrice());
        cartItems1.setQuantity(1L);
        cartItems1.setUser(activeOrder.getUser());
        cartItems1.setOrder(activeOrder);

        cartItemRepository.save(cartItems1);

        activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItems1.getPrice());
        activeOrder.setAmount(activeOrder.getAmount() + cartItems1.getPrice());
        activeOrder.getCartItems().add(cartItems1);
        orderRepository.save(activeOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItems1);
    }

    public OrderDto getCartByUserId(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        List<CartItemsDto> cartItems = activeOrder.getCartItems().stream().map(CartItems::getCartDto).collect(Collectors.toList());
        OrderDto orderDto = new OrderDto();
        orderDto.setId(activeOrder.getId());
        orderDto.setAmount(activeOrder.getAmount());
        orderDto.setDiscount(activeOrder.getDiscount());
        orderDto.setOrderStatus(activeOrder.getOrderStatus());
        orderDto.setTotalAmount(activeOrder.getTotalAmount());
        orderDto.setCartItems(cartItems);
        if( activeOrder.getCoupon() != null){
            orderDto.setCouponName(activeOrder.getCoupon().getName());
        }



        return orderDto;
    }

    @Override
    public OrderDto applyCoupon(Long userId, Long couponId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if(activeOrder == null){
            throw new RuntimeException("Không tìm thấy đơn hàng Pending");
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        double discountAmount = (activeOrder.getTotalAmount() * (coupon.getDiscount() / 100.0));
        double netAmount = activeOrder.getTotalAmount() - discountAmount;

        activeOrder.setAmount((long) netAmount);
        activeOrder.setDiscount((long) discountAmount);
        activeOrder.setCoupon(coupon);

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }
    public List<CouponDto> getAvailableCoupons() {
        List<Coupon> coupons = couponRepository.findAll()
                .stream()
                .filter(c -> c.getExpirationDate() == null || c.getExpirationDate().after(new Date()))
                .toList();

        return coupons.stream()
                .map(c -> new CouponDto(c.getId(), c.getCode(), c.getName(), c.getDiscount(), c.getExpirationDate()))
                .collect(Collectors.toList());
    }
    private boolean couponIsExprited(Coupon coupon){
        Date now = new Date();
        Date expirationDate = coupon.getExpirationDate();
        return expirationDate != null && now.after(expirationDate);
    }

    @Override
    public OrderDto increaseQuantity(AddProductInCartDto addProductInCartDto){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);
        Optional<Product> productOptional = productRepository.findById(addProductInCartDto.getProductId());
        Optional<CartItems> cartItems = cartItemRepository.findByProductIdAndUserIdAndOrderId(addProductInCartDto.getProductId(), addProductInCartDto.getUserId(), activeOrder.getId());
        if(cartItems.isPresent() && productOptional.isPresent()){
            CartItems cartItems1 = cartItems.get();
            Product product = productOptional.get();
            if(cartItems1.getQuantity() < product.getStockQuantity()){
                cartItems1.setQuantity(cartItems1.getQuantity() + 1);
                activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
                activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());

                applyDiscountIfCouponExists(activeOrder);

                cartItemRepository.save(cartItems1);
                orderRepository.save(activeOrder);
            } else {
                throw new RuntimeException("Không thể tăng số lượng. Chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho.");
            }
        }
        return null;
    }

    @Override
    public OrderDto decreaseQuantity(AddProductInCartDto addProductInCartDto){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);
        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        Optional<CartItems> cartItems = cartItemRepository.findByProductIdAndUserIdAndOrderId(addProductInCartDto.getProductId(), addProductInCartDto.getUserId(), activeOrder.getId());
        if(optionalProduct.isPresent() && cartItems.isPresent() ){
            CartItems cartItems1 = cartItems.get();
            Product product = optionalProduct.get();
            activeOrder.setAmount(activeOrder.getAmount() - product.getPrice());
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() - product.getPrice());
            cartItems1.setQuantity(cartItems1.getQuantity() - 1);

            applyDiscountIfCouponExists(activeOrder);

            cartItemRepository.save(cartItems1);
            orderRepository.save(activeOrder);
            return activeOrder.getOrderDto();

        }
        return null;
    }

    private void applyDiscountIfCouponExists(Order activeOrder) {
        if (activeOrder.getCoupon() != null) {
            double amountDiscount = (activeOrder.getAmount() * (activeOrder.getCoupon().getDiscount() / 100.0));
            double netAmount = (activeOrder.getTotalAmount() - amountDiscount);
            activeOrder.setAmount((long) netAmount);
            activeOrder.setDiscount((long) amountDiscount);
        }
    }

    @Override
    public boolean deleteCartItem(Long id){
        Optional<CartItems> cartItems = cartItemRepository.findById(id);
        if(cartItems.isPresent()){
            CartItems cartItems1 = cartItems.get();
            Order order = cartItems1.getOrder();

            Long subTotal = cartItems1.getQuantity() * cartItems1.getPrice();
            order.setTotalAmount(order.getTotalAmount() - subTotal);
            order.setAmount(order.getAmount() - subTotal);
            applyDiscountIfCouponExists(order);

            order.getCartItems().remove(cartItems1);
            cartItemRepository.delete(cartItems1);

            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public OrderDto placeOrder( PlaceOrderDto placeOrderDto){
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.PENDING);
        Optional<User> optionalUser = userRepository.findById(placeOrderDto.getUserId());
        if(optionalUser.isPresent()){
            activeOrder.setAddress(placeOrderDto.getAddress());
            activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
            activeOrder.setPhone(placeOrderDto.getPhoneNumber());
            activeOrder.setName(placeOrderDto.getName());
            activeOrder.setDate(new Date());
            activeOrder.setTrackingId(UUID.randomUUID());
            activeOrder.setPaymentMethod(placeOrderDto.getPaymentMethod());
            activeOrder.setOrderStatus(OrderStatus.PLACED);
            orderRepository.save(activeOrder);
            for (CartItems item : activeOrder.getCartItems()) {
                Product product = item.getProduct();
                long newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) newStock = 0;
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }

            Payment payment = new Payment();
            payment.setOrder(activeOrder);
            paymentRepository.save(payment);

            Order order = new Order();
            order.setAmount(0L);
            order.setTotalAmount(0L);
            order.setDiscount(0L);
            order.setUser(optionalUser.get());
            order.setOrderStatus(OrderStatus.PENDING);
            orderRepository.save(order);
            return activeOrder.getOrderDto();


        }
        return null;
    }
}
