import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/features/authen/screens/login/widgets/login_divider.dart';
import 'package:pet_shop/features/checkout/screen/payment/widget/payment_success.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/constants/sizes.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'package:pretty_qr_code/pretty_qr_code.dart';
import 'package:url_launcher/url_launcher.dart';

class PaymentQRCodeScreen extends StatelessWidget {
  final String qrData;
  final String checkoutUrl;

  const PaymentQRCodeScreen({
    super.key,
    required this.qrData,
    required this.checkoutUrl,
  });

  Future<void> _launchUrl() async {
    final Uri url = Uri.parse(checkoutUrl);
    if (!await launchUrl(url, mode: LaunchMode.externalApplication)) {
      Get.snackbar('Error', 'Could not open the link');
    }
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Payment QR Code'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(ESizes.defaultSpace),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Scan this QR code to pay',
                style: TextStyle(fontSize: 18),
              ),
              const SizedBox(height: 20),
              PrettyQrView.data(
                data: qrData,
                decoration: PrettyQrDecoration(
                  shape: PrettyQrSmoothSymbol(
                      color: dark ? EColors.thirdColor : EColors.primaryColor),
                  image: const PrettyQrDecorationImage(
                    image: AssetImage('assets/logos/logo_light.png'),
                  ),
                  quietZone: PrettyQrQuietZone.standart,
                ),
              ),
              const Divider(),
              SizedBox(
                width: double.infinity,
                child: TextButton(
                  child: Text(
                    'Xem chi tiết đơn hàng',
                    style: Theme.of(context).textTheme.titleLarge!.apply(
                        decoration: TextDecoration.underline,
                        decorationColor: dark ? Colors.white : Colors.black,
                        decorationStyle: TextDecorationStyle.wavy,),

                  ),
                  onPressed: _launchUrl, // mở link checkoutUrl
                ),
              ),
              const SizedBox(height: 20),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  child: Text('Payment Completed'),
                  onPressed: () {
                    Get.to(PaymentSuccess());
                  },
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
