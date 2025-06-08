import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../../common/widgets/texts/section_heading.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../personalizations/controllers/address_controller/address_controller.dart';

class AddressSection extends StatelessWidget {
  const AddressSection({super.key});

  @override
  Widget build(BuildContext context) {
    final addressController = Get.put(AddressController());

    return Column(
      children: [
        ESectionHeading(
          title: 'Shipping address',
          titleButton: 'Change',
          onPressed: () => addressController.selectNewAddressPopup(context),
        ),
        Obx(
              () {
            final selectedAddress = addressController.selectedAddress.value;

            // Hiển thị khi đã chọn địa chỉ (id != null)
            if (selectedAddress.id != null) {
              return Column(
                children: [
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Icon(Iconsax.user),
                      const SizedBox(width: ESizes.defaultBetweenItem),
                      Text(selectedAddress.name, style: Theme.of(context).textTheme.bodyLarge,),
                    ],
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Icon(Iconsax.user),
                      const SizedBox(width: ESizes.defaultBetweenItem),
                      Text(selectedAddress.recipientName, style: Theme.of(context).textTheme.bodyLarge,),
                    ],
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Icon(Iconsax.location),
                      const SizedBox(width: ESizes.defaultBetweenItem),
                      Expanded(
                        child: Text(
                          selectedAddress.fullAddress, style: Theme.of(context).textTheme.bodyLarge,
                          overflow: TextOverflow.clip,
                          softWrap: true,
                        ),
                      ),
                    ],
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const Icon(Iconsax.call),
                      const SizedBox(width: ESizes.defaultBetweenItem),
                      Text(selectedAddress.phone, style: Theme.of(context).textTheme.bodyLarge,),
                    ],
                  ),
                ],
              );
            } else {
              // Nếu chưa chọn địa chỉ nào
              return Text(
                'Select address',
                style: Theme.of(context).textTheme.bodyMedium,
                softWrap: true,
              );
            }
          },
        ),
      ],
    );
  }
}
