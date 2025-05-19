
import 'package:flutter/material.dart';

import '../../../../utils/constants/colors.dart';
import '../../../../utils/constants/sizes.dart';
import '../../../../utils/helpers/helper_functions.dart';

class ERoundContainer extends StatelessWidget {
  const ERoundContainer({
    super.key,
    this.width,
    this.height,
    this.radius = ESizes.cardRadiusLg,
    this.child,
    this.showBorder = false,
    this.bg = Colors.white,
    this.borderColor = EColors.primaryColor,
    this.padding,
    this.margin,
  });

  final double? width;
  final double? height;
  final double radius;
  final Widget? child;
  final bool showBorder;
  final Color bg;final Color borderColor;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return Container(
      width: width,
      height: height,
      padding: padding,
      margin: margin,
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(radius),
        border: dark ? showBorder ? Border.all(color: EColors.thirdColor) : null : showBorder ? Border.all(color: EColors.primaryColor) :null
      ),
      child: child,
    );
  }
}
