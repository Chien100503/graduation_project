import 'package:pet_shop/features/shop/models/categories/breed_model.dart';

class PetModel {
  final int id;
  final String name;
  final double price;
  final BreedModel breed;
  final String imageUrl;

  PetModel({
    required this.id,
    required this.name,
    required this.price,
    required this.imageUrl,
    required this.breed,
  });

  factory PetModel.fromJson(Map<String, dynamic> json) {
    return PetModel(
      id: json['id'],
      name: json['name'],
      price: (json['price'] as num).toDouble(), // đảm bảo là double
      imageUrl: json['imageUrl'],
      breed: BreedModel.fromJson(json['breed']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'price': price,
      'imageUrl': imageUrl,
      'breed': {
        'id': breed.id,
        'name': breed.name,
      }
    };
  }

  @override
  String toString() {
    return 'PetModel(id: $id, name: $name, price: $price, imageUrl: $imageUrl)';
  }
}
