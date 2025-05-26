import 'dart:io';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pet_shop/data/repositories/user_repository.dart';
import 'package:pet_shop/features/personalizations/controllers/profile/user_controller.dart';
import 'package:pet_shop/utils/constants/images_strings.dart';
import 'package:pet_shop/utils/constants/sizes.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/common/widgets/loader/loader.dart';
import 'package:pet_shop/utils/popups/full_screen_loader.dart';
import '../../../../../common/widgets/images/circle_images.dart';

class UpdateProfileScreen extends StatefulWidget {
  const UpdateProfileScreen({super.key});

  @override
  State<UpdateProfileScreen> createState() => _UpdateProfileScreenState();
}

class _UpdateProfileScreenState extends State<UpdateProfileScreen> {
  final controller = UserController.instance;
  final _userRepo = UserRepository();
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _firstName;
  late TextEditingController _lastName;
  late TextEditingController _name;
  late TextEditingController _phone;

  File? _selectedImage;

  @override
  void initState() {
    super.initState();
    _firstName = TextEditingController(text: controller.profile.value.firstName);
    _lastName = TextEditingController(text: controller.profile.value.lastName);
    _phone = TextEditingController(text: controller.profile.value.phone);
    _name = TextEditingController(text: controller.profile.value.name);
  }

  @override
  void dispose() {
    _firstName.dispose();
    _lastName.dispose();
    _phone.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    final image = await ImagePicker().pickImage(
        source: ImageSource.gallery,
        imageQuality: 70,
        maxHeight: 512,
        maxWidth: 512
    );
    if (image != null) {
      setState(() => _selectedImage = File(image.path));
    }
  }

  Future<void> _submit() async {
    if (_formKey.currentState!.validate()) {
      EFullScreenLoader.openLoadingDialog('Đang cập nhật...', EImages.loaderAnimation);
      await _userRepo.updateProfileDio(
        firstName: _firstName.text.trim(),
        lastName: _lastName.text.trim(),
        name: _name.text.trim(),
        phone: _phone.text.trim(),
        avatarFile: _selectedImage,
      );
      EFullScreenLoader.stopLoading();
      Get.back();
    }
  }

  @override
  Widget build(BuildContext context) {
    final dark = Theme.of(context).brightness == Brightness.dark;
    return Scaffold(
      appBar: AppBar(title: const Text('Cập nhật thông tin')),
      body: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              Center(
                child: Stack(
                  children: [
                    Obx(() {
                      final networkImage = controller.profile.value.avatar;
                      final image = networkImage.isNotEmpty ? networkImage : EImages.avt;
                      return ECircleImage(
                        height: 160,
                        width: 160,
                        image: image,
                        isNetworkImage: networkImage.isNotEmpty,
                      );
                    }),
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: IconButton(
                        icon: Icon(Icons.edit, color: dark ? Colors.white : Colors.black),
                        onPressed: _pickImage,
                      ),
                    )
                  ],
                ),
              ),
              const SizedBox(height: 20),
              TextFormField(
                controller: _firstName,
                decoration: const InputDecoration(labelText: 'First'),
                validator: (value) => value!.isEmpty ? 'Please enter First Name' : null,
              ),
              const SizedBox(height: ESizes.defaultBetweenItem),
              TextFormField(
                controller: _lastName,
                decoration: const InputDecoration(labelText: 'LastName'),
                validator: (value) => value!.isEmpty ? 'Please enter Last Name' : null,
              ),
              const SizedBox(height: ESizes.defaultBetweenItem),
              TextFormField(
                controller: _name,
                decoration: const InputDecoration(labelText: 'Username'),
                validator: (value) => value!.isEmpty ? 'Please enter User name' : null,
              ),
              const SizedBox(height: ESizes.defaultBetweenItem),
              TextFormField(
                controller: _phone,
                decoration: const InputDecoration(labelText: 'Phone number'),
                keyboardType: TextInputType.phone,
                validator: (value) => value!.isEmpty ? 'Please enter phone number' : null,
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _submit,
                child: const Text('Save'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
