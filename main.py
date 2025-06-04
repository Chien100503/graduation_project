import base64
from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI, HTTPException, Header, Depends, Body
from pydantic import BaseModel
from vector_store import init_vector_store
from chatbot import init_chatbot
from config import APP_HOST, APP_PORT, SECRET_KEY, ALGORITHM
import uvicorn
import jwt
from jwt import PyJWTError
import os
import re
import json
from mysql_connector import (
    get_mysql_connection, # Giữ lại nếu có các endpoint khác dùng MySQL
    get_or_create_conversation_firestore,
    append_message_to_conversation_firestore,
    initialize_firestore,
    fetch_user_by_email # Đã thêm import này
)
from ngrok_config import setup_ngrok
from datetime import datetime
from typing import List, Dict


app = FastAPI(title="Product Chatbot API")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Khởi tạo Firebase Firestore khi ứng dụng bắt đầu
initialize_firestore()

# Khởi tạo vector store và chatbot
vector_store = init_vector_store()
chatbot = init_chatbot()


class Query(BaseModel):
    text: str

def base64_decode_with_padding(s):
    """Thêm padding và decode chuỗi base64."""
    missing_padding = len(s) % 4
    if missing_padding != 0:
        s += '=' * (4 - missing_padding)
    return base64.b64decode(s)

try:
    DECODED_SECRET_KEY = base64_decode_with_padding(SECRET_KEY)
except Exception as e:
    print(f"Lỗi khi giải mã SECRET_KEY: {e}")
    DECODED_SECRET_KEY = b''


def verify_token(authorization: str = Header(None)):
    if authorization is None:
        raise HTTPException(
            status_code=401, detail="Authorization header bị thiếu")
    
    scheme, _, token = authorization.partition(" ")
    if scheme.lower() != "bearer":
        raise HTTPException(status_code=401, detail="Scheme xác thực không hợp lệ. Phải là Bearer.")
        
    try:
        payload = jwt.decode(token, DECODED_SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token đã hết hạn")
    except PyJWTError as e:
        raise HTTPException(status_code=401, detail=f"Token không hợp lệ: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Đã xảy ra lỗi không mong muốn trong quá trình xác thực token: {str(e)}")

    return payload


def build_context_from_history(messages: List[Dict], max_turns: int = 6) -> str:
    """
    Ghép các message gần nhất thành context cho chatbot, có đánh số thứ tự.
    """
    history = messages[-max_turns * 2:] if len(messages) > max_turns * 2 else messages
    context_lines = []
    start_idx = len(messages) - len(history) + 1
    for idx, msg in enumerate(history, start=start_idx):
        prefix = "Người dùng hỏi" if msg["sender"] == "user" else "Bot Trả lời"
        msg_text = msg.get("text", "")
        msg_data = msg.get("data", [])
        
        data_str = f" (IDs: {', '.join(map(str, msg_data))})" if msg_data else ""
        context_lines.append(f"{idx}. {prefix}: {msg_text}{data_str}")
    return "\n".join(context_lines)

@app.post("/chat")
async def chat(query: Query, token_payload: dict = Depends(verify_token)):
    try:
        email = token_payload.get("sub")
        if not email:
            raise HTTPException(
                status_code=400, detail="Token không chứa email người dùng (sub claim).")

        # Lấy thông tin người dùng từ MySQL bằng email để có được ID
        user = fetch_user_by_email(email)
        if not user:
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng trong database với email này.")
        
        user_id_for_firestore = str(user['id']) 

        conversation = get_or_create_conversation_firestore(user_id_for_firestore)
        messages = conversation.get("messages", [])

        history_context = build_context_from_history(messages)

        retrieval_results = vector_store.query(query.text)
        
        if not retrieval_results["documents"] or not retrieval_results["documents"][0]:
            append_message_to_conversation_firestore(user_id_for_firestore, "user", query.text)
            bot_response_text = "Xin lỗi, tôi không tìm thấy thông tin liên quan đến câu hỏi của bạn."
            append_message_to_conversation_firestore(user_id_for_firestore, "bot", bot_response_text, [])
            return {"response": bot_response_text, "data": [], "type": "none"}

        context = (
            history_context + "\n\n" + "\n\n".join(retrieval_results["documents"][0])
            if history_context else "\n\n".join(retrieval_results["documents"][0])
        )

        ai_result_raw = chatbot.generate_response(query.text, context)
        
        json_str = re.sub(r"^\s*```json|```\s*$", "", ai_result_raw, flags=re.MULTILINE).strip()

        try:
            result = json.loads(json_str)
            bot_text = result.get("response", ai_result_raw)
            data_ids = retrieval_results.get("ids", []) 
            response_type = retrieval_results.get("type", "none")
            
            result["data"] = data_ids
            result["type"] = response_type

        except json.JSONDecodeError:
            print(f"Cảnh báo: Phản hồi của AI không phải JSON hợp lệ. Phản hồi thô: {ai_result_raw}")
            result = {
                "response": "Xin lỗi, tôi gặp vấn đề khi xử lý phản hồi. Vui lòng thử lại.",
                "data": [],
                "type": "error"
            }
            bot_text = result["response"]
            data_ids = []
            response_type = "error"
        except Exception as e:
            print(f"Đã xảy ra lỗi không mong muốn trong quá trình xử lý phản hồi AI: {e}. Phản hồi thô: {ai_result_raw}")
            result = {
                "response": "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.",
                "data": [],
                "type": "error"
            }
            bot_text = result["response"]
            data_ids = []
            response_type = "error"

        append_message_to_conversation_firestore(user_id_for_firestore, "user", query.text)
        append_message_to_conversation_firestore(user_id_for_firestore, "bot", bot_text, data_ids)

        return result

    except HTTPException as he:
        raise he
    except Exception as e:
        print(f"Lỗi trong endpoint chat: {e}")
        raise HTTPException(status_code=500, detail=f"Lỗi máy chủ nội bộ: {str(e)}")


@app.get("/chat/history")
async def get_chat_history(token_payload: dict = Depends(verify_token)):
    try:
        email = token_payload.get("sub")
        if not email:
            raise HTTPException(
                status_code=400, detail="Token không chứa email người dùng (sub claim).")
        
        # Lấy thông tin người dùng từ MySQL bằng email để có được ID
        user = fetch_user_by_email(email)
        if not user:
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng trong database với email này.")
        
        # Sử dụng ID của người dùng từ MySQL làm user_id cho Firestore
        user_id_for_firestore = str(user['id'])

        conversation = get_or_create_conversation_firestore(user_id_for_firestore)
        
        messages = []
        for msg in conversation.get("messages", []):
            if isinstance(msg.get("timestamp"), datetime):
                msg["timestamp"] = msg["timestamp"].isoformat()
            messages.append(msg)

        # Trả về ID của người dùng thay vì email
        return {"user_id": user_id_for_firestore, "messages": messages}
    except HTTPException as he:
        raise he
    except Exception as e:
        print(f"Lỗi trong endpoint get_chat_history: {e}")
        raise HTTPException(status_code=500, detail=f"Lỗi máy chủ nội bộ: {str(e)}")


@app.get("/users/me")
async def get_current_user_info(token_payload: dict = Depends(verify_token)):
    try:
        email = token_payload.get("sub")
        if not email:
            raise HTTPException(
                status_code=400, detail="Token không chứa email người dùng (sub claim).")
        
        user_info = fetch_user_by_email(email)
        if not user_info:
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng với email này.")
        
        for key, value in user_info.items():
            if isinstance(value, datetime):
                user_info[key] = value.isoformat()

        return user_info
    except HTTPException as he:
        raise he
    except Exception as e:
        print(f"Lỗi trong endpoint get_current_user_info: {e}")
        raise HTTPException(status_code=500, detail=f"Lỗi máy chủ nội bộ: {str(e)}")


@app.post("/update")
async def add_or_update_document_endpoint(
    doc_data: dict = Body(...), 
    doc_type: str = Body(..., description="Loại tài liệu: 'product' hoặc 'pet'")
):
    """
    Endpoint để thêm hoặc cập nhật một tài liệu (sản phẩm/thú cưng) vào Vector Store.
    """
    if doc_type not in ["product", "pet"]:
        raise HTTPException(status_code=400, detail="Loại tài liệu không hợp lệ. Phải là 'product' hoặc 'pet'.")
    
    try:
        vector_store.add_or_update_document(doc_data, doc_type)
        return {"message": f"Cập nhật {doc_type} thành công!"}
    except ValueError as ve:
        raise HTTPException(status_code=400, detail=str(ve))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Lỗi khi cập nhật {doc_type}: {str(e)}")


if __name__ == "__main__":
    public_url = setup_ngrok()
    if public_url:
        print(f"FastAPI đang chạy trên: {public_url}")
    else:
        print(f"FastAPI đang chạy cục bộ trên: http://{APP_HOST}:{APP_PORT}")

    uvicorn.run(app, host=APP_HOST, port=APP_PORT)

