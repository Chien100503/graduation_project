

import 'package:get/get_navigation/src/routes/get_route.dart';
import 'package:pet_shop/routes/routes.dart';

import '../features/authen/screens/login/login.dart';

class AppRoutes {
  static final pages =[
    GetPage(name: Routes.signIn, page: () => const LoginScreen()),
    // GetPage(name: Routes.home, page: () => const HomeScreen()),
    // GetPage(name: Routes.store, page: () => const Store()),
    // GetPage(name: Routes.favorite, page: () => const WishList()),
    // GetPage(name: Routes.setting, page: () => const Setting()),
    // GetPage(name: Routes.userProfile, page: () => const Profile()),
    // GetPage(name: Routes.order, page: () => const OrderScreen()),
    // GetPage(name: Routes.productReviews, page: () => const ReviewAndRating()),
    // // GetPage(name: Routes.productDetail, page: () => const ProductDetail()),
    // GetPage(name: Routes.signup, page: () => const SignupScreen()),
    // GetPage(name: Routes.verifyEmail, page: () => const VerifyEmailScreen()),
    // GetPage(name: Routes.userAddress, page: () => const AddressScreen()),
    // GetPage(name: Routes.userAddress, page: () => const AddressScreen()),

  ];
}