package zt.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;


public class BlockingQueueTest {

	private int count;

	public BlockingQueueTest(int count) {
		super();
		this.count = count;
	}

	public static void main(String[] args) throws InterruptedException {
		BlockingDeque<BlockingQueueTest> blockingDeque = new LinkedBlockingDeque<BlockingQueueTest>();

		BlockingQueueTest queue = null;
		for (int i = 1; i < 10000; i++) {
			queue = new BlockingQueueTest(i);
			
				blockingDeque.put(queue);
				
			if (blockingDeque.size() != i) {
				System.err.println(i);
				System.err.println(blockingDeque.size());
			}
//			System.err.println(blockingDeque.take());
		}
//		Thread.sleep(500);
		System.err.println(blockingDeque.size() + "名称 " + blockingDeque);
	}
	
	public static String exportToExcel(byte[] templateFile, HashMap data)throws Exception  {

		InputStream input = new ByteArrayInputStream(templateFile);

//		Workbook create = WorkbookFactory.create(input);

//		int iSheetCnt = wb.getNumberOfSheets();
//		for (int iSheet = 0; iSheet < iSheetCnt; iSheet++) {
//			fillSheet(wb.getSheetAt(iSheet), data);
//
//		}
//		input.close();
		return null;
	}
	
	
}
