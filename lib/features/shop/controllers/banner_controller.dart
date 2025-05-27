import 'package:get/get.dart';

import '../../../common/widgets/loader/loader.dart';
import '../../../data/repositories/banner_repository/banner_repository.dart';
import '../../../utils/popups/full_screen_loader.dart';
import '../models/banners/banner_model.dart';

class BannerController extends GetxController {

  final isLoad = false.obs;
  final carousalCurrentIndex = 0.obs;
  final RxList<BannerModel> banners = <BannerModel>[].obs;


  @override
  void onInit() {
    super.onInit();
    fetchBanner();
  }

  void updatePageIndicator(index) {
    carousalCurrentIndex.value = index;
  }

  Future<void> fetchBanner() async {
    try {
      //Loader
      isLoad.value = true;

      final bannerRepo = Get.put(BannerRepository());
      final banners = await bannerRepo.fetchAllBanner();

      this.banners.assignAll(banners);

    } catch (e) {
      EFullScreenLoader.stopLoading();
      ECustomSnackBar.showError(title: 'Oh snap', message: e.toString());
      print('Banner error ${e.toString()}');
    } finally {
      isLoad.value = false;
    }
  }

}