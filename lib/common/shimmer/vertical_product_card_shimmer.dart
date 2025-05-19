import 'package:pet_shop/common/shimmer/shimmer.dart';

import '../../utils/constants/sizes.dart';
import 'package:flutter/material.dart';

import '../layouts/grid_layout.dart';

class EVerticalProductCardShimmer extends StatelessWidget {
  const EVerticalProductCardShimmer({super.key, this.itemCount = 4});

  final int itemCount;

  @override
  Widget build(BuildContext context) {
    return EGridProductLayout(
        itemCount: itemCount,
        itemBuilder: (_, __) => const SizedBox(
              width: 180,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  EShimmerEffect(width: 180, height: 180, radius: 15),
                  SizedBox(height: ESizes.defaultBetweenItem),
                  EShimmerEffect(width: 150, height: 15, radius: 15),
                  SizedBox(height: ESizes.defaultBetweenItem / 2),
                  EShimmerEffect(width: 110, height: 15, radius: 15),
                ],
              ),
            ));
  }
}
