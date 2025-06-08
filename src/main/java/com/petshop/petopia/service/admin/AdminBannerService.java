package com.petshop.petopia.service.admin;

import com.petshop.petopia.component.CalculatePrice;
import com.petshop.petopia.component.ConvertBanner;
import com.petshop.petopia.dto.request.banner.CUBannerRequest;
import com.petshop.petopia.dto.response.banner.CUBannerResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.sale.Banner;
import com.petshop.petopia.repository.BannerRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.service.FirebaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional; // Đảm bảo đã import Optional

@Service
@AllArgsConstructor
public class AdminBannerService {
    private final BannerRepository bannerRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final FirebaseService firebaseService;
    private final CalculatePrice calculatePrice;
    private final ConvertBanner convertBanner;

    @Transactional
    public CUBannerResponse createBanner(CUBannerRequest cuBannerRequest) throws IOException {
        String imageUrl = null;

        if (cuBannerRequest.getStartDate() != null && cuBannerRequest.getEndDate() != null
                && cuBannerRequest.getStartDate().after(cuBannerRequest.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        if (cuBannerRequest.getFile() != null && !cuBannerRequest.getFile().isEmpty()) {
            imageUrl = firebaseService.uploadImageBanner(cuBannerRequest.getFile());
        }

        Banner banner = new Banner();
        banner.setImage(imageUrl);
        banner.setSalePercent(cuBannerRequest.getSalePercent());
        banner.setStartDate(cuBannerRequest.getStartDate());
        banner.setEndDate(cuBannerRequest.getEndDate());

        Banner savedBanner = bannerRepository.save(banner); // Validation sẽ chạy khi save
        return convertBanner.mapBannerToCUBannerResponse(savedBanner);
    }

    @Transactional
    public CUBannerResponse updateBanner(Integer id, CUBannerRequest cuBannerRequest) throws IOException {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + id));

        if (cuBannerRequest.getFile() != null && !cuBannerRequest.getFile().isEmpty()) {
            String oldImageUrl = existingBanner.getImage();
            String newImageUrl = firebaseService.uploadImageBanner(cuBannerRequest.getFile());
            existingBanner.setImage(newImageUrl);
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                firebaseService.deleteFileByUrl(oldImageUrl);
            }
        }
        Optional.ofNullable(cuBannerRequest.getSalePercent()).ifPresent(existingBanner::setSalePercent);

        boolean newStartDateProvided = cuBannerRequest.getStartDate() != null;
        boolean newEndDateProvided = cuBannerRequest.getEndDate() != null;

        Date tempStartDate = newStartDateProvided ? cuBannerRequest.getStartDate() : existingBanner.getStartDate();
        Date tempEndDate = newEndDateProvided ? cuBannerRequest.getEndDate() : existingBanner.getEndDate();

        if (tempStartDate != null && tempEndDate != null && tempStartDate.after(tempEndDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc.");
        }

        if (newStartDateProvided) {
            existingBanner.setStartDate(cuBannerRequest.getStartDate());
        }
        if (newEndDateProvided) {
            existingBanner.setEndDate(cuBannerRequest.getEndDate());
        }

        Banner updatedBanner = bannerRepository.save(existingBanner); // Lưu banner đã cập nhật
        return convertBanner.mapBannerToCUBannerResponse(updatedBanner);
    }

    @Transactional
    public void deleteBanner(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + id));

        List<Pet> petsInBanner = petRepository.findByBannerId(id);
        petsInBanner.forEach(pet -> pet.setBanner(null));
        petRepository.saveAll(petsInBanner);

        List<Product> productsInBanner = productRepository.findByBannerId(id);
        productsInBanner.forEach(product -> product.setBanner(null));
        productRepository.saveAll(productsInBanner);

        bannerRepository.delete(banner);

        if (banner.getImage() != null && !banner.getImage().isEmpty()) {
            firebaseService.deleteFileByUrl(banner.getImage());
        }
    }

    @Transactional
    public void addPetToBanner(Integer bannerId, Integer petId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        if (!pet.getStatus()) { // Giả định getStatus() là true nếu đang bán
            throw new IllegalStateException("Không thể thêm thú cưng đã ngừng bán");
        }

        if (pet.getBanner() != null) {
            if (pet.getBanner().getId().equals(bannerId)) {
                throw new IllegalStateException("Thú cưng đã tồn tại trong banner này");
            }
            throw new IllegalStateException("Thú cưng đã thuộc về banner khác (ID: " + pet.getBanner().getId() + ")");
        }

        pet.setBanner(banner);
        petRepository.save(pet);
    }

    @Transactional
    public void removePetFromBanner(Integer petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));
        pet.setBanner(null);
        petRepository.save(pet);
    }

    @Transactional
    public void addProductToBanner(Integer bannerId, Integer productId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        if (product.getStockQuantity() <= 0) {
            throw new IllegalStateException("Không thể thêm sản phẩm đã hết hàng");
        }

        if (product.getBanner() != null) {
            if (product.getBanner().getId().equals(bannerId)) {
                throw new IllegalStateException("Sản phẩm đã tồn tại trong banner này");
            }
            throw new IllegalStateException("Sản phẩm đã thuộc về banner khác (ID: " + product.getBanner().getId() + ")");
        }

        product.setBanner(banner);
        productRepository.save(product); // Cần save product để cập nhật mối quan hệ
    }

    @Transactional
    public void removeProductFromBanner(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        if (product.getBanner() == null) {
            throw new IllegalStateException("Sản phẩm không thuộc về banner nào");
        }

        product.setBanner(null);
        productRepository.save(product); // Cần save product để cập nhật mối quan hệ
    }

    @Transactional
    public void addAllPetsToBanner(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        List<Pet> allPets = petRepository.findAll();
        List<Pet> petsToUpdate = new ArrayList<>();
        allPets.forEach(pet -> {
            // Chỉ thêm vào banner nếu chưa có banner hoặc thuộc banner khác VÀ đang bán
            if (pet.getStatus() && (pet.getBanner() == null || !pet.getBanner().getId().equals(bannerId))) {
                pet.setBanner(banner);
                petsToUpdate.add(pet);
            }
        });
        petRepository.saveAll(petsToUpdate); // Lưu tất cả thay đổi một lần
    }

    @Transactional
    public void addAllProductsToBanner(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        List<Product> allProducts = productRepository.findAll();
        List<Product> productsToUpdate = new ArrayList<>();
        allProducts.forEach(product -> {
            // Chỉ thêm vào banner nếu chưa có banner hoặc thuộc banner khác VÀ còn hàng
            if (product.getStockQuantity() > 0 && (product.getBanner() == null || !product.getBanner().getId().equals(bannerId))) {
                product.setBanner(banner);
                productsToUpdate.add(product);
            }
        });
        productRepository.saveAll(productsToUpdate); // Lưu tất cả thay đổi một lần
    }

    @Transactional
    public void addPetsByCategoryToBanner(Integer bannerId, String categoryName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không hợp lệ");
        }

        List<Pet> petsByCategory = petRepository.findByPetCategory_Name(categoryName);
        List<Pet> petsToUpdate = new ArrayList<>();
        petsByCategory.forEach(pet -> {
            if (pet.getStatus() && (pet.getBanner() == null || !pet.getBanner().getId().equals(bannerId))) {
                pet.setBanner(banner);
                petsToUpdate.add(pet);
            }
        });
        petRepository.saveAll(petsToUpdate);
    }

    @Transactional
    public void addPetsByBreedToBanner(Integer bannerId, String breedName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Pet> petsByBreed = petRepository.findByBreed_Name(breedName);
        List<Pet> petsToUpdate = new ArrayList<>();
        petsByBreed.forEach(pet -> {
            if (pet.getStatus() && (pet.getBanner() == null || !pet.getBanner().getId().equals(bannerId))) {
                pet.setBanner(banner);
                petsToUpdate.add(pet);
            }
        });
        petRepository.saveAll(petsToUpdate);
    }

    @Transactional
    public void addProductsByCategoryToBanner(Integer bannerId, String categoryName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByCategory = productRepository.findByPrCategory_Name(categoryName);
        List<Product> productsToUpdate = new ArrayList<>();
        productsByCategory.forEach(product -> {
            if (product.getStockQuantity() > 0 && (product.getBanner() == null || !product.getBanner().getId().equals(bannerId))) {
                product.setBanner(banner);
                productsToUpdate.add(product);
            }
        });
        productRepository.saveAll(productsToUpdate);
    }

    @Transactional
    public void addProductsByBrandToBanner(Integer bannerId, String brandName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByBrand = productRepository.findByBrand_Name(brandName);
        List<Product> productsToUpdate = new ArrayList<>();
        productsByBrand.forEach(product -> {
            if (product.getStockQuantity() > 0 && (product.getBanner() == null || !product.getBanner().getId().equals(bannerId))) {
                product.setBanner(banner);
                productsToUpdate.add(product);
            }
        });
        productRepository.saveAll(productsToUpdate);
    }

    @Transactional
    public void addProductsByTypeToBanner(Integer bannerId, String typeName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByType = productRepository.findByType_Name(typeName);
        List<Product> productsToUpdate = new ArrayList<>();
        productsByType.forEach(product -> {
            if (product.getStockQuantity() > 0 && (product.getBanner() == null || !product.getBanner().getId().equals(bannerId))) {
                product.setBanner(banner);
                productsToUpdate.add(product);
            }
        });
        productRepository.saveAll(productsToUpdate);
    }
}