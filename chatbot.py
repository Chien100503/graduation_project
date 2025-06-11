import google.generativeai as genai
import json
from config import GOOGLE_API_KEY, GEMINI_MODEL


class Chatbot:
    def __init__(self):
        genai.configure(api_key=GOOGLE_API_KEY)
        self.model = genai.GenerativeModel(GEMINI_MODEL)

    def generate_response(self, query: str, context: str) -> str:
        prompt = f"""
        Dựa trên thông tin sản phẩm và thú cưng sau:
        {context}
        
        Hãy trả lời câu hỏi sau bằng tiếng Việt:
        {query}
        
        Yêu cầu:
        - Trả về kết quả dưới dạng một object JSON với các trường:
            - response: nội dung trả lời cho người dùng (trả lời thân thiện bằng tiếng Việt, ngắn gọn, dễ hiểu, nếu câu hỏi không liên quan tới sản phẩm/thú cưng thì hãy trả lời thân thiện sau đó hỏi người dùng về dịch vụ sản phẩm/thú cưng. Lưu ý không được trả về những thông tin nhạy cảm, không cần thiết ở phần này như uuid, user_id,...)
            - data: mảng các ID sản phẩm hoặc thú cưng (bắt buộc phải trả về ở phần này, không được trả về ID ở phần response)
            - type: loại thông tin trả về, ví dụ: "product", "pet", "none" (nếu không tìm thấy thông tin liên quan hoặc câu hỏi không liên quan)
        - Ví dụ trả về sản phẩm:
        {{
          "response": "Sản phẩm A có giá 100.000 VNĐ và sản phẩm B có giá 200.000 VNĐ",
          "data": [1, 2],
          "type": "product"
        }}
        - Ví dụ trả về thú cưng:
        {{
          "response": "Thú cưng Labrador có giá 5.000.000 VNĐ và Poodle có giá 7.000.000 VNĐ",
          "data": [101, 102],
          "type": "pet"
        }}
        - Nếu không có thông tin liên quan, hãy trả về câu trả lời không tìm thấy hoặc trả lời câu hỏi không liên quan đó:
        - Ví dụ không tìm thấy:
        {{
          "response": "Xin lỗi, tôi không tìm thấy thông tin phù hợp về sản phẩm hoặc thú cưng bạn yêu cầu.",
          "data": [],
          "type": "none"
        }}
        - Ví dụ câu hỏi không liên quan:
        {{
          "response": "Chào bạn, tôi là trợ lý AI về sản phẩm và thú cưng. Bạn có câu hỏi nào về các sản phẩm hoặc thú cưng của chúng tôi không?",
          "data": [],
          "type": "none"
        }}
        Chỉ trả về object JSON, không trả thêm bất kỳ nội dung nào khác.
        """

        response = self.model.generate_content(prompt)
        return response.text


def init_chatbot():
    """Khởi tạo đối tượng Chatbot."""
    return Chatbot()