import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/loader/loader.dart';

import '../../../../data/repositories/address/address_repository.dart';
import '../../../../utils/constants/images_strings.dart';
import '../../../../utils/helpers/cloud_helper_functions.dart';
import '../../../../utils/popups/full_screen_loader.dart';
import '../../models/address_model.dart';

class AddressController extends GetxController {
  static AddressController get instance => Get.find();

  RxList<AddressModel> addressList = <AddressModel>[].obs;
  final addressRepository = Get.put(AddressRepository());

  // Form controllers
  final name = TextEditingController();
  final phoneNumber = TextEditingController();
  final fullAddress = TextEditingController();
  GlobalKey<FormState> addressFormKey = GlobalKey<FormState>();

  // Observable variables
  RxBool refreshData = true.obs;
  final Rx<AddressModel> selectedAddress = AddressModel(
    id: null,
    name: '',
    phone: '',
    fullAddress: '',
  ).obs;

  @override
  void onInit() {
    super.onInit();
    fetchAllAddresses();
  }

  /// Fetch all user addresses and update observable list
  Future<void> fetchAllAddresses() async {
    try {
      final addresses = await addressRepository.getAllUserAddresses();
      addressList.assignAll(addresses);
    } catch (e) {
      ECustomSnackBar.showError(title: 'Address not found', message: e.toString());
    }
  }

  /// Fetch addresses and return list directly
  Future<List<AddressModel>> getAllUserAddress() async {
    try {
      return await addressRepository.getAllUserAddresses();
    } catch (e) {
      ECustomSnackBar.showError(title: 'Address not found', message: e.toString());
      return [];
    }
  }

  /// Set selected address as default
  Future<void> selectAddress(AddressModel address) async {
    try {
      final response = await addressRepository.setDefaultAddress(address.id!.toString());

      if (response.success) {
        selectedAddress.value = address;
        refreshData.toggle();
        ECustomSnackBar.showSuccess(title: 'Success', message: 'Default address updated');
      } else {
        ECustomSnackBar.showError(title: 'Error', message: response.message);
      }
    } catch (e) {
      ECustomSnackBar.showError(title: 'Error', message: e.toString());
    }
  }

  /// Add new Address
  Future<void> addNewAddress() async {
    try {
      EFullScreenLoader.openLoadingDialog('Storing Address...', EImages.animalIcon);

      if (!addressFormKey.currentState!.validate()) {
        EFullScreenLoader.stopLoading();
        return;
      }

      final address = AddressModel(
        name: name.text.trim(),
        phone: phoneNumber.text.trim(),
        fullAddress: fullAddress.text.trim(),
        isDefault: selectedAddress.value.id == null,
      );

      final newAddress = await addressRepository.addAddress(address);

      if (selectedAddress.value.id == null) {
        selectedAddress.value = newAddress;
      }

      EFullScreenLoader.stopLoading();

      ECustomSnackBar.showSuccess(
        title: 'Congratulations',
        message: 'Your address has been saved successfully.',
      );

      refreshData.toggle();
      resetFormFields();
      Navigator.of(Get.context!).pop();
    } catch (e) {
      EFullScreenLoader.stopLoading();
      ECustomSnackBar.showError(title: 'Error', message: e.toString());
    }
  }

  /// Update address
  Future<void> updateAddress(AddressModel address) async {
    try {
      EFullScreenLoader.openLoadingDialog('Updating Address...', EImages.animalIcon);

      if (!addressFormKey.currentState!.validate()) {
        EFullScreenLoader.stopLoading();
        return;
      }

      final updatedAddress = address.copyWith(
        name: name.text.trim(),
        phone: phoneNumber.text.trim(),
        fullAddress: fullAddress.text.trim(),
      );

      await addressRepository.updateAddress(updatedAddress);

      if (selectedAddress.value.id == address.id) {
        selectedAddress.value = updatedAddress;
      }

      EFullScreenLoader.stopLoading();

      ECustomSnackBar.showSuccess(
        title: 'Congratulations',
        message: 'Your address has been updated successfully.',
      );

      refreshData.toggle();
      resetFormFields();
      Navigator.of(Get.context!).pop();
    } catch (e) {
      EFullScreenLoader.stopLoading();
      ECustomSnackBar.showError(title: 'Error', message: e.toString());
    }
  }

  /// Delete address
  Future<void> deleteAddress(String addressId) async {
    try {
      EFullScreenLoader.openLoadingDialog('Deleting Address...', EImages.animalIcon);

      await addressRepository.deleteAddress(addressId);

      EFullScreenLoader.stopLoading();

      ECustomSnackBar.showSuccess(title: 'Success', message: 'Address deleted successfully.');

      refreshData.toggle();

      if (selectedAddress.value.id.toString() == addressId) {
        selectedAddress.value = AddressModel(
          id: null,
          name: '',
          phone: '',
          fullAddress: '',
        );
      }
    } catch (e) {
      EFullScreenLoader.stopLoading();
      ECustomSnackBar.showError(title: 'Error', message: e.toString());
    }
  }

  /// Set new default address and update selectedAddress
  Future<void> setAddressAsDefault(String addressId) async {
    try {
      await addressRepository.setDefaultAddress(addressId);

      final addresses = await getAllUserAddress(); // ✅ dùng hàm trả về danh sách
      AddressModel? newDefault = addresses.firstWhereOrNull((a) => a.isDefault == true);

      if (newDefault != null) {
        selectedAddress.value = newDefault;
      }

      refreshData.toggle();
    } catch (e) {
      ECustomSnackBar.showError(title: 'Lỗi', message: e.toString());
    }
  }

  /// Reset form fields
  void resetFormFields() {
    name.clear();
    phoneNumber.clear();
    fullAddress.clear();
    addressFormKey = GlobalKey<FormState>();
  }

  @override
  void onClose() {
    name.dispose();
    phoneNumber.dispose();
    fullAddress.dispose();
    super.onClose();
  }
}
