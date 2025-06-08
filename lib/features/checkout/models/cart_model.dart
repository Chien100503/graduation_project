import 'cart_item_model.dart';

class CartModel {
  final int id;
  final List<CartItemModel> items;
  final double totalPrice;

  CartModel({
    required this.id,
    required this.items,
    required this.totalPrice,
  });

  factory CartModel.fromJson(Map<String, dynamic> json) {
    return CartModel(
      id: json['id'],
      items: List<CartItemModel>.from(
        json['items'].map((item) => CartItemModel.fromJson(item)),
      ),
      totalPrice: (json['totalPrice'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'items': items.map((e) => e.toJson()).toList(),
      'totalPrice': totalPrice,
    };
  }
}
