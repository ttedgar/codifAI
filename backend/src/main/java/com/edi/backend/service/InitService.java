package com.edi.backend.service;

import com.edi.backend.entity.Challenge;
import com.edi.backend.entity.Difficulty;
import com.edi.backend.entity.Role;
import com.edi.backend.entity.User;
import com.edi.backend.repository.ChallengeRepository;
import com.edi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminUser();

        if (challengeRepository.count() > 0) {
            log.info("Challenges already exist, skipping initialization");
            return;
        }

        log.info("Initializing database with sample challenges...");
        createChallenge1();
        createChallenge2();
        createChallenge3();
        log.info("Database initialization complete!");
    }

    private void createAdminUser() {
        if (userRepository.existsByEmail("admin@admin.admin")) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        User adminUser = User.builder()
                .username("admin")
                .email("admin@admin.admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .xp(0)
                .build();

        userRepository.save(adminUser);
        log.info("Created admin user: admin@admin.admin");
    }

    private void createChallenge1() {
        Challenge challenge = Challenge.builder()
                .title("Add Two Numbers")
                .description("""
                    Write a function that takes two numbers and returns their sum.
                    
                    **Example:**
                    ```javascript
                    add(2, 3) // returns 5
                    add(-1, 1) // returns 0
                    ```""")
                .difficulty(Difficulty.EASY)
                .starterCode("function add(a, b) {\n  // Your code here\n}")
                .hiddenTests(
                    """
                        // Test cases
                        console.log(add(2, 3) === 5 ? 'PASS: add(2, 3) should return 5' : 'FAIL: add(2, 3) should return 5');
                        console.log(add(0, 0) === 0 ? 'PASS: add(0, 0) should return 0' : 'FAIL: add(0, 0) should return 0');
                        console.log(add(-1, 1) === 0 ? 'PASS: add(-1, 1) should return 0' : 'FAIL: add(-1, 1) should return 0');
                        console.log(add(10, 20) === 30 ? 'PASS: add(10, 20) should return 30' : 'FAIL: add(10, 20) should return 30');
                        console.log(add(-5, -3) === -8 ? 'PASS: add(-5, -3) should return -8' : 'FAIL: add(-5, -3) should return -8');

                        // Count results
                        const output = [
                          add(2, 3) === 5,
                          add(0, 0) === 0,
                          add(-1, 1) === 0,
                          add(10, 20) === 30,
                          add(-5, -3) === -8
                        ];
                        const passed = output.filter(Boolean).length;
                        console.log(`RESULT:${passed}/5`);"""
                )
                .sampleTests(
                    """
                        add(2, 3) // Expected: 5
                        add(0, 0) // Expected: 0
                        add(-1, 1) // Expected: 0"""
                )
                .tags(Arrays.asList("math", "beginner", "arithmetic"))
                .build();

        challengeRepository.save(challenge);
        log.info("Created challenge: {}", challenge.getTitle());
    }

    private void createChallenge2() {
        Challenge challenge = Challenge.builder()
                .title("Sum of Array")
                .description("""
                    Write a function that takes an array of numbers and returns their sum.
                    
                    **Example:**
                    ```javascript
                    sumArray([1, 2, 3]) // returns 6
                    sumArray([]) // returns 0
                    ```""")
                .difficulty(Difficulty.EASY)
                .starterCode("function sumArray(numbers) {\n  // Your code here\n}")
                .hiddenTests(
                    """
                        // Test cases
                        console.log(sumArray([1, 2, 3]) === 6 ? 'PASS: sumArray([1, 2, 3]) should return 6' : 'FAIL: sumArray([1, 2, 3]) should return 6');
                        console.log(sumArray([]) === 0 ? 'PASS: sumArray([]) should return 0' : 'FAIL: sumArray([]) should return 0');
                        console.log(sumArray([10]) === 10 ? 'PASS: sumArray([10]) should return 10' : 'FAIL: sumArray([10]) should return 10');
                        console.log(sumArray([-1, -2, -3]) === -6 ? 'PASS: sumArray([-1, -2, -3]) should return -6' : 'FAIL: sumArray([-1, -2, -3]) should return -6');
                        console.log(sumArray([1, 2, 3, 4, 5]) === 15 ? 'PASS: sumArray([1, 2, 3, 4, 5]) should return 15' : 'FAIL: sumArray([1, 2, 3, 4, 5]) should return 15');

                        // Count results
                        const output = [
                          sumArray([1, 2, 3]) === 6,
                          sumArray([]) === 0,
                          sumArray([10]) === 10,
                          sumArray([-1, -2, -3]) === -6,
                          sumArray([1, 2, 3, 4, 5]) === 15
                        ];
                        const passed = output.filter(Boolean).length;
                        console.log(`RESULT:${passed}/5`);"""
                )
                .sampleTests(
                    """
                        sumArray([1, 2, 3]) // Expected: 6
                        sumArray([]) // Expected: 0
                        sumArray([10]) // Expected: 10"""
                )
                .tags(Arrays.asList("arrays", "beginner", "loops"))
                .build();

        challengeRepository.save(challenge);
        log.info("Created challenge: {}", challenge.getTitle());
    }

    private void createChallenge3() {
        Challenge challenge = Challenge.builder()
                .title("Double the Number")
                .description("""
                    Write a function that takes a number and returns its double (multiply by 2).
                    
                    **Example:**
                    ```javascript
                    doubleNumber(5) // returns 10
                    doubleNumber(0) // returns 0
                    ```""")
                .difficulty(Difficulty.EASY)
                .starterCode("function doubleNumber(n) {\n  // Your code here\n}")
                .hiddenTests(
                    """
                        // Test cases
                        console.log(doubleNumber(5) === 10 ? 'PASS: doubleNumber(5) should return 10' : 'FAIL: doubleNumber(5) should return 10');
                        console.log(doubleNumber(0) === 0 ? 'PASS: doubleNumber(0) should return 0' : 'FAIL: doubleNumber(0) should return 0');
                        console.log(doubleNumber(-3) === -6 ? 'PASS: doubleNumber(-3) should return -6' : 'FAIL: doubleNumber(-3) should return -6');
                        console.log(doubleNumber(100) === 200 ? 'PASS: doubleNumber(100) should return 200' : 'FAIL: doubleNumber(100) should return 200');
                        console.log(doubleNumber(2.5) === 5 ? 'PASS: doubleNumber(2.5) should return 5' : 'FAIL: doubleNumber(2.5) should return 5');

                        // Count results
                        const output = [
                          doubleNumber(5) === 10,
                          doubleNumber(0) === 0,
                          doubleNumber(-3) === -6,
                          doubleNumber(100) === 200,
                          doubleNumber(2.5) === 5
                        ];
                        const passed = output.filter(Boolean).length;
                        console.log(`RESULT:${passed}/5`);"""
                )
                .sampleTests(
                    """
                        doubleNumber(5) // Expected: 10
                        doubleNumber(0) // Expected: 0
                        doubleNumber(-3) // Expected: -6"""
                )
                .tags(Arrays.asList("math", "beginner", "arithmetic"))
                .build();

        challengeRepository.save(challenge);
        log.info("Created challenge: {}", challenge.getTitle());
    }
}
