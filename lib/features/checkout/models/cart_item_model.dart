class CartItemModel {
  final int id;
  final int? productId;
  final String? productName;
  final int? petId;
  final String? petName;
  final double price;
  final int quantity;
  final double itemTotalPrice;

  CartItemModel({
    required this.id,
    this.productId,
    this.productName,
    this.petId,
    this.petName,
    required this.price,
    required this.quantity,
    required this.itemTotalPrice,
  });

  factory CartItemModel.fromJson(Map<String, dynamic> json) {
    return CartItemModel(
      id: json['id'],
      productId: json['productId'],
      productName: json['productName'],
      petId: json['petId'],
      petName: json['petName'],
      price: (json['price'] as num).toDouble(),
      quantity: json['quantity'],
      itemTotalPrice: (json['itemTotalPrice'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'productId': productId,
      'productName': productName,
      'petId': petId,
      'petName': petName,
      'price': price,
      'quantity': quantity,
      'itemTotalPrice': itemTotalPrice,
    };
  }

  CartItemModel copyWith({
    int? id,
    int? productId,
    double? price,
    int? quantity,
    // Thêm các field khác nếu có
  }) {
    final newPrice = price ?? this.price;
    final newQuantity = quantity ?? this.quantity;
    return CartItemModel(
      id: id ?? this.id,
      productId: productId ?? this.productId,
      price: newPrice,
      quantity: newQuantity,
      itemTotalPrice: newPrice * newQuantity,
    );
  }

}
