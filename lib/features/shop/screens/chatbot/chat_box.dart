import 'package:flutter/material.dart';
import 'package:pet_shop/utils/constants/colors.dart';
import 'package:pet_shop/utils/helpers/helper_functions.dart';
import 'package:pet_shop/features/shop/controllers/chatbot/chatbot_controller.dart';

import '../../models/chatbot/chatbot_model.dart';

class ChatBoxScreen extends StatefulWidget {
  const ChatBoxScreen({super.key});

  @override
  State<ChatBoxScreen> createState() => _ChatBoxScreenState();
}

class _ChatBoxScreenState extends State<ChatBoxScreen> {
  final TextEditingController _controller = TextEditingController();
  final List<MessageModel> messages = [];
  late ChatController _chatController; // Declare ChatController instance
  bool _isLoading = false; // To show loading indicator for API calls
  final ScrollController _scrollController = ScrollController(); // To auto-scroll chat

  @override
  void initState() {
    super.initState();
    _chatController = ChatController(); // Initialize ChatController
    _fetchChatHistory(); // Fetch chat history when the screen initializes
  }

  /// Fetches historical chat messages from the backend.
  Future<void> _fetchChatHistory() async {
    setState(() {
      _isLoading = true; // Start loading
    });
    try {
      final history = await _chatController.fetchAllChat();
      setState(() {
        if (history.isEmpty) {
          // If no history, add a welcoming message from the bot
          messages.add(MessageModel(
            text: "Xin chào! Tôi có thể giúp gì cho bạn?",
            sender: "bot",
            timestamp: DateTime.now(),
            data: [],
          ));
        } else {
          messages.addAll(history);
        }
      });
      // Scroll to the bottom after loading messages
      _scrollToBottom();
    } catch (e) {
      // Show an error message to the user
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Lỗi tải lịch sử trò chuyện: ${e.toString()}')),
      );
    } finally {
      setState(() {
        _isLoading = false; // End loading
      });
    }
  }

  /// Sends the user's message and receives the bot's response.
  void sendMessage() async {
    final text = _controller.text.trim();
    if (text.isEmpty) {
      return; // Do nothing if message is empty
    }

    setState(() {
      messages.add(MessageModel(
        text: text,
        sender: "user",
        timestamp: DateTime.now(),
        data: [],
      ));
      _controller.clear();
      _isLoading = true;
    });
    _scrollToBottom(); // Scroll to show the new message

    try {
      // Call the postChat method from ChatController
      final botResponseText = await _chatController.postChat(text);
      setState(() {
        // Add bot's response to the list
        messages.add(MessageModel(
          text: botResponseText,
          sender: "bot",
          timestamp: DateTime.now(),
          data: [],
        ));
      });
      _scrollToBottom(); // Scroll to show the bot's response
    } catch (e) {
      setState(() {
        // Add an error message from the bot if sending fails
        messages.add(MessageModel(
          text: 'Lỗi: Không thể gửi tin nhắn hoặc nhận phản hồi.',
          sender: "bot",
          timestamp: DateTime.now(),
          data: [],
        ));
      });
      _scrollToBottom(); // Scroll to show the error message
      // Show a detailed error message to the user
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Không thể gửi tin nhắn: ${e.toString()}')),
      );
    } finally {
      setState(() {
        _isLoading = false; // Hide loading indicator
      });
    }
  }

  /// Scrolls the chat list to the bottom.
  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    _chatController.dispose(); // Dispose the ChatController
    _scrollController.dispose(); // Dispose the ScrollController
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final dark = EHelperFunctions.isDarkMode(context);

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Trò chuyện',
          style: Theme.of(context).textTheme.headlineMedium,
        ),
        centerTitle: true,
        backgroundColor: dark ? EColors.primaryColor : EColors.thirdColor,
      ),
      body: Column(
        children: [
          Expanded(
            child: _isLoading && messages.isEmpty // Show full screen loading only on initial fetch
                ? const Center(child: CircularProgressIndicator())
                : ListView.builder(
              controller: _scrollController, // Assign scroll controller
              padding: const EdgeInsets.all(12),
              itemCount: messages.length,
              itemBuilder: (context, index) {
                final msg = messages[index];
                final isUser = msg.sender == "user";

                return Align(
                  alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                  child: Container(
                    margin: const EdgeInsets.symmetric(vertical: 6),
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                    decoration: BoxDecoration(
                      color: isUser
                          ? (dark ? EColors.thirdColor : EColors.primaryColor)
                          : (dark ? EColors.primaryColor : EColors.thirdColor),
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Text(
                      msg.text,
                      style: Theme.of(context).textTheme.bodyLarge!.apply(
                        color: isUser
                            ? (dark ? EColors.primaryColor : EColors.thirdColor)
                            : (dark ? EColors.thirdColor : EColors.primaryColor),
                      )
                    ),
                  ),
                );
              },
            ),
          ),
          if (_isLoading && messages.isNotEmpty) // Loading indicator only when sending subsequent messages
            const Padding(
              padding: EdgeInsets.all(8.0),
              child: CircularProgressIndicator(),
            ),
          const Divider(height: 1),
          Container(
            color: dark ? EColors.primaryColor : EColors.thirdColor,
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: InputDecoration(
                      hintText: 'Nhập tin nhắn...',
                      hintStyle: Theme.of(context).textTheme.bodyLarge,
                      filled: true,
                      fillColor: dark ? EColors.primaryColor : EColors.thirdColor,
                      contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                      ),
                    ),
                    onSubmitted: _isLoading ? null : (_) => sendMessage(), // Allow sending by pressing enter
                  ),
                ),
                const SizedBox(width: 8),
                CircleAvatar(
                  backgroundColor: dark ? EColors.primaryColor : EColors.thirdColor,
                  child: IconButton(
                    icon: Icon(
                      Icons.send,
                      color: dark ? EColors.thirdColor : EColors.primaryColor,
                    ),
                    onPressed: _isLoading ? null : sendMessage, // Disable button while loading
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}