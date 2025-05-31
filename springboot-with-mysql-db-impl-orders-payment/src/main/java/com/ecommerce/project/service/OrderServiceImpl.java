package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional // makes sure all operations inside this method succeed together. If anything fails, everything is rolled back (undone).
    /*
    * This method returns an OrderDTO object.
    * It accepts order details like user email, delivery address, payment method, and payment gateway info.
    */
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        // It tries to find the cart that belongs to the user with the given email.
        Cart cart = cartRepository.findCartByEmail(emailId);

        // If no cart is found, it throws an exception (stops the process).
        if(cart==null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        // It fetches the delivery address using the addressId.
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId)); // If the address doesn't exist, it throws an error.

        // Create the order
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        // Creates a Payment object using info from the gateway (e.g., GCash).
        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);

        // Links the payment to the order.
        payment.setOrder(order);

        // Saves the payment to the database.
        payment = paymentRepository.save(payment);

        // Adds the payment back to the order.
        order.setPayment(payment);

        // Saves the Order object to the database and stores it as savedOrder.
        Order savedOrder = orderRepository.save(order);

        // Retrieves all items from the user's cart.
        List<CartItem> cartItems = cart.getCartItems();

        // If it's empty, throws an error.
        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        /*
        * Converts each CartItem into an OrderItem:
        * Copies product, quantity, discount, price.
        * Connects each to the saved order.
        */
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        // Saves all order items in one go.
        orderItems = orderItemRepository.saveAll(orderItems);

        // update product stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity());
            productRepository.save(product);

            // clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());

        });
        // Converts the saved order to an OrderDTO object (used to send back to the frontend).
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        // Adds the list of ordered items (OrderItemDTO) to the order.
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        // Adds the address ID to the order DTO.
        orderDTO.setAddressId(addressId);

        // Returns the completed OrderDTO, ready to be sent as a response to the user.
        return orderDTO;
    }
}
