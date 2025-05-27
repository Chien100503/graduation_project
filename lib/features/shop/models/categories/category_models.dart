class CategoryModel {
  final int id;
  final String name;
  final String itemType;
  final String? imageUrl;

  CategoryModel({
    required this.id,
    required this.name,
    required this.itemType,
    this.imageUrl,
  });

  factory CategoryModel.fromJson(Map<String, dynamic> json) {
    return CategoryModel(
      id: json['id'],
      name: json['name'],
      itemType: (json['itemType'] ?? '').toString().toUpperCase(),
      imageUrl: json['imageUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'itemType': itemType,
      'imageUrl': imageUrl,
    };
  }

  bool get isPet => itemType == 'PET';
  bool get isProduct => itemType == 'PRODUCT';

  String get image => imageUrl ?? '';

  @override
  String toString() => 'CategoryModel(id: $id, name: $name, itemType: $itemType)';
}
