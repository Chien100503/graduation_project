class CartItemModel {
  final int id;
  final int? productId;
  final String? productName;
  final int? petId;
  final String? petName;
  final String? thumbnailUrl;
  final String? itemType;
  final String? breedName;
  final String? brandName;
  final double price;
  final double priceDiscount;
  final int quantity;
  final double itemTotalPrice;

  CartItemModel({
    required this.id,
    this.productId,
    this.productName,
    this.petId,
    this.petName,
    this.thumbnailUrl,
    this.itemType,
    this.breedName,
    this.brandName,
    required this.price,
    required this.priceDiscount,
    required this.quantity,
    required this.itemTotalPrice,
  });

  factory CartItemModel.fromJson(Map<String, dynamic> json) {
    return CartItemModel(
      id: json['id'] ?? 0,
      productId: json['productId'],
      productName: json['productName'],
      petId: json['petId'],
      petName: json['petName'],
      thumbnailUrl: json['thumbnailUrl'],
      itemType: json['itemType'],
      breedName: json['breedName'],
      brandName: json['brandName'],
      price: (json['price'] as num?)?.toDouble() ?? 0.0,
      priceDiscount: (json['priceDiscount'] as num?)?.toDouble() ?? 0.0,
      quantity: json['quantity'] ?? 1, // fallback nếu API không gửi field này
      itemTotalPrice: (json['itemTotalPrice'] as num?)?.toDouble() ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'productId': productId,
      'productName': productName,
      'petId': petId,
      'petName': petName,
      'thumbnailUrl': thumbnailUrl,
      'itemType': itemType,
      'breedName': breedName,
      'brandName': brandName,
      'price': price,
      'priceDiscount': priceDiscount,
      'quantity': quantity,
      'itemTotalPrice': itemTotalPrice,
    };
  }

  CartItemModel copyWith({
    int? id,
    int? productId,
    double? price,
    double? priceDiscount,
    int? quantity,
    double? itemTotalPrice,
  }) {
    final newPrice = price ?? this.price;
    final newQuantity = quantity ?? this.quantity;
    return CartItemModel(
      id: id ?? this.id,
      productId: productId ?? this.productId,
      productName: productName,
      petId: petId,
      petName: petName,
      thumbnailUrl: thumbnailUrl,
      itemType: itemType,
      breedName: breedName,
      brandName: brandName,
      price: newPrice,
      priceDiscount: priceDiscount ?? this.priceDiscount,
      quantity: newQuantity,
      itemTotalPrice: itemTotalPrice ?? newPrice * newQuantity,
    );
  }
}
