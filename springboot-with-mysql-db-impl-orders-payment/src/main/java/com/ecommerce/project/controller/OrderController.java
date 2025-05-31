package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    /*
    * This is a controller method.
    * It handles an HTTP POST or GET request (depending on how it's mapped).
    * It expects:
    * paymentMethod: a value from the URL (like "gcash" or "creditcard").
    * orderRequestDTO: data from the body of the request, like the address ID, payment info, etc.
    * It returns a response wrapped in ResponseEntity<OrderDTO> — the details of the order created.
    */
    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequestDTO){

        /*
        *
        * It gets the logged-in user's email address using a helper utility (authUtil).
        * This email identifies who is placing the order.
        */
        String emailId = authUtil.loggedInEmail();

        // This calls a method in the orderService to actually create the order.
        // The placeOrder method processes all this and returns an OrderDTO object with the order details.
        OrderDTO order = orderService.placeOrder(
                emailId, // The user's email.
                orderRequestDTO.getAddressId(), // The address where the order should be delivered.
                paymentMethod, // The selected payment method.
                orderRequestDTO.getPgName(), // The payment gateway name (pgName, e.g., "GCash").
                orderRequestDTO.getPgPaymentId(), // The payment ID from the payment gateway.
                orderRequestDTO.getPgStatus(), // The status of the payment (e.g., "SUCCESS").
                orderRequestDTO.getPgResponseMessage() // A response message from the gateway (like "Payment successful").
        );

        // Returns the created order and a status code of 201 CREATED to the client.
        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);

    }
}
