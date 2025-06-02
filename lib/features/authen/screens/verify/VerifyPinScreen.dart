import 'dart:async';
import 'package:flutter/material.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'package:pinput/pinput.dart';
import '../../../../utils/constants/colors.dart';
import '../../controllers/signup/signup_controller.dart';

class VerifyPinScreen extends StatefulWidget {
  final String email;

  const VerifyPinScreen({super.key, required this.email});

  @override
  State<VerifyPinScreen> createState() => _VerifyPinScreenState();
}

class _VerifyPinScreenState extends State<VerifyPinScreen> {
  final TextEditingController pinController = TextEditingController();
  Timer? _timer;
  int _secondsLeft = 180;
  bool _canResend = false;

  @override
  void initState() {
    super.initState();
    _startCountdown();
  }

  void _startCountdown() {
    _secondsLeft = 180;
    _canResend = false;
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_secondsLeft > 0) {
        setState(() {
          _secondsLeft--;
        });
      } else {
        timer.cancel();
        setState(() {
          _canResend = true;
        });
      }
    });
  }

  String _formatTime(int seconds) {
    final minutes = (seconds ~/ 60).toString().padLeft(2, '0');
    final secs = (seconds % 60).toString().padLeft(2, '0');
    return '$minutes:$secs';
  }

  void _verifyPin(String pin) {
    SignupController.instance.verifyPin(pin);
  }

  void _resendCode() async {
    await SignupController.instance.resendCode();
    _startCountdown();
  }

  @override
  void dispose() {
    _timer?.cancel();
    pinController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    return Scaffold(
      appBar: EAppBar(
        title: Text(
          'Verify PIN code',
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.headlineMedium,
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Enter the PIN code that has been sent to your email.',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 18),
            ),
            const SizedBox(height: 32),
            Pinput(
              length: 6,
              controller: pinController,
              onCompleted: _verifyPin,
              defaultPinTheme: PinTheme(
                width: 56,
                height: 56,
                textStyle: Theme.of(context).textTheme.titleLarge,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                      color: dark ? EColors.thirdColor : EColors.primaryColor),
                ),
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: () => _verifyPin(pinController.text),
              child: const Padding(
                padding: EdgeInsets.symmetric(horizontal: 15),
                child: Text('Submit'),
              ),
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  _canResend
                      ? 'Bạn có thể gửi lại mã.'
                      : 'Gửi lại mã sau: ${_formatTime(_secondsLeft)}',
                  style: const TextStyle(fontSize: 16),
                ),
                // Show IconButton only when _canResend is true
                if (_canResend)
                  IconButton(
                    icon: const Icon(Icons.refresh),
                    tooltip: 'Gửi lại mã',
                    onPressed: _resendCode,
                  ),
              ],
            )
          ],
        ),
      ),
    );
  }
}