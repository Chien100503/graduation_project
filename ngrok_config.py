import os
from pyngrok import ngrok
from config import NGROK_AUTH_TOKEN, APP_PORT, NGROK_STATIC_DOMAIN

def setup_ngrok():
    if not NGROK_AUTH_TOKEN:
        print("NGROK_AUTH_TOKEN không được tìm thấy trong .env. Bỏ qua cấu hình ngrok.")
        return None

    try:
        ngrok.set_auth_token(NGROK_AUTH_TOKEN)
        
        tunnels = ngrok.get_tunnels()
        if tunnels:
            print("Đã tìm thấy tunnel ngrok đang chạy:")
            for tunnel in tunnels:
                print(f"- {tunnel.public_url}")
            return tunnels[0].public_url
        
        if NGROK_STATIC_DOMAIN:
            public_url = ngrok.connect(addr=APP_PORT, domain=NGROK_STATIC_DOMAIN).public_url
            print(f"Ngrok tunnel với domain tĩnh '{NGROK_STATIC_DOMAIN}' đã khởi tạo: {public_url}")
        else:
            public_url = ngrok.connect(APP_PORT).public_url
            print(f"Ngrok tunnel với domain ngẫu nhiên đã khởi tạo: {public_url}")
            
        return public_url
    except Exception as e:
        print(f"Lỗi khi cấu hình hoặc khởi tạo ngrok: {e}")
        print("Đảm bảo ngrok đã được cài đặt, NGROK_AUTH_TOKEN hợp lệ và domain tĩnh (nếu có) là của bạn.")
        return None

if __name__ == "__main__":
    public_url = setup_ngrok()
    if public_url:
        print(f"Public URL: {public_url}")
        print("Nhấn Ctrl+C để thoát.")
        try:
            ngrok.get_connection().join()
        except KeyboardInterrupt:
            print("Ngrok tunnel đóng.")
            ngrok.disconnect()
            ngrok.kill()