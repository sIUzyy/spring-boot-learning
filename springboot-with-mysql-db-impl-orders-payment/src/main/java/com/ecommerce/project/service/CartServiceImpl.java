package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    /*
    * This method adds a product to the user's cart.
    * It takes two inputs: the product ID and the quantity.
    * It returns a CartDTO (a data object that shows cart details).
    */
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // find existing cart or create one
        Cart cart = createCart();

        // It looks up the product in the database. If not found, it throws an error saying the product doesn't exist.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // This checks if the same product is already in the user's cart.
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        // If the product is already in the cart, don’t allow adding it again — show an error.
        if(cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exist in the cart");
        }

        // If the requested quantity is more than what's in stock, show an error.
        if(product.getQuantity() < quantity){
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        /*
        * Create a new CartItem object.
        * Set its product, cart, quantity, discount, and price.
        */
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // Save the new cart item into the database.
        cartItemRepository.save(newCartItem);

        // This does nothing — it sets the quantity to itself.
        product.setQuantity((product.getQuantity()));

        // Add the product's price × quantity to the cart's total.
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        // Save the updated cart with the new total price.
        cartRepository.save(cart);

        // Map the cart entity into a CartDTO (for returning to the frontend).
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // Convert each product in the cart to a ProductDTO.
        // Also add the quantity from CartItem into the ProductDTO.
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item
                -> {ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                map.setQuantity(item.getQuantity());
                return map;
        });

        // Add the list of product DTOs (with quantities) into the cart DTO.
        cartDTO.setProducts(productStream.toList());

        // Finally, return the updated cart with product info.
        return cartDTO;
    }

    /*
    * This overrides a method from the CartService interface.
    */
    @Override
    public List<CartDTO> getAllCarts() {
        // Fetches all carts from the database using the repository.
        List<Cart> carts = cartRepository.findAll();

        // If no carts are found, throw a custom exception with a message.
        if(carts.isEmpty()){
            throw new APIException("No cart exist");
        }

        // Starts a stream over the carts list and begins mapping each Cart object to a CartDTO.
        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class); // Uses modelMapper to automatically copy matching fields from Cart to CartDTO.

                    /*
                    * It’s converting all items in a shopping cart into a list of ProductDTO objects (Data Transfer Objects), including the quantity for each product.
                    * */
                    // cart.getCartItems() Gets the list of items in the user's shopping cart.
                    // .stream() Turns the list into a stream so we can loop through and transform each item easily.
                    // .map(cartItem -> { ... }) This takes each item in the cart and converts it into a ProductDTO (used for sending data to the frontend).
                    //  ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    // Uses modelMapper to copy the product info (like name, price, etc.) from the Product object inside CartItem into a ProductDTO.
                    // productDTO.setQuantity(cartItem.getQuantity());
                    // Manually adds the quantity from the CartItem into the ProductDTO, since modelMapper doesn’t do that by default.
                    List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                        ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                        return productDTO;
                    }).toList(); // Collects all the converted ProductDTOs into a new list called products.

                    // Adds the list of product DTOs into the current CartDTO.
                    cartDTO.setProducts(products);
                    return cartDTO; // Returns the fully built CartDTO inside the stream map function.
                }).toList(); // Collects all CartDTOs into a list and returns it.
        return cartDTOS;
    }


    /*
    * This is a method from a service class that retrieves a specific user's cart based on email and cart ID.
    * It returns a CartDTO (a Data Transfer Object version of the cart, used for sending only needed info to the frontend).
    */
    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        // Uses your custom repository method to fetch the cart that matches the provided email and cart ID.
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);

        // If no cart is found, throw a custom exception to tell the system: "Cart not found with this ID."
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        // Converts the Cart entity into a CartDTO object.
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        /*
        * For each item in the cart, it sets the product's quantity equal to the cart item's quantity.
        * This ensures the product quantity reflects how many of that product are in the cart, not the overall stock.
        */
        cart.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity()));

        /*
        * Converts all the products in the cart to a list of ProductDTO (simplified versions of Product).
        * It does this using a stream and the same modelMapper.
        */
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();

        // Adds the list of ProductDTOs to the CartDTO, so the response includes the product details in the cart.
        cartDTO.setProducts(products);

        // Finally, it returns the fully prepared CartDTO with all the necessary data for the frontend.
        return cartDTO;
    }

    @Transactional // If anything fails inside the method, all changes will be rolled back.
    @Override
    /*
    * Accepts a product ID and a quantity change (e.g., +1 for add, -1 for delete).
    * Returns the updated cart as a CartDTO.
    */
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        // Calls a utility method to get the logged-in user's email.
        String emailId = authUtil.loggedInEmail();

        // Gets the user's cart from the database using their email.
        Cart userCart = cartRepository.findCartByEmail(emailId);

        // Extracts the cart ID.
        Long cartId = userCart.getCartId();

        // Fetches the full cart from the DB using its ID.
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId)); // If it doesn't exist, throws a ResourceNotFoundException.

        // Tries to fetch the product.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId)); // Throws an error if it doesn't exist.

        // If the product is out of stock (0 quantity), throw an error.
        if(product.getQuantity() == 0){
            throw new APIException("Sorry, '" + product.getProductName() + "' is currently out of stock.");
        }

        // If the requested quantity exceeds the stock, throw an error.
        if(product.getQuantity() < quantity){
            throw new APIException("Only " + product.getQuantity() + " item(s) of '" + product.getProductName() + "' are available. Please reduce the quantity.");
        }

        // Find if the product is already in the cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the item isn't already in the cart, it can't be updated—so throw an error.
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " does not exist in the cart");
        }

        // calcuate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // validation to prevent negative quantity
        if(newQuantity < 0){
            throw new APIException("The resulting quantity cannot be negative");
        }

        // if quantity is == 0
        if(newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        } else {
            // Update the cart item
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());

            // Updates the cart's total based on the change in quantity.
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            // Save the cart
            cartRepository.save(cart);


        }

        // Save cart item
        CartItem updatedItem = cartItemRepository.save(cartItem);

        // If the item’s quantity is now 0 (i.e., user removed it), remove it from the cart completely.
        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        // Uses ModelMapper to convert the Cart entity to a simpler CartDTO for response.
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // Convert each cart item’s product to ProductDTO
        List<CartItem>cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        // Sets the list of ProductDTOs (with quantity) into the cart DTO.
        cartDTO.setProducts(productStream.toList());

        // Returns the final, updated cart as a DTO, ready to be sent back as a response.
        return cartDTO;
    }

    @Transactional // If anything fails inside the method, all changes will be rolled back.
    @Override
    /*
    * cartId – the ID of the cart where the product should be deleted.
    * productId – the ID of the product to remove.
    */
    public String deleteProductFromCart(Long cartId, Long productId) {
        /*
        * Tries to fetch the cart from the database using the cartId.
        * If the cart doesn't exist, throws a custom ResourceNotFoundException.
        */
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        /*
        * Checks if the product exists in the given cart.
        * This assumes a method like this exists in your CartItemRepository.
        */
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the cart item is null, that means the product isn't in the cart.
        if(cartItem == null){
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        /*
        * Update cart total price
        * Updates the cart’s total price by subtracting the price of the removed product multiplied by its quantity.
        * This keeps the cart's total accurate after removing the product.
        */
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        /*
        * Deletes the cart item using a custom query (which you defined earlier).
        * Removes that specific product from the cart.
        */
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        // Returns a friendly confirmation message to the user.
        return "Product" + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        // Fetches the full cart from the DB using its ID.
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId)); // If it doesn't exist, throws a ResourceNotFoundException.

        // Tries to fetch the product.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId)); // Throws an error if it doesn't exist.

        /*
        * Looks for the specific CartItem in the cart by:
        * cartId: which cart
        * productId: which product
        * If found, it proceeds. Otherwise:
        */
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // Throws an exception if the product is not in the cart.
        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " does not exist in the cart");
        }

        /*
        * Subtracts the old total price of that cart item from the cart's total.
        * Example: if the item was $10 and quantity is 2, subtract $20.
        */
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        // Updates the cart item's price to the new special price (like a discount).
        cartItem.setProductPrice(product.getSpecialPrice());

        /*
        * Recalculates and sets the updated total cart price by:
        * Adding back the new total for that item (e.g. $8 × 2 = $16)
        */
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        // Saves the updated CartItem to the database.
        cartItem = cartItemRepository.save(cartItem);

    }


    private Cart createCart(){
        // This line tries to find the cart that belongs to the currently logged-in user.
        // authUtil.loggedInEmail() gets the user's email.
        // cartRepository.findCartByEmail(...) searches the database for a cart using that email.
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        // Checks if a cart was found for that user.
        if(userCart != null) {
            // If a cart already exists, it returns that cart and stops the method here — so you don’t create a new one.
            return userCart;
        }

        /*
        * If no cart exists, create a new one with:
        * Total price = 0
        * Logged-in user as the owner
        * Save and return it
        * */
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;

    }
}
