package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    /*
    * This is an implementation of a method likely declared in a service interface.
    * It takes:
    * addressDTO: The address data from the client (e.g., city, zip code, etc.).
    * user: The currently logged-in user, passed in by the controller.
    */
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        // Converts AddressDTO into an Address entity using ModelMapper.
        //This makes the DTO ready to be saved to the database.
        Address address = modelMapper.map(addressDTO, Address.class);

        // Gets the current list of addresses for the user.

        List<Address> addressList = user.getAddresses();

        //Adds the new address to that list.
        addressList.add(address);

        //Updates the user's list of addresses.
        user.setAddresses(addressList);

        // This sets the foreign key relationship — the address "belongs" to the user.
        //Required to maintain referential integrity in the database.
        address.setUser(user);

        // Saves the new address to the database using Spring Data JPA’s addressRepository.
        Address savedAddress = addressRepository.save(address);

        // Converts the saved Address entity back into a AddressDTO to send as a response to the client.
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    // Returns a List of AddressDTO.
    // Typically used by an admin endpoint or a general listing feature.
    public List<AddressDTO> getAddresses() {
        // Retrieves all address records from the database using Spring Data JPA.
        //addressRepository is likely an interface that extends JpaRepository<Address, Long>.
        List<Address> addresses = addressRepository.findAll();
        /*
        * Uses a Java Stream to: Iterate through each Address entity. Map it to an AddressDTO using ModelMapper.toList() creates a list from the stream results.
        */
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        // Sends the list of address DTOs back to the calling code, typically your controller.
        return addressDTOs;

    }

    @Override
    // This method retrieves one address by its ID.
    //It returns an AddressDTO, which is a simplified version of the Address entity.
    public AddressDTO getAddressById(Long addressId) {
        // addressRepository.findById(addressId) tries to find the Address in the database.
        // It returns an Optional<Address>, meaning the address might be present or not.
        // If not found, .orElseThrow(...) throws a ResourceNotFoundException.
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Converts the Address entity to an AddressDTO using ModelMapper.
        //This is useful because you usually don’t want to expose the full entity (with things like user references or internal fields) directly in the API response.
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    /*
    * It takes a User object as input.
    * It returns a list of AddressDTO—the addresses belonging only to that user.
    */
    public List<AddressDTO> getUserAddresses(User user) {
        // Calls getAddresses() on the User entity.
        //Assumes there's a relationship (like OneToMany) between User and Address, typically like this:
        List<Address> addresses = user.getAddresses();

        /*
        * Uses Java Stream to:
        * Loop through each Address.
        * Convert it to an AddressDTO using ModelMapper.
        * Collect the result into a List<AddressDTO>.
        */
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    /*
    * This method updates an existing address in the database by its ID.
    * It accepts:
    * addressId: the ID of the address to update.
    * addressDTO: the new address data to apply.
    * It returns an updated AddressDTO after saving the changes.
    */
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {

        // Looks for the Address entity with the given ID.
        //If it doesn’t exist, throws a ResourceNotFoundException.
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Updates individual fields of the existing Address entity using values from the AddressDTO.
        //Only specified fields are updated (no changes to related user or ID).
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPinCode(addressDTO.getPinCode());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

        // Persists the updated address entity.
        //Returns the updated address instance with any database-generated values refreshed.
        Address updatedAddress = addressRepository.save(addressFromDatabase);

        // Retrieves the user linked to this address.
        User user = addressFromDatabase.getUser();

        // Removes the old address reference from the user's addresses list.
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));

        // Adds the updated address to the list.
        user.getAddresses().add(updatedAddress);

        // Saves the user entity to update the relationship in the database.
        userRepository.save(user);

        // Maps the updated Address entity back to AddressDTO and returns it.
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    /*
    * This method deletes an address by its ID.
    * It returns a confirmation message as a String.
    */
    public String deleteAddress(Long addressId) {
        // Tries to find the Address entity with the given ID.
        //If it doesn't exist, throws a ResourceNotFoundException (which typically results in a 404 error in the API).
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Gets the User associated with this address.
        //This assumes there is a relation like @ManyToOne from Address to User.
        User user = addressFromDatabase.getUser();

        // Removes the address with the given ID from the user’s list of addresses.
        //This prevents a "dangling" reference in the user entity after the address is deleted.
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));

        // Saves the user entity to update its list of addresses in the database.
        //This is important to keep the database consistent.
        userRepository.save(user);

        // Deletes the Address from the database.
        addressRepository.delete(addressFromDatabase);
        return "Address deleted successfully with address id: " + addressId;
    }
}
