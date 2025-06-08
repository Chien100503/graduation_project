import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';

import '../../../../common/widgets/appbar/appbar.dart';
import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/cloud_helper_functions.dart';
import '../../controllers/address_controller/address_controller.dart';
import 'add_address.dart';

class AddressScreen extends StatelessWidget {
  const AddressScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final controller = Get.put(AddressController());

    return Scaffold(
      floatingActionButton: FloatingActionButton(
        backgroundColor: dark ? EColors.thirdColor : EColors.accent,
        onPressed: () => Get.to(
          () => const AddNewAddress(),
          transition: Transition.zoom,
          duration: const Duration(milliseconds: 500),
        ),
        child:
            Icon(Icons.add, color: dark ? EColors.accent : EColors.thirdColor),
      ),
      appBar: EAppBar(
        showBackArrow: true,
        title:
            Text('Address', style: Theme.of(context).textTheme.headlineSmall),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          await controller.fetchAllAddresses();
        },
        child: FutureBuilder(
          key: Key(controller.refreshData.value.toString()),
          future: controller.getAllUserAddress(),
          builder: (context, snapshot) {
            final response = CloudHelperFunctions.checkMultiRecordState(snapshot: snapshot);
            if (response != null) return response;

            final addresses = snapshot.data!;

            return ListView.builder(
              padding: const EdgeInsets.all(ESizes.defaultSpace),
              itemCount: addresses.length,
              itemBuilder: (_, index) {
                final address = addresses[index];
                final isDefault = address.isDefault == true;
                return Card(
                  color: isDefault ? Colors.blue.shade100 : null,
                  margin: const EdgeInsets.only(bottom: ESizes.defaultSpace),
                  child: ListTile(
                    leading: const Icon(Icons.location_on),
                    title: Row(
                      children: [
                        Expanded(child: Text(address.fullAddress)),
                      ],
                    ),
                    subtitle: Text('${address.name} - ${address.phone}'),
                    trailing: PopupMenuButton<String>(
                      onSelected: (value) async {
                        if (value == 'edit') {
                          Get.to(() => AddNewAddress(
                            initialAddress: address,
                            isEditing: true,
                          ));
                        } else if (value == 'delete') {
                          final confirm = await showDialog(
                            context: context,
                            builder: (_) => AlertDialog(
                              title: const Text('Confirm Deletion'),
                              content: const Text('Are you sure you want to delete this address?'),
                              actions: [
                                TextButton(
                                  onPressed: () => Navigator.of(context).pop(false),
                                  child: const Text('Cancel'),
                                ),
                                TextButton(
                                  onPressed: () => Navigator.of(context).pop(true),
                                  child: const Text('Delete'),
                                ),
                              ],
                            ),
                          );
                          if (confirm) {
                            await controller.deleteAddress(address.id!.toString());
                          }
                        }
                      },
                      itemBuilder: (context) => const [
                        PopupMenuItem(value: 'edit', child: Text('Edit')),
                        PopupMenuItem(value: 'delete', child: Text('Delete')),
                      ],
                    ),
                    onTap: () => controller.selectAddress(address),
                  ),
                );
              },
            );
          },
        ),
      ),

    );
  }
}
