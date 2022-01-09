package org.jeecg.modules.exam.service.impl;

import org.jeecg.modules.exam.entity.QAset;
import org.jeecg.modules.exam.service.IQsetService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QAsetServiceImplTest {
    @Autowired
    private QAsetServiceImpl qAsetService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void genQAsetByExamId() {
        Map<String,Object> qAMap = new HashMap<String, Object>();
        Map<String, Object> demoresult1 = new HashMap<String,Object>();
        demoresult1.put("answer","A");
        demoresult1.put("acc",0.8);
        demoresult1.put("ratioA",0.8);
        demoresult1.put("ratioB",0.1);
        demoresult1.put("ratioC",0.1);
        demoresult1.put("ratioD",0.0);
        qAMap.put("1",demoresult1);

        Map<String, Object> demoresult2 = new HashMap<String,Object>();
        demoresult2.put("answer","A");
        demoresult2.put("acc",0.8);
        demoresult2.put("ratioA",0.8);
        demoresult2.put("ratioB",0.1);
        demoresult2.put("ratioC",0.1);
        demoresult2.put("ratioD",0.0);
        demoresult2.put("ratioE",0.0);
        qAMap.put("2",demoresult2);

        Map<String, Object> demoresult3 = new HashMap<String,Object>();
        demoresult3.put("answer","AB");
        demoresult3.put("acc",0.8);
        demoresult3.put("ratioA",0.8);
        demoresult3.put("ratioB",0.1);
        demoresult3.put("ratioC",0.1);
        demoresult3.put("ratioD",0.0);
        qAMap.put("101",demoresult3);

        Map<String, Object> demoresult4 = new HashMap<String,Object>();
        demoresult4.put("answer","AB");
        demoresult4.put("acc",0.8);
        demoresult4.put("ratioA",0.8);
        demoresult4.put("ratioB",0.1);
        demoresult4.put("ratioC",0.1);
        demoresult4.put("ratioD",0.0);
        qAMap.put("102",demoresult4);


        assertEquals(true,qAsetService.genQAsetByExamId("a9d0fa4d24a10cc11c986037b7242b09",qAMap));

    }
}