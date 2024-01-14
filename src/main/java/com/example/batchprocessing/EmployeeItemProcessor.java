package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {
	
	//ログ出力用のオブジェクト
	private static final Logger log = LoggerFactory.getLogger(EmployeeItemProcessor.class);
	
	//処理実装
	@Override
	public Employee process(final Employee employee) throws Exception {
		//名前を大文字に
		final String name = employee.getName().toUpperCase();
		
		//フィールドを差し替えた新しいemployeeオブジェクト
		final Employee transformedEmployee = new Employee(name, employee.getDepartment());
		
		//差分をログに出力
		log.info("変換結果 (" + employee + ") => (" + transformedEmployee + ")");
		
		//変換後のオブジェクトを返却
		return transformedEmployee;
	}
}