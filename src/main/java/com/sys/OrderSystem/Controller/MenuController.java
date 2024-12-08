package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Entity.Menu;
import com.sys.OrderSystem.Service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    private static final String IMAGE_UPLOAD_DIR = "uploads/images";  // 이미지 저장 디렉터리


    // 메뉴 저장 (AJAX)
    @PostMapping("/save")
    public ResponseEntity<?> saveMenu(
            @RequestParam String name,
            @RequestParam Integer price,
            @RequestParam String categoryName,
            @RequestParam(required = false) MultipartFile image) {
        try {
            // 로깅 추가
            System.out.println("Received save menu request:");
            System.out.println("Name: " + name);
            System.out.println("Price: " + price);
            System.out.println("Category: " + categoryName);
            System.out.println("Image: " + (image != null ? image.getOriginalFilename() : "no image"));

            Menu menu = new Menu();
            menu.setName(name);
            menu.setPrice(price);

            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = saveImage(image, categoryName);
                menu.setImageUrl(imageUrl);
            }

            Menu savedMenu = menuService.saveMenu(menu, categoryName, imageUrl);
            return ResponseEntity.ok(savedMenu);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("메뉴 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Menu>> getMenusByCategory(@RequestParam String category) {
        try {
            List<Menu> menus = menuService.getMenusByCategory(category);
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String saveImage(MultipartFile image, String categoryName) throws IOException {
        // 이미지를 저장할 디렉토리 경로 설정
        String uploadDir = "src/main/resources/static/images/" + categoryName;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 생성
        String fileName = UUID.randomUUID().toString() + "_" +
                StringUtils.cleanPath(image.getOriginalFilename());

        // 파일 저장
        Path path = Paths.get(uploadDir + File.separator + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // 웹에서 접근 가능한 경로 반환
        return "/images/" + categoryName + "/" + fileName;
    }


    @PutMapping("/{menuId}")
    public ResponseEntity<?> updateMenu(
            @PathVariable Integer menuId,
            @RequestParam String name,
            @RequestParam Integer price,
            @RequestParam String categoryName,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Menu updatedMenu = menuService.updateMenu(menuId, name, categoryName, price, image);
            return ResponseEntity.ok(updatedMenu);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메뉴 수정 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Integer menuId) {
        try {
            menuService.deleteMenu(menuId);
            return ResponseEntity.ok().body("메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메뉴 삭제 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<Menu> getMenu(@PathVariable Integer menuId) {
        try {
            Menu menu = menuService.getMenuById(menuId);
            return ResponseEntity.ok(menu);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
