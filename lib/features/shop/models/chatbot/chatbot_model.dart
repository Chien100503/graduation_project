class MessageModel {
  final String text;
  final String sender; // "user" or "bot"
  final DateTime timestamp;
  final List<int> data; // Can be used for raw data, e.g., image bytes

  MessageModel({
    required this.text,
    required this.sender,
    required this.timestamp,
    required this.data,
  });

  factory MessageModel.fromJson(Map<String, dynamic> json) {
    return MessageModel(
      text: json['text'] as String,
      sender: json['sender'] as String,
      timestamp: json['timestamp'] != null
          ? DateTime.parse(json['timestamp'] as String)
          : DateTime.now(),
      data: json['data'] != null
          ? List<int>.from(json['data'] as List)
          : [], // Default to empty list if data is missing
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'text': text,
      'sender': sender,
      'timestamp': timestamp.toIso8601String(),
      'data': data,
    };
  }

  @override
  String toString() {
    return 'MessageModel(text: $text, sender: $sender, timestamp: $timestamp, data: $data)';
  }
}
