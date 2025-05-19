import 'package:pet_shop/common/shimmer/shimmer.dart';
import 'package:flutter/material.dart';

import '../layouts/grid_layout.dart';

class ShimmerBrand extends StatelessWidget {
  const ShimmerBrand({super.key, this.count = 4});

  final int count;

  @override
  Widget build(BuildContext context) {
    return EGridProductLayout(itemCount: count, itemBuilder: (_, __) => const EShimmerEffect(width: 300, height: 80, radius: 15));
  }
}
