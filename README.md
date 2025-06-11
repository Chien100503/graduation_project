python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt

# clone models AI of Huggingface
make folder models
open window powershell
git clone https://huggingface.co/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2 archive
cd archive
git lfs install
git lfs pull

# train first data for code
py vector_store.py

# run code for chat
py main.py