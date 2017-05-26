package com.mmt.automation.datareader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelDataReader {
	private static String excelFilePath = "/Users/MMT6189/venu/testcases.xlsx";
	private static String testScenarioIdentifier = "Test Scenario";
	
	public static void main(String[] args) {
		Map<String, List<Map<String, Map<String, Map<String, String>>>>> testScenarioMap = (new ExcelDataReader()).getTestScenarios();
//		for(String sheet : testScenarioMap.keySet()) {
//			List<Map<String, Map<String, Map<String, String>>>> testScenarios = testScenarioMap.get(sheet);
//			System.out.println("sheet:"+sheet);
//			for(Map<String, Map<String, Map<String, String>>> scenario : testScenarios) {
//				for(String testCaseId : scenario.keySet()) {
//					Map<String, Map<String, String>> testCase = scenario.get(testCaseId);
//					System.out.println("testCaseId:"+testCaseId);
//					for(String criteria : testCase.keySet()) {
//						System.out.println("criteria:"+criteria);
//						Map<String, String> criteriaFields = testCase.get(criteria);
//							for(String key : criteriaFields.keySet()) {
//								System.out.println(key+":"+criteriaFields.get(key));
//							}
//					}
//				}
//			}
//		}
	}
	
	public Map<String, List<Map<String, Map<String, Map<String, String>>>>> getTestScenarios() {
		Map<String, List<Map<String, Map<String, Map<String, String>>>>> testScenarioMap = new HashMap<>();
		FileInputStream inputStream = null;
		Workbook workbook = null;
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			workbook = new XSSFWorkbook(inputStream);
			for(int i=0;i<workbook.getNumberOfSheets();i++) {
				Sheet testSheet = workbook.getSheet(workbook.getSheetName(i));
				List<Map<String, Map<String, Map<String, String>>>> testScenarios = getScenariosInSheet(testSheet);
				testScenarioMap.put(workbook.getSheetName(i), testScenarios);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(inputStream != null)
					inputStream.close();
				if(workbook != null)
					workbook.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return testScenarioMap;
	}
	private List<Map<String, Map<String, Map<String, String>>>> getScenariosInSheet(Sheet sheet) {
		int counter = 0;
		List<String> criterias = new ArrayList<>();
		List<String> columns = new ArrayList<>();
		List<Map<String,Map<String, Map<String,String>>>> scenarios = new ArrayList<>();
		for(Row entry : sheet) {
			Map<String, Map<String, String>> criteriaMap = new HashMap<>();
			HashMap<String, Map<String, Map<String, String>>> testScenario = new HashMap<>();
			String rowIdentifier = null;
			String criteria = null;
			String column = null;
			if(counter == 0) {
				for(Cell cell : entry) {
					criterias.add(cell.getStringCellValue());	
				}
			} else if(counter == 1) {
				for(Cell cell : entry) {
					columns.add(cell.getStringCellValue());	
				}
			}
			else {
				int columnIndex = 0;
				for(Cell cell : entry) {
					if(!(StringUtils.isEmpty(criterias.get(columnIndex))))
						criteria = criterias.get(columnIndex);
					if(!(StringUtils.isEmpty(columns.get(columnIndex))))
						column = columns.get(columnIndex);
					else
						column = criteria;
					if(testScenarioIdentifier.equalsIgnoreCase(criteria)) {
						rowIdentifier = cell.getStringCellValue();
					} else {
						Map<String, String> criteriaEntry = criteriaMap.get(criteria);
						if(criteriaEntry == null) {
							criteriaEntry = new HashMap<>();
							criteriaMap.put(criteria, criteriaEntry);
						}
						Map<String, String> fieldMap = new HashMap<>();
						fieldMap.put(column, cell.getStringCellValue());
						criteriaEntry.putAll(fieldMap);
					}
					columnIndex += 1;
				}
				testScenario.put(rowIdentifier, criteriaMap);
				scenarios.add(testScenario);
			}
			counter += 1;
		}
		return scenarios;
	}

}
