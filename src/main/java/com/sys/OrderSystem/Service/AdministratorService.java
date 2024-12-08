package com.sys.OrderSystem.Service;

import com.sys.OrderSystem.Entity.Administrator;
import com.sys.OrderSystem.Repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;

    public boolean registerAdmin(Administrator administrator) {
        // 중복 검사
        if (administratorRepository.existsByUsername(administrator.getUsername()) ||
                administratorRepository.existsByEmail(administrator.getEmail())) {
            return false;
        }

        administratorRepository.save(administrator);
        return true;
    }

    public Administrator login(String username, String password) {
        Administrator admin = administratorRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }
    public boolean checkUsernameExists(String username) {
        return administratorRepository.existsByUsername(username);
    }

    public boolean updatePassword(String username, String newPassword) {
        Administrator admin = administratorRepository.findByUsername(username);
        if (admin != null) {
            admin.setPassword(newPassword);
            administratorRepository.save(admin);
            return true;
        }
        return false;
    }
}