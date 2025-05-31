package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    // create an address - POST
    @PostMapping("/addresses")
    /*
    * ResponseEntity<AddressDTO>: The method returns a response containing an AddressDTO object and an HTTP status code.
    * @RequestBody AddressDTO addressDTO: This tells Spring to take the JSON payload from the request body and convert it into an AddressDTO object.
    * @Valid: This ensures that any validation annotations (e.g., @NotNull, @Size) on AddressDTO are checked before processing.
    */
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {

        /*
        * This gets the currently logged-in user. authUtil is a utility class that retrieves user information from the security context.
        */
        User user = authUtil.loggedInUser();
        /*
        * This line delegates the creation of the address to a service class (addressService) and passes both the input data (addressDTO) and the user.
        * The service likely saves the address to a database and returns the saved data as a DTO.
        */
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);

        // The response will include the created address data in the body.
        return new ResponseEntity<AddressDTO>(savedAddressDTO, HttpStatus.CREATED);
    }

    // get all addresses - GET
    @GetMapping("/addresses")
    // This method returns a list of addresses (List<AddressDTO>) wrapped in a ResponseEntity.
    public ResponseEntity <List<AddressDTO>> getAddresses() {

        List<AddressDTO> addressList = addressService.getAddresses();

        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    // get address by address id - GET
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        /*
        * This calls the getAddresses() method in the service layer to fetch the list of addresses.
        * The service likely talks to the database and returns the data as DTOs (Data Transfer Objects).
        */
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        // This means the request was successful and the data is being returned.
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    // get user addresses - GET
    @GetMapping("/users/addresses")
    // Returns a list of AddressDTO (data transfer objects) representing the user's addresses.
    public ResponseEntity <List<AddressDTO>> getUserAddresses() {

        // This gets the currently authenticated/logged-in user.
        User user = authUtil.loggedInUser();
        /*
        * This calls the service layer method getUserAddresses(user) which:
        * Uses the user info to fetch only that user's addresses from the database.
        * Returns them as a list of AddressDTO.
        */
        List<AddressDTO> addressList = addressService.getUserAddresses(user);

        // Sends the list of addresses back to the client with an HTTP 200 OK status.
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    // update address - PUT
    /*
    * @PathVariable Long addressId: Extracts the addressId from the URL.
    * @RequestBody AddressDTO addressDTO: Gets the updated address data from the request body.
    * Returns a ResponseEntity containing the updated address DTO and an HTTP status code.
    */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId,
                                                        @RequestBody AddressDTO addressDTO) {

        /*
        * Calls the service layer method to perform the update:
        * Finds the address by addressId.
        * Updates it with the values from addressDTO.
        * Returns the updated address as a DTO.
        */
        AddressDTO updatedAddress = addressService.updateAddressById(addressId, addressDTO);

        // Sends back the updated address with an HTTP 200 OK status.
        return  new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    // delete address - DELETE
    /*
    * @PathVariable Long addressId: Extracts the address ID from the URL.
    * Returns a ResponseEntity<String>, which contains a message (like "Address deleted successfully") and an HTTP status code.
    */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        /*
        * Calls the service layer to:
        * Find the address with the given ID.
        * Delete it (or mark it as deleted).
        * Return a confirmation message as a String.
        */
        String status = addressService.deleteAddress(addressId);

        // Sends back the message (like "Deleted successfully") with an HTTP 200 OK status.
        return new ResponseEntity<>(status, HttpStatus.OK);

    }



}
