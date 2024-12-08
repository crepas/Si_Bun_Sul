package com.sys.OrderSystem.Repository;


import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByCategory(Category category);
    List<Menu> findByCategoryOrderByMenuIdAsc(Category category);
}