import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pet_shop/common/widgets/appbar/appbar.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/constants/sizes.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'chat_box.dart';

class ChatHistoryScreen extends StatelessWidget {
  const ChatHistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);
    final List<String> histories = [
      'Hỏi về sản phẩm A',
      'Tư vấn giao hàng',
      'Đổi trả hàng',
    ];

    return Scaffold(
      appBar: EAppBar(
        showBackArrow: true,
        title: Text('Chat-bot AI', style: Theme.of(context).textTheme.headlineMedium,),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            ElevatedButton.icon(
              onPressed: () => Get.to(() => const ChatBoxScreen()),
              icon: const Icon(Icons.add_comment),
              label: const Text('Bắt đầu cuộc trò chuyện mới'),
              style: ElevatedButton.styleFrom(
                backgroundColor: dark ? EColors.thirdColor : EColors.primaryColor,
                foregroundColor: dark ? EColors.primaryColor : EColors.thirdColor,
                padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(ESizes.borderRadiusLg),
                ),
              ),
            ),
            const SizedBox(height: 24),
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                'Lịch sử cuộc trò chuyện',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: ListView.separated(
                itemCount: histories.length,
                separatorBuilder: (_, __) => const Divider(),
                itemBuilder: (context, index) {
                  return ListTile(
                    leading: const Icon(Icons.history, color: Colors.deepPurple),
                    title: Text(histories[index]),
                    trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                    onTap: () => Get.to(() => const ChatBoxScreen()),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    tileColor: dark ? EColors.primaryColor : EColors.thirdColor,
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
