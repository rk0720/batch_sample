package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	public JobBuilderFactory jobBuilderFactory;
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
	//ItemProcessorのインスタンスを返す
	@Bean
	public EmployeeItemProcessor processor() {
		return new EmployeeItemProcessor();
	}
	
	//ItemReader用メソッド
	@Bean
	public FlatFileItemReader<Employee> reader() {
		return new FlatFileItemReaderBuilder<Employee>()
				.name("employeeItemReader") //リーダーの名前設定
				.resource(new ClassPathResource("employee.csv")) //入力に利用されるデータ指定
				.delimited() //csvファイルをカンマ区切りに分割
				.names(new String[] {"name", "department"}) //区切った要素ごとにフィールド名を与える
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
					setTargetType(Employee.class);
				}})//取り出した情報を元にEmployeeクラスのオブジェクト生成
				.build();
	}
	
	//ItemWriter用メソッド
	@Bean
	public JdbcBatchItemWriter<Employee> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Employee>() //ItemWriterのインスタンス生成
				//パラメータ付きのSQLを実行するためのオブジェクト
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				//SQL文をセット
				.sql("INSERT INTO EMPLOYEES (ID, NAME, DEPARTMENT) VALUES (EMPLOYEE_ID_SEQ.nextval, :name, :department)")
				//パラメータをセット
				.dataSource(dataSource)
				.build(); //オブジェクト生成
	}
	
	//step用メソッド
	@Bean
	public Step step1(JdbcBatchItemWriter<Employee> writer) {
		return stepBuilderFactory.get("step") //JobRepositoryにステップ名を登録し、ステップの設定を開始
				.<Employee, Employee> chunk(10) //<input, output>、何件のデータごとに変更を確定するか
				.reader(reader()) //ItemReaderのオブジェクト指定
				.processor(processor()) //ItemProcesserのオブジェクト指定
				.writer(writer) //ItemWriterのオブジェクト指定
				.build(); //Step型のオブジェクト生成
	}
	
	//Job実装メソッド
	@Bean
	public Job importEmployeeJob(Step step1) {
		return jobBuilderFactory.get("importEmployeeJob") //Jobリポジトリにジョブ名を登録、ジョブ設定開始
				.incrementer(new RunIdIncrementer()) //ジョブの実行IDを内部的にインクリメントするため
				.flow(step1) //step1を実行
				.end() //ジョブを終了
				.build(); //Job型のオブジェクトを生成
	}

}
