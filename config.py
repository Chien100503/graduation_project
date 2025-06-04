import os
from dotenv import load_dotenv

load_dotenv()

GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

GEMINI_MODEL = "gemini-2.0-flash"
SENTENCE_TRANSFORMER_MODEL_PATH = "./models/archive"

APP_HOST = "127.0.0.1"
APP_PORT = 8000

MYSQL_HOST=os.getenv("MYSQL_HOST")
MYSQL_PORT=os.getenv("MYSQL_PORT")
MYSQL_USER=os.getenv("MYSQL_USER")
MYSQL_PASSWORD=os.getenv("MYSQL_PASSWORD")
MYSQL_DATABASE=os.getenv("MYSQL_DATABASE")
MYSQL_USE_SSL=os.getenv("MYSQL_USE_SSL")

SECRET_KEY = os.getenv("SECRET_KEY")
ALGORITHM = os.getenv("ALGORITHM")

FIREBASE_SERVICE_ACCOUNT_KEY_PATH = os.getenv("FIREBASE_SERVICE_ACCOUNT_KEY_PATH", "serviceAccountKey.json")

NGROK_AUTH_TOKEN = os.getenv("NGROK_AUTH_TOKEN")
NGROK_STATIC_DOMAIN = os.getenv("NGROK_STATIC_DOMAIN")