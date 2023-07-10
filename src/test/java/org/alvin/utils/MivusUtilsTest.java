package org.alvin.utils;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class MivusUtilsTest {

    @Autowired
    private MivusUtils mivusUtils;
//
//    @BeforeEach
//    void setUp() {
//    }

//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void search() {
        List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f,4.0f);
        List<List<Float>> vectors = Collections.singletonList(floats);
        List<Long> longs = mivusUtils.search("product", vectors, 10);
        System.out.println(longs);
    }
}