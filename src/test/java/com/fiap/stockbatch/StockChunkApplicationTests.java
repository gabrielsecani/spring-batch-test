package com.fiap.stockbatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StockChunkApplication.class, BatchConfig.class})
class StockChunkApplicationTests {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private Job job;
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	void batchProcessGalinhaFileToDatabase() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, SQLException {

		JobExecution run = jobLauncherTestUtils.getJobLauncher()
				.run(job, jobLauncherTestUtils.getUniqueJobParameters());
		Assertions.assertNotNull(run);
		assertEquals(BatchStatus.COMPLETED, run.getStatus());

		ResultSet resultSet = dataSource.getConnection()
				.prepareStatement("select count(*) from TB_PESSOA")
				.executeQuery();
		await().atMost(10, TimeUnit.SECONDS)
				.until(() -> {
					resultSet.last();
					return resultSet.getInt(1) == 3;
				});
		assertEquals(3, resultSet.getInt(1));
	}

}
