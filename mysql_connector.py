import os
import mysql.connector
import json
import firebase_admin
from firebase_admin import credentials, firestore
from dotenv import load_dotenv
from datetime import datetime
from decimal import Decimal

load_dotenv()

MYSQL_HOST = os.getenv("MYSQL_HOST")
MYSQL_PORT = int(os.getenv("MYSQL_PORT")) 
MYSQL_USER = os.getenv("MYSQL_USER")
MYSQL_PASSWORD = os.getenv("MYSQL_PASSWORD")
MYSQL_DATABASE = os.getenv("MYSQL_DATABASE")
MYSQL_USE_SSL_DISABLED = os.getenv("MYSQL_USE_SSL") 

def get_mysql_connection():
    """Tạo và trả về kết nối tới cơ sở dữ liệu MySQL."""
    return mysql.connector.connect(
        host=MYSQL_HOST,
        port=MYSQL_PORT,
        user=MYSQL_USER,
        password=MYSQL_PASSWORD,
        database=MYSQL_DATABASE,
        ssl_disabled=MYSQL_USE_SSL_DISABLED
    )

def fetch_products():
    """
    Lấy dữ liệu sản phẩm từ MySQL, bao gồm thông tin danh mục, hãng, loại.
    Chỉ lấy các trường cần thiết cho Vector Store.
    """
    query = """
    SELECT
        p.id, p.name, p.description, p.price, p.weight, p.stock_quantity, p.size, p.expiration_date,
        pc.name AS category_name,
        b.name AS brand_name,
        t.name AS type_name
    FROM products p
    LEFT JOIN product_categories pc ON p.pr_category_id = pc.id
    LEFT JOIN brands b ON p.brand_id = b.id
    LEFT JOIN types t ON p.type_id = t.id
    """
    conn = get_mysql_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query)
    results = cursor.fetchall()
    cursor.close()
    conn.close()
    return results

def fetch_pets():
    """
    Lấy dữ liệu thú cưng từ MySQL, bao gồm thông tin danh mục thú cưng và giống.
    Chỉ lấy các trường cần thiết cho Vector Store.
    """
    query = """
    SELECT
        pt.id, pt.name, pt.age, pt.gender, pt.size, pt.weight, pt.color, pt.price, pt.status, pt.description,
        pc.name AS category_name,
        b.name AS breed_name
    FROM pets pt
    LEFT JOIN pet_categories pc ON pt.pet_category_id = pc.id
    LEFT JOIN breeds b ON pt.breed_id = b.id
    """
    conn = get_mysql_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query)
    results = cursor.fetchall()
    cursor.close()
    conn.close()
    return results

def fetch_user_by_email(email: str) -> dict:
    query = "SELECT id, name, email, avatar FROM users WHERE email = %s"
    conn = get_mysql_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, (email,))
    user = cursor.fetchone()
    cursor.close()
    conn.close()
    return user

_firestore_db = None

def initialize_firestore():
    """Khởi tạo Firebase Admin SDK nếu chưa được khởi tạo."""
    global _firestore_db
    if _firestore_db is None:
        try:
            if not firebase_admin._apps:
                cred_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH", "serviceAccountKey.json")
                if not os.path.exists(cred_path):
                    raise FileNotFoundError(f"Firebase service account key not found at {cred_path}")
                
                cred = credentials.Certificate(cred_path)
                firebase_admin.initialize_app(cred)
            _firestore_db = firestore.client()
            print("Firebase Firestore initialized successfully.")
        except Exception as e:
            print(f"Error initializing Firebase Firestore: {e}")
            raise

def get_firestore_db():
    """Trả về đối tượng Firestore client."""
    if _firestore_db is None:
        initialize_firestore()
    return _firestore_db

def get_or_create_conversation_firestore(user_id: str) -> dict:
    """
    Lấy hoặc tạo tài liệu cuộc trò chuyện cho người dùng.
    Sử dụng user_id (email từ token sub) làm ID tài liệu Firestore.
    """
    db = get_firestore_db()
    doc_ref = db.collection("conversations").document(user_id)
    doc = doc_ref.get()

    if doc.exists:
        return doc.to_dict()
    else:
        now = datetime.utcnow()
        convo_doc = {
            "user_id": user_id,
            "messages": [],
            "created_at": now,
            "updated_at": now
        }
        doc_ref.set(convo_doc)
        return convo_doc

def append_message_to_conversation_firestore(user_id: str, sender: str, text: str, data: list = None):
    """
    Thêm một tin nhắn vào lịch sử cuộc trò chuyện của người dùng.
    """
    db = get_firestore_db()
    doc_ref = db.collection("conversations").document(user_id)
    
    if data is None:
        data = []

    message = {
        "sender": sender,
        "text": text,
        "data": data,
        "timestamp": datetime.utcnow()
    }

    doc_ref.update({
        "messages": firestore.ArrayUnion([message]),
        "updated_at": datetime.utcnow()
    })

def clean_metadata(obj):
    """
    Đệ quy làm sạch các giá trị None trong dictionary/list.
    ChromaDB không cho phép giá trị None trong metadata.
    """
    if isinstance(obj, dict):
        return {k: clean_metadata(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [clean_metadata(i) for i in obj]
    elif obj is None:
        return "unknown"
    else:
        return obj

def convert_decimal_to_float(obj):
    """
    Đệ quy chuyển đổi các đối tượng Decimal sang float.
    Firestore và JSON không hỗ trợ Decimal trực tiếp.
    """
    if isinstance(obj, dict):
        return {k: convert_decimal_to_float(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [convert_decimal_to_float(i) for i in obj]
    elif isinstance(obj, Decimal):
        return float(obj)
    else:
        return obj

if __name__ == "__main__":
    print("Testing MySQL connection and data fetching...")
    try:
        products_data = fetch_products()
        print(f"Fetched {len(products_data)} products.")

        pets_data = fetch_pets()
        print(f"Fetched {len(pets_data)} pets.")

        os.makedirs("./data", exist_ok=True)
        with open("./data/products_raw.json", "w", encoding="utf-8") as f:
            json.dump(products_data, f, ensure_ascii=False, indent=2, default=str)
        with open("./data/pets_raw.json", "w", encoding="utf-8") as f:
            json.dump(pets_data, f, ensure_ascii=False, indent=2, default=str)

        print("\nTesting Firestore initialization and conversation functions...")
        initialize_firestore()
        test_user_id = "test_user_123@example.com"
        convo = get_or_create_conversation_firestore(test_user_id)
        print(f"Conversation for {test_user_id}: {convo}")

        append_message_to_conversation_firestore(test_user_id, "user", "Chào bạn, tôi muốn mua một sản phẩm.")
        append_message_to_conversation_firestore(test_user_id, "bot", "Chào bạn, tôi có thể giúp gì cho bạn?")
        
        updated_convo = get_or_create_conversation_firestore(test_user_id)
        print(f"Updated conversation: {updated_convo}")

        print("\nTesting fetch_user_by_email...")
        test_email = "your_test_user@example.com" 
        user_info = fetch_user_by_email(test_email)
        print(f"User info for {test_email}: {user_info}")

    except Exception as e:
        print(f"An error occurred during testing: {e}")

