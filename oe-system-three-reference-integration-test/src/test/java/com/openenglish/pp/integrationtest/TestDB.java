package com.openenglish.pp.integrationtest;

import com.openenglish.substrate.database.DatabaseConfig;
import com.openenglish.substrate.environment.EnvironmentPropertyConfigurer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.ContextConfiguration;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {propConfig.class, DatabaseConfig.class})
public class TestDB {

  @Autowired
  DataSource dataSource;

  @Test
  public void pingBinaryClient() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//    //default timezone: PDT
////    final Date timestamp = new Date(new java.util.Date().getTime());
//    DateTime dateTime = new DateTime();
//    final Timestamp timestamp = new Timestamp(dateTime.getMillis());
//    ;
//    System.out.println("********************Inserting timestamp:"+dateTime);
//
//    //insert query with java date to without timezone
//    jdbcTemplate.update("delete from auth_ref_message where message_id='1'");
//    PreparedStatementCreator creator1 = new PreparedStatementCreator() {
//      @Override
//      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//        PreparedStatement pst = con.prepareStatement(
//            "insert into auth_ref_message(message_id, region, created_date, message_bytes) values ('1', 'US', ?, 'blah')");
//        pst.setTimestamp(1, timestamp);
//
//        return pst;
//      }
//    };
//    jdbcTemplate.update(creator1);
//    //insert query with java date to with timezone
//    jdbcTemplate.update("delete from payment_history_audit where id='1'");
//    PreparedStatementCreator creator2 = new PreparedStatementCreator() {
//      @Override
//      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//        PreparedStatement pst = con.prepareStatement(
//            "insert into payment_history_audit\n"
//            + "(id, zuora_account_id, merchant_transaction_id, status, message, created_at, document_number, phone, email, invoice_id, amount, \n"
//            + " failure_count, payment_type, subscription_id, archive_date, payment_method_id, modified_at, oe_payment_gateway, currency, modified_at_tz)\n"
//            + "    values(1, 1, 1, 'ACTIVE', 'MSG', ?, 123, '1111', 'a@a.com', 1, 12, 2, 1, 23,?, 1, now(),'barnote', 'USD',? )");
//        pst.setTimestamp(1, timestamp);
//        pst.setTimestamp(2, timestamp);
//        pst.setTimestamp(3, timestamp);
//        return pst;
//      }
//    };
//    jdbcTemplate.update(creator2);


    jdbcTemplate.query("select created_at created, modified_at_tz modifiedAtTz from payment_history_audit where id=1",

                       new ResultSetExtractor<Object>() {
      @Override
      public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
//        Timestamp created = resultSet.getTimestamp("created");
//        Timestamp modifiedAtTz = );
        System.out.println("created:"+new DateTime(resultSet.getTimestamp("created").getTime())+" modifiedAtTz:"+new DateTime(resultSet.getTimestamp("modifiedAtTz").getTime()));
        return null;
      }
    });

    //override timezone: New York
    //insert query with java date to without timezone

    //insert query with java date to with timezone



  }
}

@Configuration
class propConfig{
  @Bean
  public static EnvironmentPropertyConfigurer envPropertyPlaceholderConfigurer(){
    EnvironmentPropertyConfigurer environmentPropertyConfigurer = new EnvironmentPropertyConfigurer();
    environmentPropertyConfigurer.setPropFileName("oe-system-three-reference.properties");
    environmentPropertyConfigurer.setOrder(1);
    return environmentPropertyConfigurer;
  }

}
