python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt

# train first dat for code
py vector_store.py

# run code for chat
py main.py