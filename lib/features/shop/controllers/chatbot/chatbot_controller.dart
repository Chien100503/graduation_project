import 'package:flutter/material.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import '../../models/chatbot/chatbot_model.dart';

class ChatController extends ChangeNotifier {
  final _storage = GetStorage();
  final String _chatHost = 'https://handy-cheaply-wombat.ngrok-free.app';

  Future<List<MessageModel>> fetchAllChat() async {
    final token = _storage.read('TOKEN');
    if (token == null) {
      throw Exception('Token không tồn tại. Vui lòng đăng nhập lại.');
    }

    try {
      final response = await http.get(
        Uri.parse('$_chatHost/chat/history'),
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );

      print('fetchAllChat - Status Code: ${response.statusCode}');
      print('fetchAllChat - Response Body: ${response.body}');


      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);

        List<dynamic> chatHistoryJson = [];

        if (responseData.containsKey('messages') && responseData['messages'] is List) {
          chatHistoryJson = responseData['messages'] as List<dynamic>;
        }
        else {
          throw Exception('Cấu trúc phản hồi API không mong muốn: Dự kiến danh sách tin nhắn chat dưới key "messages".');
        }

        return chatHistoryJson
            .map((json) => MessageModel.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception(
            'Failed to load chat history: HTTP ${response.statusCode}, ${response.body}');
      }
    } catch (e) {
      print('Error fetching chat history: $e');
      throw Exception('Lỗi khi tải lịch sử trò chuyện: $e');
    }
  }

  Future<String> postChat(String messageText) async {
    final token = _storage.read('TOKEN');
    if (token == null) {
      throw Exception('Token không tồn tại. Vui lòng đăng nhập lại.');
    }

    try {
      final response = await http.post(
        Uri.parse('$_chatHost/chat'),
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({'text': messageText}),
      );

      print('postChat - Status Code: ${response.statusCode}');
      print('postChat - Response Body: ${response.body}');

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(response.body);
        return data['response'] ?? 'Không có phản hồi từ bot.';
      } else {
        throw Exception(
            'Failed to post message: HTTP ${response.statusCode}, ${response.body}');
      }
    } catch (e) {
      print('Error posting chat message: $e');
      throw Exception('Lỗi khi gửi tin nhắn: $e');
    }
  }
}
