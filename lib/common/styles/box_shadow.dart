import 'package:flutter/cupertino.dart';

import '../../utils/constants/colors.dart';

class EBoxShadow {
  static final verticalProductBoxShadow = BoxShadow(
    color: EColors.darkGrey.withOpacity(0.2),
    blurRadius: 50,
    offset: const Offset(0, 2),
    spreadRadius: 7,
  );

  static final horizontalProductBoxShadow = BoxShadow(
    color: EColors.darkGrey.withOpacity(0.2),
    blurRadius: 50,
    offset: const Offset(0, 2),
    spreadRadius: 7,
  );
}
