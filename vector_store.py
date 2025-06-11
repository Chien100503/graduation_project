import json
from sentence_transformers import SentenceTransformer
from typing import List, Dict, Union
import chromadb
from mysql_connector import fetch_products, fetch_pets, clean_metadata, convert_decimal_to_float
from config import SENTENCE_TRANSFORMER_MODEL_PATH
import os
from decimal import Decimal
from datetime import datetime

class VectorStore:
    def __init__(self):
        self.model = SentenceTransformer(SENTENCE_TRANSFORMER_MODEL_PATH)
        os.makedirs("./chroma_db", exist_ok=True)
        self.chroma_client = chromadb.PersistentClient(path="./chroma_db")
        self.products_collection = self.chroma_client.get_or_create_collection("products_collection")
        self.pets_collection = self.chroma_client.get_or_create_collection("pets_collection")

    def _prepare_document_for_embedding(self, doc: Dict, doc_type: str) -> str:
        if doc_type == "product":
            return (
                f"Sản phẩm ID: {doc.get('id')}, Tên: {doc.get('name')}, Mô tả: {doc.get('description')}, "
                f"Giá: {doc.get('price')}, Số lượng tồn kho: {doc.get('stock_quantity')}, "
                f"Kích thước: {doc.get('size')}, Cân nặng: {doc.get('weight')}, "
                f"Ngày hết hạn: {doc.get('expiration_date')}, Danh mục: {doc.get('category_name')}, "
                f"Thương hiệu: {doc.get('brand_name')}, Loại: {doc.get('type_name')}"
            )
        elif doc_type == "pet":
            return (
                f"Thú cưng ID: {doc.get('id')}, Tên: {doc.get('name')}, Tuổi: {doc.get('age')}, "
                f"Giới tính: {doc.get('gender')}, Kích thước: {doc.get('size')}, "
                f"Cân nặng: {doc.get('weight')}, Màu sắc: {doc.get('color')}, "
                f"Giá: {doc.get('price')}, Trạng thái: {'Đang bán' if doc.get('status') else 'Đã bán/Ngừng bán'}, "
                f"Mô tả: {doc.get('description')}, Danh mục: {doc.get('category_name')}, "
                f"Giống: {doc.get('breed_name')}"
            )
        return ""

    def add_documents(self, documents: List[Dict], doc_type: str):
        if not documents:
            print(f"No documents to add for type: {doc_type}")
            return

        texts = [self._prepare_document_for_embedding(doc, doc_type) for doc in documents]
        embeddings = self.model.encode(texts).tolist()
        ids = [str(doc['id']) for doc in documents]
        
        processed_metadatas = [clean_metadata(convert_decimal_to_float(doc)) for doc in documents]

        collection = self.products_collection if doc_type == "product" else self.pets_collection

        try:
            all_current_ids = collection.get()['ids']
            if all_current_ids:
                collection.delete(ids=all_current_ids)
            print(f"Cleared existing documents from {doc_type} collection.")
        except Exception as e:
            print(f"Warning: Could not clear {doc_type} collection. Error: {e}")

        collection.add(
            ids=ids,
            embeddings=embeddings,
            documents=texts,
            metadatas=processed_metadatas
        )
        print(f"Successfully added {len(documents)} {doc_type} documents to ChromaDB.")

    def query(self, query_text: str, n_results: int = 5) -> Dict:
        query_embedding = self.model.encode([query_text]).tolist()[0]

        product_results = self.products_collection.query(
            query_embeddings=[query_embedding],
            n_results=n_results,
            include=['documents', 'metadatas', 'distances']
        )

        pet_results = self.pets_collection.query(
            query_embeddings=[query_embedding],
            n_results=n_results,
            include=['documents', 'metadatas', 'distances']
        )

        combined_results = []

        if product_results and product_results["ids"] and product_results["ids"][0]:
            for i in range(len(product_results["ids"][0])):
                combined_results.append({
                    "id": product_results["ids"][0][i],
                    "distance": product_results["distances"][0][i],
                    "metadata": product_results["metadatas"][0][i],
                    "document": product_results["documents"][0][i],
                    "type": "product"
                })
        
        if pet_results and pet_results["ids"] and pet_results["ids"][0]:
            for i in range(len(pet_results["ids"][0])):
                combined_results.append({
                    "id": pet_results["ids"][0][i],
                    "distance": pet_results["distances"][0][i],
                    "metadata": pet_results["metadatas"][0][i],
                    "document": pet_results["documents"][0][i],
                    "type": "pet"
                })

        combined_results.sort(key=lambda x: x["distance"])

        relevant_documents = [res["document"] for res in combined_results[:n_results]]
        relevant_metadatas = [res["metadata"] for res in combined_results[:n_results]]
        
        relevant_ids = [int(res["id"]) for res in combined_results[:n_results]]
        
        response_type = "none"
        if combined_results:
            response_type = combined_results[0]["type"]

        return {
            "documents": [relevant_documents],
            "metadatas": [relevant_metadatas],
            "ids": relevant_ids,
            "type": response_type
        }

    def update_document(self, doc: Dict, doc_type: str):
        if 'id' not in doc:
            raise ValueError("Document must have an 'id' field for update/add operation.")
        
        processed_doc = clean_metadata(convert_decimal_to_float(doc))
        text = self._prepare_document_for_embedding(processed_doc, doc_type)
        embedding = self.model.encode([text]).tolist()[0]
        doc_id = str(processed_doc['id'])

        collection = self.products_collection if doc_type == "product" else self.pets_collection

        try:
            collection.delete(ids=[doc_id])
        except Exception as e:
            print(f"Warning: Could not delete existing document with ID {doc_id} from {doc_type} collection: {e}")

        collection.add(
            ids=[doc_id],
            embeddings=[embedding],
            documents=[text],
            metadatas=[processed_doc]
        )
        print(f"Successfully added/updated {doc_type} document with ID {doc_id} in ChromaDB.")

        os.makedirs("./data", exist_ok=True)
        file_path = f"./data/{doc_type}s.json"
        try:
            with open(file_path, "r", encoding="utf-8") as f:
                documents = json.load(f)
        except (FileNotFoundError, json.JSONDecodeError):
            documents = []

        found = False
        for i, existing_doc in enumerate(documents):
            if existing_doc.get('id') == doc.get('id'):
                documents[i] = doc
                found = True
                break
        if not found:
            documents.append(doc)

        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(documents, f, ensure_ascii=False, indent=2, default=str)
        print(f"Successfully updated local JSON file: {file_path}")


def init_vector_store():
    vector_store = VectorStore()
    
    print("Loading products from MySQL...")
    products = fetch_products()
    products_processed = [clean_metadata(convert_decimal_to_float(p)) for p in products]
    vector_store.add_documents(products_processed, "product")

    print("Loading pets from MySQL...")
    pets = fetch_pets()
    pets_processed = [clean_metadata(convert_decimal_to_float(p)) for p in pets]
    vector_store.add_documents(pets_processed, "pet")
    
    return vector_store

if __name__ == "__main__":
    from mysql_connector import fetch_products, fetch_pets

    print("Initializing Vector Store and loading data...")
    vs = init_vector_store()
    print("Vector Store initialized and data loaded.")

    print("\nTesting query for 'thức ăn cho chó'...")
    query_results = vs.query("thức ăn cho chó")
    print("Query results documents:", query_results["documents"])
    print("Query results IDs:", query_results["ids"])
    print("Query results type:", query_results["type"])

    print("\nTesting query for 'chó poodle'...")
    query_results = vs.query("chó poodle")
    print("Query results documents:", query_results["documents"])
    print("Query results IDs:", query_results["ids"])
    print("Query results type:", query_results["type"])

    print("\nTesting add/update product (using data from DB)...")
    existing_products = fetch_products()
    if existing_products:
        product_to_update = existing_products[0]
        updated_product_data = product_to_update.copy()
        updated_product_data['name'] = f"{product_to_update['name']} (Cập nhật - {datetime.now().strftime('%H:%M:%S')})"
        updated_product_data['price'] = Decimal(str(float(product_to_update['price']) * 1.1))
        updated_product_data['description'] = f"Mô tả mới: {product_to_update['description']} - đã cập nhật."
        
        vs.update_document(updated_product_data, "product")
        print(f"Updated product ID {updated_product_data['id']}: {updated_product_data['name']}")
    else:
        print("Không có sản phẩm nào trong database để kiểm thử cập nhật.")
    
    print("\nTesting add/update pet (using data from DB)...")
    existing_pets = fetch_pets()
    if existing_pets:
        pet_to_update = existing_pets[0]
        updated_pet_data = pet_to_update.copy()
        updated_pet_data['name'] = f"{pet_to_update['name']} (Cập nhật - {datetime.now().strftime('%H:%M:%S')})"
        updated_pet_data['price'] = Decimal(str(float(pet_to_update['price']) * 0.9))
        updated_pet_data['description'] = f"Mô tả mới: {pet_to_update['description']} - đã cập nhật."
        
        vs.update_document(updated_pet_data, "pet")
        print(f"Updated pet ID {updated_pet_data['id']}: {updated_pet_data['name']}")
    else:
        print("Không có thú cưng nào trong database để kiểm thử cập nhật.")

    print("\nQuerying for updated product...")
    if existing_products:
        query_results = vs.query(updated_product_data['name'])
        print("Query results documents:", query_results["documents"])
        print("Query results IDs:", query_results["ids"])
        print("Query results type:", query_results["type"])
    
    print("\nQuerying for updated pet...")
    if existing_pets:
        query_results = vs.query(updated_pet_data['name'])
        print("Query results documents:", query_results["documents"])
        print("Query results IDs:", query_results["ids"])
        print("Query results type:", query_results["type"])
