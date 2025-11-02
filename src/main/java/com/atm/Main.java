package com.atm;

import com.atm.dto.AccountDTO;
import com.atm.model.*;
import com.atm.repository.ATMRepository;
import com.atm.service.AccountService;
import com.atm.repository.UserRepository; // Import UserRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

@SpringBootApplication(scanBasePackages = "com.atm")
@EnableJpaRepositories(basePackages = "com.atm.repository") // Đảm bảo quét các repository
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        try {
            logger.info("Starting ATM System...");

            // Khởi chạy ứng dụng và lấy Spring Context
            ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

            // Lấy instance UserRepository từ Spring context
            UserRepository userRepository = context.getBean(UserRepository.class);

            ATMRepository atmRepository = context.getBean(ATMRepository.class);

            // Gọi phương thức khởi tạo tài khoản admin
            initializeAdminAccount(context.getBean(AccountService.class), userRepository, atmRepository);

            logger.info("ATM System started successfully.");
        } catch (Exception e) {
            logger.error("Failed to start ATM System: ", e);
        }
    }

    private static void initializeAdminAccount(AccountService accountService, UserRepository userRepository, ATMRepository atmRepository) {
        logger.info("Starting admin account initialization...");

        User existingUser = userRepository.findByEmail("admin@example.com");
        if (existingUser == null) {
            logger.info("Admin user does not exist, proceeding to create...");

            String userId = "123456789012";
            User user = new User(userId, "Admin User", "admin@example.com", "1234567890");
            userRepository.save(user);
            logger.info("Admin user created successfully!");

            // Mã hóa PIN trước khi lưu vào database
//            String hashedPin = passwordEncoder.encode("123456");

            AccountDTO adminDTO = new AccountDTO(
                    "0000000000",
                    "Default Admin",
                    user.getUserId(),
                    AccountType.SAVINGS,
                    AccountStatus.ACTIVE,
                    null,  // Để balance null
                    null,  // Để pin null
                    "ADMIN",
                    user.getPhone(),
                    user.getEmail()
            );

            Account account = adminDTO.toAccount(userRepository);
            accountService.createAccount(account);
            logger.info("Admin account has been created successfully!");

            // Tạo ATM mặc định
            Optional<ATM> currentATM = atmRepository.findById(1L);
            if (!currentATM.isPresent()) {
                ATM atm = new ATM();
                atm.setAtmId(1L);
                atm.setCash200(10);
                atm.setCash50(10);
                atm.setCash100(10);
                atm.setCash500(10);
                atm.setTotalAmount(8500000);
                atm.setStatus(ATMStatus.ACTIVE);
                atmRepository.save(atm);
                logger.info("ATM has been created successfully!");
            }

        } else {
            logger.info("Admin user already exists, skipping user creation.");
        }
    }
}
