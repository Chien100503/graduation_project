package com.petshop.petopia.component;

import com.petshop.petopia.dto.request.user.AddressRequest;
import com.petshop.petopia.dto.response.user.AddressResponse;
import com.petshop.petopia.model.user.Address;
import com.petshop.petopia.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ConvertAddress {
    public AddressResponse convertToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setName(address.getName());
        response.setPhone(address.getPhone());
        response.setFullAddress(address.getFullAddress());
        response.setDefault(address.isDefault());
        return response;
    }

    public Address convertToEntity(AddressRequest request, User user) {
        Address address = new Address();
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setFullAddress(request.getFullAddress());
        address.setDefault(request.getIsDefault());
        address.setUser(user);
        return address;
    }
}
