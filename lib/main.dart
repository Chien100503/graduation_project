import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'app.dart';

Future<void> main() async {
  // Stripe.publishableKey = 'pk_test_51PgdLoGXQ1iVSXMTTne3imnV4qo7CYqw4NpNT2K67acJObmz0OVCGIY1ui63xuQoyqTztFOoQDaWHARRwVbE58cU00RSvk8kdx';
  // // Todo: Add Widgets Bindings
  final WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();

  // Todo: Init local storage
  await GetStorage.init();

  // Todo: Await Native Splash
  // FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);

  // Todo: Initialize Firebase


  runApp(const App());
}

