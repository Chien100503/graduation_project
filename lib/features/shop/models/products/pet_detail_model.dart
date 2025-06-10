import '../categories/breed_model.dart';

class PetDetailModel {
  final int id;
  final String petCategoryName;
  final String breedName;
  final String name;
  final int age;
  final String gender;
  final String size;
  final double weight;
  final String color;
  final double price;
  final double priceDiscount;
  final double percentDiscount;
  final bool status;
  final String thumbnailUrl;
  final String description;
  final List<String> imageUrls;
  final BreedModel breed;

  PetDetailModel({
    required this.breed,
    required this.priceDiscount,
    required this.percentDiscount,
    required this.id,
    required this.petCategoryName,
    required this.breedName,
    required this.name,
    required this.age,
    required this.gender,
    required this.size,
    required this.weight,
    required this.color,
    required this.price,
    required this.status,
    required this.description,
    required this.imageUrls,
    required this.thumbnailUrl,
  });

  factory PetDetailModel.fromJson(Map<String, dynamic> json) {
    return PetDetailModel(
      id: json['id'] ?? 0,
      thumbnailUrl: json['thumbnailUrl'] ?? '',
      petCategoryName: json['petCategoryName'] ?? '',
      breedName: json['breedName'] ?? '',
      name: json['name'] ?? '',
      age: json['age'] ?? 0,
      gender: json['gender'] ?? '',
      size: json['size'] ?? '',
      weight: (json['weight'] as num?)?.toDouble() ?? 0.0,
      color: json['color'] ?? '',
      price: (json['price'] as num?)?.toDouble() ?? 0.0,
      status: json['status'] ?? false,
      description: json['description'] ?? '',
      imageUrls: (json['imageUrls'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
      priceDiscount: (json['priceDiscount'] as num?)?.toDouble() ?? 0.0,
      percentDiscount: (json['percentDiscount'] as num?)?.toDouble() ?? 0.0,
      breed: json['breed'] != null
          ? BreedModel.fromJson(json['breed'])
          : BreedModel(id: 0, name: 'Unknown', petCategoryId: 0),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'petCategoryName': petCategoryName,
      'breedName': breedName,
      'name': name,
      'age': age,
      'gender': gender,
      'size': size,
      'weight': weight,
      'color': color,
      'price': price,
      'status': status,
      'description': description,
      'imageUrls': imageUrls,
      'thumbnailUrl': thumbnailUrl,
      'priceDiscount': priceDiscount,
      'percentDiscount': percentDiscount,
      'breed': breed.toJson(),
    };
  }

  @override
  String toString() {
    return 'PetDetailModel(id: $id, name: $name, breed: $breedName, category: $petCategoryName)';
  }
}
