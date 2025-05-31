package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service // BUSINESS LOGIC
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${project.image}") // get the property we set on application.properties
    private String path;


    // create a product - POST
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        // if category is exist, return
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if not, return this error

        // list of products from the category
        List<Product> products = category.getProducts();

        // create a variable
        boolean isProductNotPresent = true;

        // loop through products, check if product is exist
        for (Product value : products) {
            // check if product name is equal to product name in db
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false; // if match
                break; // then stop the code.
            }
        }

        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice(); // discount calculation price - (discount*0.01) * price
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            throw new APIException("Product already exist!!");
        }

    }

    // get all product - GET
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // get the page of product
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        // list of product
        List<Product> products = pageProducts.getContent();

        // then we map thru the products and the the response is ProductDTO
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)) // convert the product to ProductDTO
                .toList();


        // set the content of product response to productDTOS
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // metadata
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    // get all products by category
    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // if category is exist, return
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)); // if not, return this error

        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // get the page of product and use the custom method find by category order by price asc
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        // list of product
        List<Product> products = pageProducts.getContent();

        // validation
        if(products.isEmpty()){
            throw new APIException(category.getCategoryName() + " category does not have any products!");
        }

        // then we map thru the products and the the response is ProductDTO
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)) // convert the product to ProductDTO
                .toList();

        // set the content of product response to productDTOS
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // metadata
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    // get all products by keyword
    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // get the page of product and use the custom method and find by keyword(custom)
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails); // % = pattern matching

        // list of product
        List<Product> products = pageProducts.getContent();

        // then we map thru the products and the the response is ProductDTO
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)) // convert the product to ProductDTO
                .toList();

        // validation
        if(products.isEmpty()){
            throw new APIException("Products not found with keyword: " + keyword);
        }

        // set the content of product response to productDTOS
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // metadata
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    // update a product
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        // get the existing product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // convert
        Product product = modelMapper.map(productDTO, Product.class);

        // update the product info with the one in request body
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        // save to database
        Product savedProduct = productRepository.save(productFromDb);

        // Retrieves all carts that contain the specified productId.
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        // Map products inside the cart to ProductDTOs
        // For each CartItem in the cart, extract its Product, convert it to a ProductDTO, and collect them into a list.
        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            // For each cart, it converts its cart items’ Product into ProductDTOs.
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                    .toList(); // Sets this list of products on the CartDTO.
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();

        // Loops through each cart DTO and calls a service to update product info inside that cart
        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    // delete a product
    @Override
    public ProductDTO deleteProduct(Long productId) {

        // fetch the product in db
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // This line retrieves all Cart entities that contain the product with the given productId.
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        /*
        * Iterates over each cart in the list.
        * Calls a method deleteProductFromCart from the cartService, passing in:
        * The cart's ID
        * The productId to remove
        * This likely removes the product from the cart's list of items (e.g., cartItems), then updates the cart in the database.
        */
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        // delete the product
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    // update a product image
    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // get the product from db
        Product productfromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // upload the image to server
        // get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // updating the new file name to the product
        productfromDb.setImage(fileName);

        // save updated product
        Product updatedProduct = productRepository.save(productfromDb);

        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }


}
