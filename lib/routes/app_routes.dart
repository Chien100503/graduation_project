import 'package:get/get.dart';
import 'package:pet_shop/features/shop/screens/stores/store.dart';
import 'package:pet_shop/features/shop/screens/wishlist/wishlist.dart';
import 'package:pet_shop/routes/routes.dart';

import '../features/authen/screens/login/login.dart';
import '../features/authen/screens/signup/signup.dart';
import '../features/personalizations/screens/addresses/address.dart';
import '../features/personalizations/screens/profile/profile.dart';
import '../features/shop/screens/home/home.dart';
import '../features/shop/screens/settings/setting.dart';


class AppRoutes {
  static final pages =[
    GetPage(name: Routes.home, page: () => const HomeScreen()),
    GetPage(name: Routes.store, page: () => const StoreScreen()),
    GetPage(name: Routes.favorite, page: () => const WishListScreen()),
    GetPage(name: Routes.setting, page: () => const Setting()),
    GetPage(name: Routes.userProfile, page: () => const Profile()),
    // GetPage(name: Routes.order, page: () => const OrderScreen()),
    // GetPage(name: Routes.productReviews, page: () => const ReviewAndRating()),
    // GetPage(name: Routes.productDetail, page: () => const ProductDetail()),
    GetPage(name: Routes.signIn, page: () => const LoginScreen()),
    GetPage(name: Routes.signup, page: () => const SignupScreen()),
    GetPage(name: Routes.userAddress, page: () => const AddressScreen()),

  ];
}