import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:iconsax/iconsax.dart';

import '../../../../../common/widgets/appbar/appbar.dart';
import '../../../../../utils/constants/sizes.dart';
import '../../../../../utils/validators/validation.dart';

class ChangeName extends StatelessWidget {
  const ChangeName({super.key});

  @override
  Widget build(BuildContext context) {
    // final controller = Get.put(UpdateNameController());
    return Scaffold(
      appBar: const EAppBar(
        title: Text('Change Name'),
        showBackArrow: true,
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(ESizes.defaultSpace),
          child: Column(
            children: [
              Text('Please enter your name in the 2 boxes below',
                  style: Theme.of(context).textTheme.titleMedium),
              const SizedBox(height: ESizes.defaultBetweenSections),
              Form(
                  // key: controller.updateNameController,
                  child: Column(
                    children: [
                      TextFormField(
                        // controller: controller.firstName,
                        validator: (value) => EValidation.validateEmptyText('First name', value),
                        expands: false,
                        decoration: const InputDecoration(
                            labelText: 'First name', prefixIcon: Icon(Iconsax.user)
                        ),
                      ),
                      const SizedBox(height: ESizes.inputBetweenFields),
                      TextFormField(
                        // controller: controller.lastName,
                        validator: (value) => EValidation.validateEmptyText('Last name', value),
                        expands: false,
                        decoration: const InputDecoration(
                            labelText: 'Last name', prefixIcon: Icon(Iconsax.user)
                        ),
                      ),
                      const SizedBox(height: ESizes.defaultBetweenSections),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: (){},
                          // onPressed: () => controller.updateUserName(),
                          child: const Text('Save'),
                        ),
                      )
                    ],
                  ))
            ],
          ),
        ),
      ),
    );
  }
}
