package com.sys.OrderSystem.Service;

import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Entity.Menu;
import com.sys.OrderSystem.Repository.CategoryRepository;
import com.sys.OrderSystem.Repository.MenuRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MenuService {
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Map<Category, List<Menu>> getMenusByCategory() {
        Map<Category, List<Menu>> menuMap = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            List<Menu> menus = menuRepository.findByCategoryOrderByMenuIdAsc(category);
            menuMap.put(category, menus);
        }

        return menuMap;
    }

    public List<Menu> getMenusByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

        return menuRepository.findByCategory(category);
    }

    @Transactional
    public Menu saveMenu(Menu menu, String categoryName, String imageUrl) {
        try {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

            menu.setCategory(category);
            if (imageUrl != null) {
                menu.setImageUrl(imageUrl);
            }

            return menuRepository.save(menu);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save menu: " + e.getMessage());
        }
    }

    @Transactional
    public Menu updateMenu(Integer menuId, String name, String categoryName, Integer price, MultipartFile image) throws IOException {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다."));

        menu.setName(name);
        menu.setPrice(price);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image, categoryName);
            menu.setImageUrl(imageUrl);
        }

        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
        menu.setCategory(category);

        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Integer menuId) {
        menuRepository.deleteById(menuId);
    }

    private String saveImage(MultipartFile image, String categoryName) throws IOException {
        // 카테고리별 디렉토리 생성
        String uploadDir = "src/main/resources/static/images/" + categoryName;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 생성 (고유한 파일명을 위해 UUID 사용)
        String fileName = UUID.randomUUID().toString() + "_" +
                StringUtils.cleanPath(image.getOriginalFilename());

        // 파일 저장 경로 생성
        Path path = Paths.get(uploadDir + File.separator + fileName);

        // 파일 저장
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // 웹에서 접근 가능한 URL 경로 반환
        return "/images/" + categoryName + "/" + fileName;
    }

    public Menu getMenuById(Integer menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다."));
    }
}
