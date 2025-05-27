class TypeModel {
  final int id;
  final String name;
  final int productCategoryId;

  TypeModel({
    required this.id,
    required this.name,
    required this.productCategoryId,
  });

  factory TypeModel.fromJson(Map<String, dynamic> json) {
    return TypeModel(
      id: json['id'],
      name: json['name'],
      productCategoryId: json['productCategory']?['id'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'productCategoryId': productCategoryId,
    };
  }

  @override
  String toString() => 'TypeModel(id: $id, name: $name, productCategoryId: $productCategoryId)';
}
