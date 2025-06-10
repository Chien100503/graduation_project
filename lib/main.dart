import 'package:flutter/material.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:get_storage/get_storage.dart';
import 'app.dart';

void main() async {
  final WidgetsBinding widgetsBinding = WidgetsFlutterBinding.ensureInitialized();

  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);

  await GetStorage.init();

  final storage = GetStorage();
  final isFirstTime = storage.read('isFirstTime') ?? true;
  final token = storage.read('TOKEN');

  runApp(App(
    isFirstTime: isFirstTime,
    isLoggedIn: token != null && token != '',
  ));

  FlutterNativeSplash.remove();
}