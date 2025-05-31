package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {

    // custom method to create an address of user
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    // custom method to get all address
    List<AddressDTO> getAddresses();

    // custom method to get address by id
    AddressDTO getAddressById(Long addressId);

    // custom method to get user address
    List<AddressDTO> getUserAddresses(User user);

    // custom method to update address
    AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO);

    // custom method to delete address
    String deleteAddress(Long addressId);
}
