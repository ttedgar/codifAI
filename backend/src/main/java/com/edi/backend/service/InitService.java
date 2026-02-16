package com.edi.backend.service;

import com.edi.backend.entity.Challenge;
import com.edi.backend.entity.Difficulty;
import com.edi.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;

    @Override
    public void run(String... args) {
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
                        function add(a, b) {
                          // USER CODE WILL BE INSERTED HERE
                        }
                        
                        // Test cases
                        const test1 = add(2, 3) === 5;
                        const test2 = add(0, 0) === 0;
                        const test3 = add(-1, 1) === 0;
                        const test4 = add(10, 20) === 30;
                        const test5 = add(-5, -3) === -8;
                        
                        const passed = [test1, test2, test3, test4, test5].filter(Boolean).length;
                        console.log(`Passed ${passed}/5 tests`);
                        if (passed === 5) console.log('All tests passed!');
                        else process.exit(1);"""
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
                        function sumArray(numbers) {
                          // USER CODE WILL BE INSERTED HERE
                        }
                        
                        // Test cases
                        const test1 = sumArray([1, 2, 3]) === 6;
                        const test2 = sumArray([]) === 0;
                        const test3 = sumArray([10]) === 10;
                        const test4 = sumArray([-1, -2, -3]) === -6;
                        const test5 = sumArray([1, 2, 3, 4, 5]) === 15;
                        
                        const passed = [test1, test2, test3, test4, test5].filter(Boolean).length;
                        console.log(`Passed ${passed}/5 tests`);
                        if (passed === 5) console.log('All tests passed!');
                        else process.exit(1);"""
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
                        function doubleNumber(n) {
                          // USER CODE WILL BE INSERTED HERE
                        }
                        
                        // Test cases
                        const test1 = doubleNumber(5) === 10;
                        const test2 = doubleNumber(0) === 0;
                        const test3 = doubleNumber(-3) === -6;
                        const test4 = doubleNumber(100) === 200;
                        const test5 = doubleNumber(2.5) === 5;
                        
                        const passed = [test1, test2, test3, test4, test5].filter(Boolean).length;
                        console.log(`Passed ${passed}/5 tests`);
                        if (passed === 5) console.log('All tests passed!');
                        else process.exit(1);"""
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
