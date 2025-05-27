package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.user.AddressRequest;
import com.petshop.petopia.dto.response.user.AddressResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllUserAddresses(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(addressService.getAllAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createNewAddress(
            @RequestHeader("Authorization") String token,
            @RequestBody AddressRequest request)
    {
        Integer userId = jwtService.extractUserId(token);
        return new ResponseEntity<>(addressService.createAddress(userId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateExistingAddress(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer addressId,
            @RequestBody AddressRequest request)
    {
        Integer userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteExistingAddress(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer addressId)
    {
        Integer userId = jwtService.extractUserId(token);
        addressService.deleteAddress(userId, addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
