package com.petshop.petopia.service.admin;

import com.petshop.petopia.component.CalculateDiscount;
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
import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminBannerService {
    private final BannerRepository bannerRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final FirebaseService firebaseService;
    private final CalculateDiscount calculateDiscount;
    private final ConvertBanner convertBanner;

    @Transactional
    public CUBannerResponse createBanner(CUBannerRequest cuBannerRequest) throws IOException {
        String imageUrl = null;

        if (cuBannerRequest.getStartDate() != null && cuBannerRequest.getEndDate() != null
                && cuBannerRequest.getStartDate().after(cuBannerRequest.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        if (cuBannerRequest.getSalePercent() != null) {
            BigDecimal salePercent = cuBannerRequest.getSalePercent();
            if (salePercent.compareTo(BigDecimal.ZERO) < 0 || salePercent.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Phần trăm giảm giá phải từ 0 đến 1");
            }
        }

        if (cuBannerRequest.getFile() != null && !cuBannerRequest.getFile().isEmpty()) {
            imageUrl = firebaseService.uploadImageBanner(cuBannerRequest.getFile());
        }

        Banner banner = new Banner();
        banner.setImage(imageUrl);
        banner.setSalePercent(cuBannerRequest.getSalePercent());
        banner.setStartDate(cuBannerRequest.getStartDate());
        banner.setEndDate(cuBannerRequest.getEndDate());

        Banner savedBanner = bannerRepository.save(banner);
        return convertBanner.mapBannerToCUBannerResponse(savedBanner);
    }

    @Transactional
    public CUBannerResponse updateBanner(Integer id, CUBannerRequest cuBannerRequest) throws IOException {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + id));

        if (cuBannerRequest.getStartDate() != null && cuBannerRequest.getEndDate() != null
                && cuBannerRequest.getStartDate().after(cuBannerRequest.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        String oldImageUrl = existingBanner.getImage();
        if (cuBannerRequest.getFile() != null && !cuBannerRequest.getFile().isEmpty()) {
            String newImageUrl = firebaseService.uploadImageBanner(cuBannerRequest.getFile());
            existingBanner.setImage(newImageUrl);
            if (oldImageUrl != null) {
                firebaseService.deleteFileByUrl(oldImageUrl);
            }
        }

        if (cuBannerRequest.getSalePercent() != null) {
            BigDecimal salePercent = cuBannerRequest.getSalePercent();
            if (salePercent.compareTo(BigDecimal.ZERO) >= 0 && salePercent.compareTo(BigDecimal.ONE) <= 0) {
                existingBanner.setSalePercent(salePercent);
            } else {
                throw new IllegalArgumentException("Phần trăm giảm giá phải từ 0 đến 1");
            }
        }
        if (cuBannerRequest.getStartDate() != null) {
            existingBanner.setStartDate(cuBannerRequest.getStartDate());
        }
        if (cuBannerRequest.getEndDate() != null) {
            existingBanner.setEndDate(cuBannerRequest.getEndDate());
        }

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return convertBanner.mapBannerToCUBannerResponse(updatedBanner);
    }

    @Transactional
    public void deleteBanner(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + id));

        List<Pet> petsInBanner = petRepository.findByBannerId(id);
        petsInBanner.forEach(pet -> pet.setBanner(null));

        List<Product> productsInBanner = productRepository.findByBannerId(id);
        productsInBanner.forEach(product -> product.setBanner(null));

        bannerRepository.delete(banner);

        if (banner.getImage() != null) {
            firebaseService.deleteFileByUrl(banner.getImage());
        }
    }

    @Transactional
    public void addPetToBanner(Integer bannerId, Integer petId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thú cưng với ID: " + petId));

        if (!pet.getStatus()) {
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
    }

    @Transactional
    public void removeProductFromBanner(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        if (product.getBanner() == null) {
            throw new IllegalStateException("Sản phẩm không thuộc về banner nào");
        }

        product.setBanner(null);
    }

    @Transactional
    public void addAllPetsToBanner(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        List<Pet> allPets = petRepository.findAll();
        allPets.forEach(pet -> {
            if (pet.getBanner() == null || !pet.getBanner().getId().equals(bannerId) || pet.getStatus()) {
                pet.setBanner(banner);
            }
        });
    }

    @Transactional
    public void addAllProductsToBanner(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        List<Product> allProducts = productRepository.findAll();
        allProducts.forEach(product -> {
            if (product.getBanner() == null || !product.getBanner().getId().equals(bannerId) || product.getStockQuantity() > 0) {
                product.setBanner(banner);
            }
        });
    }

    @Transactional
    public void addPetsByCategoryToBanner(Integer bannerId, String categoryName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));

        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không hợp lệ");
        }

        List<Pet> petsByCategory = petRepository.findByPetCategory_Name(categoryName);
        petsByCategory.forEach(pet -> {
            if (pet.getBanner() == null || !pet.getBanner().getId().equals(bannerId)) {
                pet.setBanner(banner);
            }
        });
    }

    @Transactional
    public void addPetsByBreedToBanner(Integer bannerId, String breedName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Pet> petsByBreed = petRepository.findByBreed_Name(breedName);
        petsByBreed.forEach(pet -> {
            pet.setBanner(banner);
            petRepository.save(pet);
        });
    }

    @Transactional
    public void addProductsByCategoryToBanner(Integer bannerId, String categoryName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByCategory = productRepository.findByPrCategory_Name(categoryName);
        productsByCategory.forEach(product -> {
            product.setBanner(banner);
            productRepository.save(product);
        });
    }

    @Transactional
    public void addProductsByBrandToBanner(Integer bannerId, String brandName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByBrand = productRepository.findByBrand_Name(brandName);
        productsByBrand.forEach(product -> {
            product.setBanner(banner);
            productRepository.save(product);
        });
    }

    @Transactional
    public void addProductsByTypeToBanner(Integer bannerId, String typeName) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner với ID: " + bannerId));
        List<Product> productsByType = productRepository.findByType_Name(typeName);
        productsByType.forEach(product -> {
            product.setBanner(banner);
            productRepository.save(product);
        });
    }
}
