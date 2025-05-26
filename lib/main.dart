import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'app.dart';

Future<void> main() async {
  // // Todo: Add Widgets Bindings
  final WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();

  // Todo: Init local storage
  await GetStorage.init();

  // Todo: Await Native Splash
  // FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);
  final storage = GetStorage();
  final isFirstTime = storage.read('isFirstTime') ?? true;


  runApp(App(isFirstTime: isFirstTime));
}

