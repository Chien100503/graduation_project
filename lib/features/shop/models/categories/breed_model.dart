import 'package:pet_shop/features/shop/models/products/pet_detail_model.dart';

import '../products/pet_model.dart';

class BreedModel {
  final int id;
  final String name;
  final int petCategoryId;
  final List<PetDetailModel> pets;

  BreedModel({
    required this.id,
    required this.name,
    required this.petCategoryId,
    this.pets = const [], // Mặc định là rỗng nếu không có
  });

  factory BreedModel.fromJson(Map<String, dynamic> json) {
    // An toàn khi 'pets' có thể null
    List<dynamic> petsJson = json['pets'] ?? [];

    return BreedModel(
      id: json['id'],
      name: json['name'],
      petCategoryId: json['petCategory']?['id'] ?? 0,
      pets: petsJson.map((e) => PetDetailModel.fromJson(e)).toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'petCategoryId': petCategoryId,
      'pets': pets.map((e) => e.toJson()).toList(),
    };
  }

  @override
  String toString() {
    return 'BreedModel(id: $id, name: $name, petCategoryId: $petCategoryId, pets: $pets)';
  }
}
