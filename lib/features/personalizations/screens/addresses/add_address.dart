import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';
import '../../../../utils/validators/validation.dart';
import '../../controllers/address_controller/address_controller.dart';
import '../../models/address_model.dart';

class AddNewAddress extends StatefulWidget {
  const AddNewAddress({
    super.key,
    this.initialAddress,
    this.isEditing = false,
  });

  final AddressModel? initialAddress;
  final bool isEditing;

  @override
  State<AddNewAddress> createState() => _AddNewAddressState();
}

class _AddNewAddressState extends State<AddNewAddress> {
  final controller = Get.put(AddressController());

  @override
  void initState() {
    super.initState();
    if (widget.isEditing && widget.initialAddress != null) {
      _initializeFields();
    }
  }

  void _initializeFields() {
    final address = widget.initialAddress!;
    controller.name.text = address.name;
    controller.phoneNumber.text = address.phone;
    controller.fullAddress.text = address.fullAddress;
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return Scaffold(
      appBar: EAppBar(
        title: Text(
          widget.isEditing ? 'Edit Address' : 'Add Address',
          style: Theme.of(context).textTheme.headlineSmall,
        ),
        showBackArrow: true,
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Form(
            key: controller.addressFormKey,
            child: Column(
              children: [
                // Name Field
                TextFormField(
                  controller: controller.name,
                  validator: (value) =>
                      EValidation.validateEmptyText('Name', value),
                  decoration: InputDecoration(
                    prefixIcon: Icon(
                      Iconsax.user,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                    label: Text(
                      'Name',
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: ESizes.inputBetweenFields),
                TextFormField(
                  controller: controller.recipientName,
                  validator: (value) =>
                      EValidation.validateEmptyText('Name', value),
                  decoration: InputDecoration(
                    prefixIcon: Icon(
                      Iconsax.user,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                    label: Text(
                      'Recipient Name',
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: ESizes.inputBetweenFields),

                // Phone Number Field
                TextFormField(
                  controller: controller.phoneNumber,
                  validator: EValidation.validatePhoneNumber,
                  keyboardType: TextInputType.phone,
                  decoration: InputDecoration(
                    prefixIcon: Icon(
                      Iconsax.call,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                    label: Text(
                      'Phone number',
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: ESizes.inputBetweenFields),

                // Full Address Field
                TextFormField(
                  controller: controller.fullAddress,
                  validator: (value) =>
                      EValidation.validateEmptyText('Full Address', value),
                  maxLines: 3,
                  decoration: InputDecoration(
                    prefixIcon: Icon(
                      Iconsax.location,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                    label: Text(
                      'Full Address',
                      style: TextStyle(
                        color: dark ? EColors.thirdColor : EColors.primaryColor,
                      ),
                    ),
                    alignLabelWithHint: true,
                  ),
                ),

                const SizedBox(height: ESizes.defaultBetweenSections),

                // Save/Update Button
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () {
                      if (widget.isEditing) {
                        controller.updateAddress(widget.initialAddress!);
                      } else {
                        controller.addNewAddress();
                      }
                    },
                    child: Text(widget.isEditing ? 'Update' : 'Save'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }
}
