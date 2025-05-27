package com.petshop.petopia.service.user;

import com.petshop.petopia.component.ConvertAddress;
import com.petshop.petopia.dto.request.user.AddressRequest;
import com.petshop.petopia.dto.response.user.AddressResponse;
import com.petshop.petopia.model.user.Address;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.AddressRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ConvertAddress convertAddress;

    public List<AddressResponse> getAllAddresses(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        return addressRepository.findByUserId(userId)
                .stream()
                .map(convertAddress::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse createAddress(Integer userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Address address = convertAddress.convertToEntity(request, user);
        if (request.getIsDefault()) {
            setDefaultAddress(userId, address);
        }
        Address savedAddress = addressRepository.save(address);
        return convertAddress.convertToResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateAddress(Integer userId, Integer addressId, AddressRequest request) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ với ID: " + addressId));

        if (!existingAddress.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Địa chỉ này không thuộc về người dùng với ID: " + userId);
        }
        if (request.getName() != null) {
            existingAddress.setName(request.getName());
        }
        if (request.getPhone() != null) {
            existingAddress.setPhone(request.getPhone());
        }
        if (request.getFullAddress() != null) {
            existingAddress.setFullAddress(request.getFullAddress());
        }
        if (request.getIsDefault() != null) {
            if (request.getIsDefault() != existingAddress.isDefault()) {
                existingAddress.setDefault(request.getIsDefault());
                if (request.getIsDefault()) {
                    setDefaultAddress(userId, existingAddress);
                }
            }
        }

        Address updatedAddress = addressRepository.save(existingAddress);
        return convertAddress.convertToResponse(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Integer userId, Integer addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Address addressToDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ với ID: " + addressId));

        if (!addressToDelete.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Địa chỉ này không thuộc về người dùng với ID: " + userId);
        }

        addressRepository.delete(addressToDelete);
    }

    @Transactional
    protected void setDefaultAddress(Integer userId, Address addressToBeDefault) {
        List<Address> currentDefaultAddresses = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        currentDefaultAddresses.forEach(address -> {
            if (!address.getId().equals(addressToBeDefault.getId())) {
                address.setDefault(false);
                addressRepository.save(address);
            }
        });
        addressToBeDefault.setDefault(true);
        addressRepository.save(addressToBeDefault);
    }
}
