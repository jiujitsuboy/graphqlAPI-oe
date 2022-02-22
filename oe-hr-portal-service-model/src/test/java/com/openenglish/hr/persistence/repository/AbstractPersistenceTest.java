package com.openenglish.hr.persistence.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.openenglish.hr.persistence.PersistenceTestConfig;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {PersistenceTestConfig.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
                          DirtiesContextTestExecutionListener.class,
                          TransactionalTestExecutionListener.class,
                          DbUnitTestExecutionListener.class })
//@TransactionConfiguration(defaultRollback = true)
public class AbstractPersistenceTest {

}
